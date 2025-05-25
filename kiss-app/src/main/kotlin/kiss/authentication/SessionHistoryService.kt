package kiss.authentication

import kiss.authentication.dto.SessionHistorySpecification
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController
@RequestMapping("/session-histories")
class SessionHistoryService(val sql: KSqlClient) {

    @GetMapping
    fun list(
        @RequestParam pageIndex: Int,
        @RequestParam pageSize: Int,
        specification: SessionHistorySpecification
    ): Page<SessionHistory> {
        return sql.createQuery(SessionHistory::class) {
            where(specification)
            select(table.fetch(LIST_ITEM))
        }.fetchPage(pageIndex, pageSize)
    }

    companion object {
        val LIST_ITEM = newFetcher(SessionHistory::class).by {
            allScalarFields()
            user {
                displayName()
            }
        }
    }
}
