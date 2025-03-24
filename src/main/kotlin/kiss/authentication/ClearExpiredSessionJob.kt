package kiss.authentication

import kiss.job.JobDescription
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.le
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import java.time.Instant

@JobDescription(
    title = "清理过期会话",
    cron = "0 0 3 * * ?" // 每天凌晨3点执行
)
@Component
class ClearExpiredSessionJob(val sql: KSqlClient) : Job {

    override fun execute(context: JobExecutionContext) {
        sql.executeDelete(Session::class) {
            where(table.expiredTime le Instant.now())
        }
    }
}
