package kiss.system.user

import kiss.system.user.dto.UserSpecification
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserService(val sql: KSqlClient) {

    @GetMapping
    fun list(
        @RequestParam pageIndex: Int,
        @RequestParam pageSize: Int,
        @ModelAttribute specification: UserSpecification,
    ): Page<@FetchBy("LIST") User> {
        return sql.createQuery(User::class) {
            where(specification)
            orderBy(table.createdTime.desc())
            select(table.fetch(LIST))
        }.fetchPage(pageIndex, pageSize)
    }

    companion object {
        val LIST = newFetcher(User::class).by {
            allScalarFields()
            password(false)
        }
    }
}