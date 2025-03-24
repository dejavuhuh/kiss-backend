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

    @Key
    @ManyToOne
    val parent: Permission?

    @OneToMany(mappedBy = "parent")
    val children: List<Permission>

    @ManyToMany(mappedBy = "permissions")
    val roles: List<Role>
}

enum class PermissionType {
    DIRECTORY,
    PAGE,
    BUTTON,
}
