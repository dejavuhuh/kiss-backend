package kiss.business.e_commerce.product

import kiss.infrastructure.job.JobDescription
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@JobDescription(
    title = "刷新商品好评率",
    cron = "0 0 3 * * ?" // 每天凌晨3点执行
)
@Component
class SpuPositiveRatingRatioRefreshJob(
    val jdbcTemplate: JdbcTemplate,
) : Job {
    override fun execute(context: JobExecutionContext) {
        jdbcTemplate.update(
            "REFRESH MATERIALIZED VIEW CONCURRENTLY spu_positive_rating_ratio_mv"
        )
    }
}