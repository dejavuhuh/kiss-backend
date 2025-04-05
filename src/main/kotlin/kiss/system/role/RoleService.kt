package kiss.system.role

import kiss.jimmer.insertOnly
import kiss.jimmer.updateOnly
import kiss.system.role.dto.RoleInput
import kiss.system.role.dto.RoleSpecification
import kiss.system.user.User
import kiss.system.user.UserFetchers
import kiss.system.user.createdTime
import kiss.system.user.dto.UserSpecification
import kiss.system.user.roles
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 角色服务
 */
@Transactional
@RestController
@RequestMapping("/roles")
@DefaultFetcherOwner(RoleFetchers::class)
class RoleService(val sql: KSqlClient) {

    /**
     * 列表查询
     *
     * @param specification 查询条件
     * @return 角色列表
     */
    @GetMapping
    fun list(specification: RoleSpecification): List<@FetchBy("LIST_ITEM") Role> {
        return sql.executeQuery(Role::class) {
            where(specification)
            orderBy(table.id.desc())
            select(table.fetch(RoleFetchers.LIST_ITEM))
        }
    }

    @GetMapping("/options")
    fun options(): List<@FetchBy("SIMPLE") Role> {
        return sql.executeQuery(Role::class) {
            select(table.fetch(RoleFetchers.SIMPLE))
        }
    }

    @GetMapping("/{id}/users")
    fun users(
        @PathVariable id: Int,
        specification: UserSpecification
    ): List<@FetchBy(value = "LIST_ITEM", ownerType = UserFetchers::class) User> {
        return sql.executeQuery(User::class) {
            where(specification)
            where(table.roles {
                this.id.eq(id)
            })
            orderBy(table.createdTime.desc())
            select(table.fetch(UserFetchers.LIST_ITEM))
        }
    }

    /**
     * 创建角色
     */
    @PostMapping
    fun create(@RequestBody input: RoleInput) {
        sql.insertOnly(input)
    }

    /**
     * 更新角色
     *
     * @param id 角色ID
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody input: RoleInput) {
        sql.updateOnly(input.toEntity { this.id = id })
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        sql.deleteById(Role::class, id)
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     */
    @DeleteMapping
    fun deleteBatch(@RequestParam ids: List<Int>) {
        sql.deleteByIds(Role::class, ids)
    }
}
