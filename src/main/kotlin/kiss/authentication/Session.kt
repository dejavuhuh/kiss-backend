package kiss.authentication

import kiss.system.user.User
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.OneToOne
import java.time.Instant
import java.util.*

@Entity
interface Session {

    @Id
    val token: UUID

    @OneToOne
    val user: User

    val expiredTime: Instant
}