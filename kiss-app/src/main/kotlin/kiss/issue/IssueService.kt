package kiss.issue

import kiss.issue.dto.IssueInput
import kiss.issue.dto.IssueSpecification
import kiss.jimmer.insertOnly
import kiss.jimmer.updateOnly
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 问题管理
 */
@Transactional
@RestController
@RequestMapping("/issues")
class IssueService(val sql: KSqlClient) {

    /**
     * 上报问题
     */
    @PostMapping
    fun report(@RequestBody input: IssueInput) {
        sql.insertOnly(input)
    }

    /**
     * 查询问题详情
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): @FetchBy("GET") Issue {
        return sql.findOneById(GET, id)
    }

    /**
     * 查询问题列表
     */
    @GetMapping
    fun list(
        @RequestParam pageIndex: Int,
        @RequestParam pageSize: Int,
        specification: IssueSpecification,
    ): Page<@FetchBy("LIST_ITEM") Issue> = sql.createQuery(Issue::class) {
        where(specification)
        orderBy(table.id.desc())
        select(table.fetch(LIST_ITEM))
    }.fetchPage(pageIndex, pageSize)

    /**
     * 查询可关联的问题列表
     */
    @GetMapping("/{id}/relatable")
    fun relatable(@PathVariable id: Int): List<@FetchBy("RELATABLE") Issue> {
        return sql.executeQuery(Issue::class) {
            where(table.id ne id)
            where(table.relatedToId.isNull())
            where(table.state eq IssueState.OPEN)
            select(table.fetch(RELATABLE))
        }
    }

    /**
     * 关联问题
     */
    @PutMapping("/{id}/relateTo")
    fun relateTo(@PathVariable id: Int, @RequestParam relatedToId: Int) {
        sql.updateOnly(Issue {
            this.id = id
            this.relatedToId = relatedToId
        })
    }

    /**
     * 取消关联问题
     */
    @DeleteMapping("/{id}/unRelate")
    fun unRelate(@PathVariable id: Int) {
        sql.updateOnly(Issue {
            this.id = id
            this.relatedToId = null
        })
    }

    companion object {
        val LIST_ITEM = newFetcher(Issue::class).by {
            title()
            createdTime()
            creator {
                displayName()
            }
        }
        val GET = newFetcher(Issue::class).by {
            allScalarFields()
            creator {
                displayName()
            }
            relatedTo()
            relatedFrom()
        }
        val RELATABLE = newFetcher(Issue::class).by {
            title()
            creator {
                displayName()
            }
        }
    }
}
