package kiss.system.user

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "\"user\"")
interface User : BaseEntity {

    @Key
    val username: String

    val password: String
}