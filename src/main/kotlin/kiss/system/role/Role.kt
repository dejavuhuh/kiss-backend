package kiss.system.role

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import kiss.system.permission.Permission
import kiss.system.user.User
import org.babyfish.jimmer.sql.Entity
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

    @ManyToMany(mappedBy = "roles")
    val users: List<User>
}