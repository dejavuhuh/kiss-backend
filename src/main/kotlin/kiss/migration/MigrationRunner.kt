package kiss.migration

import kiss.jimmer.insertOnly
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.max
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class MigrationRunner(
    val executors: List<MigrationExecutor>,
    val sql: KSqlClient,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val maxVersion = sql.createQuery(MigrationHistory::class) {
            select(max(table.version))
        }.fetchOne() ?: 0

        for (executor in executors) {
            if (executor.version > maxVersion) {
                executor.execute()
                sql.insertOnly(MigrationHistory {
                    version = executor.version
                })
            }
        }
    }
}
