package kiss.web.authentication

import kiss.infrastructure.jimmer.insertOnly
import kiss.web.authentication.dto.SessionSpecification
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 会话管理
 */
@Transactional
@RestController
@RequestMapping("/sessions")
class SessionService(val sql: KSqlClient) {

    /**
     * 查询会话列表
     */
    @GetMapping
    fun list(specification: SessionSpecification): List<@FetchBy("LIST_ITEM") Session> {
        return sql.executeQuery(Session::class) {
            where(specification)
            orderBy(table.id.desc())
            select(table.fetch(LIST_ITEM))
        }
    }

    /**
     * 强制下线
     */
    @PostMapping("/{id}/kickOut")
    fun kickOut(@PathVariable id: Int) {
        val (id, userId) = sql.createQuery(Session::class) {
            where(table.id eq id)
            select(table.id, table.userId)
        }.fetchOne()

        sql.insertOnly(SessionHistory {
            this.id = id
            this.userId = userId
            this.reason = HistoryReason.KICK_OUT
        })

        sql.deleteById(Session::class, id)
    }

    companion object {
        val LIST_ITEM = newFetcher(Session::class).by {
            allScalarFields()
            token(false)
            user {
                displayName()
            }
        }
    }
}