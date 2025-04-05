package kiss.authentication

import jakarta.servlet.http.HttpServletRequest
import kiss.jimmer.insertOnly
import kiss.system.role.Role
import kiss.system.role.by
import kiss.system.role.users
import kiss.system.user.*
import kiss.web.BusinessException
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.mindrot.jbcrypt.BCrypt
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
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
        sql.insertOnly(User {
            username = request.username
            password = BCrypt.hashpw(request.password, BCrypt.gensalt())
        })
    }

    @PostMapping("/sign-out")
    fun signOut(request: HttpServletRequest) {
        val token = request.getHeader("Authorization").substring(7)
        val (id, userId) = sessionRepository.get(token) ?: return

        sql.insertOnly(SessionHistory {
            this.id = id
            this.userId = userId
            this.reason = HistoryReason.SIGN_OUT
        })

        sql.deleteById(Session::class, id)
    }

    @GetMapping("/current-user")
    fun getCurrentUser(): @FetchBy("CURRENT_USER") User {
        return sql.findOneById(CURRENT_USER, CurrentUserIdHolder.get())
    }

    @GetMapping("/current-user/roles")
    fun getCurrentUserRoles(): List<@FetchBy("CURRENT_USER_ROLE") Role> {
        return sql.executeQuery(Role::class) {
            where(table.users {
                id eq CurrentUserIdHolder.get()
            })
            select(table.fetch(CURRENT_USER_ROLE))
        }
    }

    companion object {
        val CURRENT_USER = newFetcher(User::class).by {
            username()
        }
        val CURRENT_USER_ROLE = newFetcher(Role::class).by {
            name()
        }
    }
}
