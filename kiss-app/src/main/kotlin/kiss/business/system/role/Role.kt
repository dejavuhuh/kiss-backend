package kiss.business.system.role

import kiss.infrastructure.jimmer.BaseEntity
import kiss.infrastructure.jimmer.Creator
import kiss.business.system.permission.Permission
import kiss.business.system.user.User
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToMany

/**
 * 角色
 */
@Entity
interface Role : BaseEntity, Creator {

    /**
     * 角色名称
     */
    @Key
    val name: String

    /**
     * 角色描述
     */
    val description: String?

    @ManyToMany
    val permissions: List<Permission>

    @IdView("permissions")
    val permissionIds: List<Int>

    @ManyToMany(mappedBy = "roles")
    val users: List<User>

    @IdView("users")
    val userIds: List<Int>
}