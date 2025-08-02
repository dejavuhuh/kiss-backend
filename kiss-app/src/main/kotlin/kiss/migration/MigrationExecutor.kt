package kiss.migration

import kiss.infrastructure.jimmer.insertOnly
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.max
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Order(2)
@Profile("!test")
@Component
@Transactional
class MigrationExecutor(
    val executors: List<Migration>,
    val sql: KSqlClient,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val maxVersion = sql.createQuery(MigrationHistory::class) {
            select(max(table.version))
        }.fetchOne() ?: 0

        for (executor in executors) {
            if (executor.version > maxVersion) {
                executor.migrate()
                sql.insertOnly(MigrationHistory {
                    version = executor.version
                })
            }
        }
    }
}
