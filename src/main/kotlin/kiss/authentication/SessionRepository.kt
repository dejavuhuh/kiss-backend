package kiss.authentication

import kiss.jimmer.insertOnly
import org.babyfish.jimmer.spring.SqlClients
import org.babyfish.jimmer.sql.ast.tuple.Tuple2
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

    fun get(token: String): Tuple2<Int, Int>? {
        return sql.createQuery(Session::class) {
            where(table.token eq token)
            where(table.expiredTime gt Instant.now())
            select(table.id, table.userId)
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
}
