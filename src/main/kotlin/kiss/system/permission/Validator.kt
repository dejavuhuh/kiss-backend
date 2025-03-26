package kiss.system.permission

import kiss.system.role.id
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.ast.table.source
import org.babyfish.jimmer.sql.kt.ast.table.target
import org.springframework.stereotype.Component

@Component
class Validator(val sql: KSqlClient) {

    fun checkIfRolesAreBoundToPermission(roleIds: List<Int>, permissionId: Int) {
        val boundRoleIds = sql.queries.forList(Permission::roles) {
            where(table.source.id eq permissionId)
            where(table.target.id valueIn roleIds)
            select(table.target.id)
        }.execute()

        val unboundRoleIds = roleIds - boundRoleIds

        if (unboundRoleIds.isNotEmpty()) {
            throw IllegalStateException("Roles $unboundRoleIds are not bound to permission $permissionId")
        }
    }
}
