package kiss.business.system.user

import kiss.infrastructure.jimmer.BaseEntity
import kiss.business.system.role.Role
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.Table
import java.time.Instant

@Entity
@Table(name = "\"user\"")
interface User : BaseEntity {

    val displayName: String

    @ManyToMany
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Int>

    val lastActiveTime: Instant?
}