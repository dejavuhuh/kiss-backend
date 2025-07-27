package kiss.system.api

import kiss.job.JobDescription
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@JobDescription(
    title = "刷新用户接口权限",
    cron = "0 * * * * ?" // 每分钟执行一次
)
@Component
class UserApiPermissionsRefreshJob(
    val jdbcTemplate: JdbcTemplate,
) : Job {
    override fun execute(context: JobExecutionContext) {
        jdbcTemplate.update(
            "REFRESH MATERIALIZED VIEW CONCURRENTLY user_api_permissions_mv"
        )
    }
}