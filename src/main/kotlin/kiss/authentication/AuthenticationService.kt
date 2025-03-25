package kiss.authentication

import kiss.system.user.*
import kiss.web.BusinessException
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.mindrot.jbcrypt.BCrypt
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.time.Duration.Companion.days

@Transactional
@RestController
class AuthenticationService(
    val sql: KSqlClient,
    val sessionRepository: SessionRepository,
) {

    val sessionExpiration = 7.days

    data class SignInRequest(
        val username: String,
        val password: String,
    )

    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: SignInRequest): String {
        val (id, password) = sql.createQuery(User::class) {
            where(table.username eq request.username)
            select(table.id, table.password)
        }.fetchOneOrNull() ?: throw BusinessException("用户名或密码错误")

        if (!BCrypt.checkpw(request.password, password)) {
            throw BusinessException("用户名或密码错误")
        }

        val token = UUID.randomUUID().toString()
        sessionRepository.set(token, id, sessionExpiration)

        return token
    }

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignInRequest) {
        sql.insert(User {
            username = request.username
            password = BCrypt.hashpw(request.password, BCrypt.gensalt())
        })
    }

    @PostMapping("/sign-out")
    fun signOut(@RequestHeader("Authorization") token: String) {
        sessionRepository.delete(token)
    }

    @GetMapping("/current-user")
    fun getCurrentUser(): @FetchBy("CURRENT_USER") User {
        return sql.createQuery(User::class) {
            where(table.id eq CurrentUserIdHolder.get())
            select(table.fetch(CURRENT_USER))
        }.fetchOne()
    }

    companion object {
        val CURRENT_USER = newFetcher(User::class).by {
            username()
        }
    }
}
