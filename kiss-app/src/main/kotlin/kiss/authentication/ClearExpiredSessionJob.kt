package kiss.authentication

import kiss.job.JobDescription
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.le
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@JobDescription(
    title = "清理过期会话",
    cron = "0 0 3 * * ?" // 每天凌晨3点执行
)
@Component
@Transactional
class ClearExpiredSessionJob(val sql: KSqlClient) : Job {

    override fun execute(context: JobExecutionContext) {
        val sessions = sql.executeQuery(Session::class) {
            where(table.expiredTime le Instant.now())
            select(table.fetchBy {
                user()
            })
        }

        val histories = sessions.map {
            SessionHistory {
                id = it.id
                user = it.user
                reason = HistoryReason.EXPIRED
            }
        }

        sql.entities.saveEntities(histories) {
            setMode(SaveMode.INSERT_ONLY)
        }
        sql.deleteByIds(Session::class, sessions.map { it.id })
    }
}
