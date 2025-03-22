package kiss.authentication

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

class AuthenticationException(message: String) : Exception(message)

class AuthenticationInterceptor(val sessionRepository: SessionRepository) : HandlerInterceptor {

    val whiteList = listOf("/sign-in", "/sign-up")

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // 白名单
        if (whiteList.contains(request.requestURI)) {
            return true
        }

        val authorization = request.getHeader("Authorization")
        if (authorization == null) {
            throw AuthenticationException("Authorization header is missing")
        }

        if (!authorization.startsWith("Bearer ")) {
            throw AuthenticationException("Authorization header is invalid")
        }

        val token = authorization.substring(7)
        val userId = sessionRepository.get(UUID.fromString(token))
        if (userId == null) {
            throw AuthenticationException("Token is invalid")
        }

        CurrentUserIdHolder.set(userId)

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        CurrentUserIdHolder.remove()
    }
}