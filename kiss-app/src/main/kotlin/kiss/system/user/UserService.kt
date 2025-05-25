package kiss.system.user

import kiss.system.user.dto.UserSpecification
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/users")
@DefaultFetcherOwner(UserFetchers::class)
class UserService(val sql: KSqlClient) {

    @GetMapping
    fun list(specification: UserSpecification): List<@FetchBy("LIST_ITEM") User> {
        return sql.executeQuery(User::class) {
            where(specification)
            orderBy(table.createdTime.desc())
            select(table.fetch(UserFetchers.LIST_ITEM))
        }
    }

    @PutMapping("/{id}/assignRoles")
    fun assignRoles(@PathVariable id: Int, @RequestBody roleIds: List<Int>) {
        sql.entities.save(User {
            this.id = id
            this.roleIds = roleIds
        })
    }
}