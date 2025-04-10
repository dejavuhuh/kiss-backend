package kiss.system.user

import kiss.jimmer.BaseEntity
import kiss.system.role.Role
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "\"user\"")
interface User : BaseEntity {

    @Key
    val username: String

    val password: String

    @ManyToMany
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Int>
}