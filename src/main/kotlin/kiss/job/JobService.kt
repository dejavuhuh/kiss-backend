package kiss.job

import jakarta.annotation.PostConstruct
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            val jobDescription = job::class.java.getAnnotation(JobDescription::class.java)
            val jobDetail = JobBuilder
                .newJob(job::class.java)
                .withIdentity(job::class.simpleName)
                .withDescription(jobDescription.title)
                .build()
            val trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(job::class.simpleName)
                .startNow()
                .withSchedule(
                    CronScheduleBuilder.cronScheduleNonvalidatedExpression(jobDescription.cron)
                )
                .build()
            scheduler.scheduleJob(jobDetail, trigger)
        }
    }

    @GetMapping
    fun list(): List<JobView> {
        return scheduler
            .getJobKeys(GroupMatcher.anyGroup())
            .map {
                val jobDetail = scheduler.getJobDetail(it)
                val triggers = scheduler.getTriggersOfJob(it)
                JobView(jobDetail, triggers[0] as CronTrigger)
            }

    }
}