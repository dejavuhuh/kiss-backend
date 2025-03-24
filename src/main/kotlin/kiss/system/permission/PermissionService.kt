package kiss.system.permission

import kiss.system.permission.dto.PermissionInput
import kiss.system.role.Role
import kiss.system.role.by
import kiss.system.role.id
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.ast.table.source
import org.babyfish.jimmer.sql.kt.ast.table.target
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

    @GetMapping("/{id}/roles")
    fun roles(@PathVariable id: Int): List<@FetchBy("BINDABLE_ROLE") Role> {
        return sql.executeQuery(Role::class) {
            where(table.id valueIn subQueries.forList(Permission::roles) {
                where(table.source.id eq id)
                select(table.target.id)
            })
            select(table.fetch(BINDABLE_ROLE))
        }
    }

    companion object {
        val LIST = newFetcher(Permission::class).by {
            allScalarFields()
            `children*`()
        }
        val BINDABLE_ROLE = newFetcher(Role::class).by {
            name()
        }
    }
}
