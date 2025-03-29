package kiss.authentication

import org.babyfish.jimmer.sql.ast.tuple.Tuple2
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.gt
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class SessionRepository(val sql: KSqlClient) {

    fun get(token: String): Tuple2<Int, Int>? {
        return sql.createQuery(Session::class) {
            where(table.token eq token)
            where(table.expiredTime gt LocalDateTime.now())
            select(table.id, table.userId)
        }.fetchOneOrNull()
    }

    fun set(token: String, userId: Int, expiration: Duration) {
        sql.insert(Session {
            this.token = token
            this.userId = userId
            this.expiredTime = LocalDateTime.now().plus(expiration.toJavaDuration())
        })
    }

    fun delete(token: String) {
        sql.executeDelete(Session::class) {
            where(table.token eq token)
        }
    }
}
