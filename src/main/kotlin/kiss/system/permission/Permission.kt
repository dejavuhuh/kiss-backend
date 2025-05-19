package kiss.system.permission

import kiss.jimmer.BaseEntity
import kiss.system.role.Role
import org.babyfish.jimmer.sql.*

@Entity
interface Permission : BaseEntity {

    val type: PermissionType

    @Key
    val code: String

    val name: String

    @ManyToOne
    val parent: Permission?

    @IdView
    val parentId: Int?

    @OneToMany(mappedBy = "parent")
    val children: List<Permission>

    @ManyToMany(mappedBy = "permissions")
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Int>

    @OneToMany(mappedBy = "permission")
    val auditLogs: List<PermissionAuditLog>
}

enum class PermissionType {
    DIRECTORY,
    PAGE,
    BUTTON,
}
