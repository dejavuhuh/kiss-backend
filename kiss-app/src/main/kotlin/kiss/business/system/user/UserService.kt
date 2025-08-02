package kiss.business.system.user

import kiss.business.system.user.dto.UserSpecification
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 用户管理
 */
@Transactional
@RestController
@RequestMapping("/users")
@DefaultFetcherOwner(UserFetchers::class)
class UserService(val sql: KSqlClient) {

    /**
     * 查询用户列表
     */
    @GetMapping
    fun list(specification: UserSpecification): List<@FetchBy("LIST_ITEM") User> {
        return sql.executeQuery(User::class) {
            where(specification)
            orderBy(table.createdTime.desc())
            select(table.fetch(UserFetchers.LIST_ITEM))
        }
    }

    /**
     * 分配角色
     */
    @PutMapping("/{id}/assignRoles")
    fun assignRoles(@PathVariable id: Int, @RequestBody roleIds: List<Int>) {
        sql.entities.save(User {
            this.id = id
            this.roleIds = roleIds
        })
    }
}