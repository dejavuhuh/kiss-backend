package kiss.system.permission

import kiss.system.permission.dto.PermissionInput
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.table.isNull
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            where(table.parent.isNull())
            orderBy(table.position)
            select(table.fetch(LIST))
        }
    }

    companion object {
        val LIST = newFetcher(Permission::class).by {
            allScalarFields()
            `children*` {
                filter {
                    orderBy(table.position)
                }
            }
        }
    }
}
