package kiss.infrastructure.job

import jakarta.annotation.PostConstruct
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.web.bind.annotation.*

/**
 * 定时任务管理
 */
@RestController
@RequestMapping("/jobs")
class JobService(
    val scheduler: Scheduler,
    val jobs: List<Job>,
) {

    class JobView(jobDetail: JobDetail, trigger: CronTrigger) {
        val name: String = jobDetail.key.name
        val description: String = jobDetail.description
        val cron: String = trigger.cronExpression
    }

    @PostConstruct
    fun initializeJobs() {
        for (job in jobs) {
            @Suppress("UNCHECKED_CAST")
            val jobClass = AopProxyUtils.ultimateTargetClass(job) as Class<out Job>
            val jobDescription = jobClass.getAnnotation(JobDescription::class.java)
                ?: throw IllegalStateException("Job must be annotated with @JobDescription")
            val jobDetail = JobBuilder
                .newJob(jobClass)
                .withIdentity(jobClass.simpleName)
                .withDescription(jobDescription.title)
                .build()
            val trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(jobClass.simpleName)
                .startNow()
                .withSchedule(
                    CronScheduleBuilder.cronScheduleNonvalidatedExpression(jobDescription.cron)
                )
                .build()
            scheduler.scheduleJob(jobDetail, trigger)
        }
    }

    /**
     * 查询定时任务列表
     */
    @GetMapping
    fun list(): List<JobView> {
        return scheduler
            .getJobKeys(GroupMatcher.anyGroup())
            .map {
                val jobDetail = scheduler.getJobDetail(it)
                val trigger = scheduler
                    .getTriggersOfJob(it)
                    .filterIsInstance<CronTrigger>()
                    .first()
                JobView(jobDetail, trigger)
            }
    }

    /**
     * 触发定时任务
     */
    @PostMapping("/{name}/trigger")
    fun trigger(@PathVariable name: String) {
        scheduler.triggerJob(JobKey.jobKey(name))
    }
}