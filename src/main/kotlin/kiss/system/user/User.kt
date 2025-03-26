package kiss.system.user

import kiss.jimmer.BaseEntity
import kiss.system.role.Role
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "\"user\"")
interface User : BaseEntity {

    @Key
    val username: String

    val password: String

    @ManyToMany
    val roles: List<Role>
}