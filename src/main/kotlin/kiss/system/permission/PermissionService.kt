package kiss.system.permission

import kiss.jimmer.insertOnly
import kiss.system.permission.dto.PermissionInput
import kiss.system.role.Role
import kiss.system.role.RoleFetchers
import kiss.system.role.permissions
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/permissions")
@DefaultFetcherOwner(PermissionFetchers::class)
class PermissionService(val sql: KSqlClient) {

    @PostMapping
    fun create(@RequestBody input: PermissionInput) {
        sql.insertOnly(input)
    }

    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") Permission> {
        return sql.executeQuery(Permission::class) {
            where(table.parentId.isNull())
            select(table.fetch(PermissionFetchers.LIST_ITEM))
        }
    }

    @GetMapping("/{id}/roles")
    fun roles(@PathVariable id: Int): List<@FetchBy("SIMPLE", ownerType = RoleFetchers::class) Role> {
        return sql.executeQuery(Role::class) {
            where(table.permissions {
                this.id eq id
            })
            select(table.fetch(RoleFetchers.SIMPLE))
        }
    }

    @PostMapping("/{id}/bindRoles")
    fun bindRoles(@PathVariable id: Int, @RequestBody roleIds: List<Int>) {
        sql.entities.save(Permission {
            this.id = id
            this.roleIds = roleIds
        })
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        sql.deleteById(Permission::class, id)
    }
}
