package kiss.authentication

import kiss.system.user.User
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.OneToOne
import java.time.Instant

@Entity
interface Session {

    @Id
    val token: String

    @OneToOne
    val user: User

    val expiredTime: Instant
}