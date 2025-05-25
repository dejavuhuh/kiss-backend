package kiss.application

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import kiss.system.permission.Permission
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToMany

@Entity
interface PermissionApplication : BaseEntity, Creator {

    @ManyToMany
    val permissions: List<Permission>

    val reason: String
}