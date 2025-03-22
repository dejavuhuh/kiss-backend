package kiss.authentication

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.gt
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class SessionRepository(val sql: KSqlClient) {

    fun get(token: UUID): Int? {
        return sql.createQuery(Session::class) {
            where(table.token eq token)
            where(table.expiredTime gt Instant.now())
            select(table.userId)
        }.fetchOneOrNull()
    }

    fun set(token: UUID, userId: Int, expiration: Duration) {
        sql.insert(Session {
            this.token = token
            this.userId = userId
            this.expiredTime = Instant.now().plus(expiration.toJavaDuration())
        })
    }

    fun delete(token: UUID) {
        sql.deleteById(Session::class, token)
    }
}
