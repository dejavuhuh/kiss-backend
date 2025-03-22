package kiss.authentication

import kiss.job.JobDescription
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@JobDescription(
    title = "清理过期会话",
    cron = "0 0 3 * * ?" // 每天凌晨3点执行
)
@Component
class ClearExpiredSessionJob : Job {
    override fun execute(context: JobExecutionContext) {
        TODO("Not yet implemented")
    }
}