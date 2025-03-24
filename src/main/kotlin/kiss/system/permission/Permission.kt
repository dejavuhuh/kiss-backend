package kiss.system.permission

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OneToMany

@Entity
interface Permission: BaseEntity {

    val type: PermissionType

    @Key
    val code: String

    val name: String

    val position: Int

    @Key
    @ManyToOne
    val parent: Permission?

    @OneToMany(mappedBy = "parent")
    val children: List<Permission>
}

enum class PermissionType {
    PAGE,
    BUTTON,
}
