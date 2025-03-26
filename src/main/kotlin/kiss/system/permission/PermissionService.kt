package kiss.system.permission

import kiss.system.permission.dto.PermissionInput
import kiss.system.role.id
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/permissions")
class PermissionService(val sql: KSqlClient) {

    @PostMapping
    fun create(@RequestBody input: PermissionInput) {
        sql.insert(input)
    }

    @GetMapping
    fun list(): List<@FetchBy("LIST") Permission> {
        return sql.executeQuery(Permission::class) {
            where(table.parentId.isNull())
            select(table.fetch(LIST))
        }
    }

    @PostMapping("/{id}/bindRoles")
    fun bindRoles(@PathVariable id: Int, @RequestBody roleIds: List<Int>) {
        sql.save(Permission {
            this.id = id
            this.roleIds = roleIds
        })
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        sql.deleteById(Permission::class, id)
    }

    companion object {
        val LIST = newFetcher(Permission::class).by {
            allScalarFields()
            parentId()
            roles({
                filter {
                    orderBy(table.id)
                }
            }) {
                name()
            }
            `children*`()
        }
        val BOUND_ROLES = newFetcher(Permission::class).by {
            roles {
                name()
            }
        }
    }
}
