package kiss.business.export

import kiss.web.authentication.CurrentUserIdHolder
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/export-tasks")
class ExportTaskService(val sql: KSqlClient) {

    @GetMapping
    fun list(@RequestParam scene: ExportTaskScene): List<@FetchBy("LIST_ITEM") ExportTask> {
        return sql.executeQuery(ExportTask::class) {
            where(table.scene eq scene)
            where(table.creatorId eq CurrentUserIdHolder.get())
            select(table.fetch((LIST_ITEM)))
        }
    }

    companion object {
        val LIST_ITEM = newFetcher(ExportTask::class).by {
            allScalarFields()
        }
    }
}
