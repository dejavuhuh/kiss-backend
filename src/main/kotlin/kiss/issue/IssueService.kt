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

@Transactional
@RestController
@RequestMapping("/issues")
class IssueService(val sql: KSqlClient) {

    @PostMapping
    fun report(@RequestBody input: IssueInput) {
        sql.insertOnly(input)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): @FetchBy("GET") Issue {
        return sql.findOneById(GET, id)
    }

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
     * 获取可关联的问题列表
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

    @PutMapping("/{id}/relateTo")
    fun relateTo(@PathVariable id: Int, @RequestParam relatedToId: Int) {
        sql.updateOnly(Issue {
            this.id = id
            this.relatedToId = relatedToId
        })
    }

    companion object {
        val LIST_ITEM = newFetcher(Issue::class).by {
            title()
            createdTime()
            creator {
                username()
            }
        }
        val GET = newFetcher(Issue::class).by {
            allScalarFields()
            creator {
                username()
            }
            relatedTo()
            relatedFrom()
        }
        val RELATABLE = newFetcher(Issue::class).by {
            title()
            creator {
                username()
            }
        }
    }
}
