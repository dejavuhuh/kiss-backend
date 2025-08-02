package kiss.web.authentication

import kiss.business.system.user.User
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.ManyToOne
import java.time.LocalDateTime

enum class HistoryReason {
    EXPIRED,
    SIGN_OUT,
    KICK_OUT,
}

@Entity
interface SessionHistory {

    @Id
    val id: Int

    @ManyToOne
    val user: User

    val reason: HistoryReason

    val createdTime: LocalDateTime
}