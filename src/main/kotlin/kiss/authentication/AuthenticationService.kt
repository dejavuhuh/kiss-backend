package kiss.authentication

import kiss.system.user.User
import kiss.system.user.id
import kiss.system.user.password
import kiss.system.user.username
import kiss.web.BusinessException
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.mindrot.jbcrypt.BCrypt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.time.Duration.Companion.days

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
    fun signIn(@RequestBody request: SignInRequest): UUID {
        val (id, password) = sql.createQuery(User::class) {
            where(table.username eq request.username)
            select(table.id, table.password)
        }.fetchOneOrNull() ?: throw BusinessException("用户名或密码错误")

        if (!BCrypt.checkpw(request.password, password)) {
            throw BusinessException("用户名或密码错误")
        }

        val token = UUID.randomUUID()
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
    fun signOut(@RequestHeader("Authorization") token: UUID) {
        sessionRepository.delete(token)
    }
}