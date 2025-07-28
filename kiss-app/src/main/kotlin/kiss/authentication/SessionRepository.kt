package kiss.authentication

import kiss.authentication.dto.UserSession
import kiss.jimmer.insertOnly
import kiss.jimmer.updateOnly
import org.babyfish.jimmer.spring.SqlClients
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.gt
import org.babyfish.jimmer.sql.runtime.DefaultExecutor
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class SessionRepository(ctx: ApplicationContext) {

    val sql by lazy {
        SqlClients.kotlin(ctx) {
            setExecutor(DefaultExecutor.INSTANCE)
        }
    }

    fun get(token: String): UserSession? {
        return sql.createQuery(Session::class) {
            where(table.token eq token)
            where(table.expiredTime gt Instant.now())
            select(table.fetch(UserSession::class))
        }.fetchOneOrNull()
    }

    fun create(userId: Int): String {
        val token = UUID.randomUUID().toString()
        sql.insertOnly(Session {
            this.token = token
            this.userId = userId
            this.expiredTime = Instant.now().plus(Duration.ofDays(7))
        })
        return token
    }

    fun tryRenew(session: UserSession) {
        val now = Instant.now()
        if (session.expiredTime <= now) {
            throw UnsupportedOperationException("Expired session cannot be renewed")
        }

        // 如果剩余时间大于10分钟，则不续期
        val leftTime = Duration.between(now, session.expiredTime)
        if (leftTime > Duration.ofMinutes(10)) {
            return
        }

        // 续30分钟
        sql.updateOnly(Session {
            id = session.id
            expiredTime = now + Duration.ofMinutes(30)
        })
    }
}
