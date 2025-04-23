package kiss.authentication

import jakarta.servlet.http.HttpServletRequest
import kiss.system.user.*
import kiss.web.BusinessException
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
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
        val (userId, password) = sql.createQuery(Account::class) {
            where(table.username eq request.username)
            select(table.userId, table.password)
        }.fetchOneOrNull() ?: throw BusinessException("用户名或密码错误")

        if (!BCrypt.checkpw(request.password, password)) {
            throw BusinessException("用户名或密码错误")
        }

        val token = UUID.randomUUID().toString()
        sessionRepository.set(token, userId, sessionExpiration)

        return token
    }

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignInRequest) {
        sql.save(Account {
            username = request.username
            password = BCrypt.hashpw(request.password, BCrypt.gensalt())
            user {
                displayName = request.username
            }
        }, SaveMode.INSERT_ONLY, AssociatedSaveMode.APPEND)
    }

    @PostMapping("/sign-out")
    fun signOut(request: HttpServletRequest) {
        val token = request.getHeader("Authorization").substring(7)
        val (id, userId) = sessionRepository.get(token) ?: return

        sql.save(SessionHistory {
            this.id = id
            this.userId = userId
            this.reason = HistoryReason.SIGN_OUT
        }) {
            setMode(SaveMode.INSERT_ONLY)
        }

        sql.deleteById(Session::class, id)
    }

    @GetMapping("/current-user")
    fun getCurrentUser(): @FetchBy("CURRENT_USER") User {
        return sql.findOneById(CURRENT_USER, CurrentUserIdHolder.get())
    }

    companion object {
        val CURRENT_USER = newFetcher(User::class).by {
            roles {
                name()
                permissions {
                    code()
                }
            }
        }
    }
}
