package kiss.authentication

import kiss.jimmer.BaseEntity
import kiss.system.user.User
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.OneToOne
import java.time.Instant

@Entity
interface Session : BaseEntity {

    @Key
    val token: String

    @OneToOne
    val user: User

    val expiredTime: Instant
}
