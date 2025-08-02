package kiss.business.application

import kiss.infrastructure.jimmer.BaseEntity
import kiss.infrastructure.jimmer.Creator
import kiss.business.system.permission.Permission
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToMany

@Entity
interface PermissionApplication : BaseEntity, Creator {

    @ManyToMany
    val permissions: List<Permission>

    val reason: String
}