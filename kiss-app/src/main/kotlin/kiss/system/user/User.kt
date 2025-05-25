package kiss.system.user

import kiss.jimmer.BaseEntity
import kiss.system.role.Role
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "\"user\"")
interface User : BaseEntity {

    val displayName: String

    @ManyToMany
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Int>
}