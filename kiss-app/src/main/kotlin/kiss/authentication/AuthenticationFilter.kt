package kiss.authentication

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val log = KotlinLogging.logger {}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class AuthenticationFilter(val sessionRepository: SessionRepository) : OncePerRequestFilter() {

    val whiteList = listOf(
        "/sign-in",
        "/sign-up",
        "/sign-out",
        "/ts.zip",
        "/favicon.ico",
        "/feishu/authorize",
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // 白名单
        if (whiteList.contains(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val authorization = request.getHeader("Authorization")
        if (authorization == null) {
            unauthorized("Authorization header is missing", response)
            return
        }

        if (!authorization.startsWith("Bearer ")) {
            unauthorized("Authorization header is invalid", response)
            return
        }

        val token = authorization.substring(7)
        val session = sessionRepository.get(token) ?: run {
            unauthorized("Token is invalid", response)
            return
        }

        // 会话续期
        sessionRepository.tryRenew(session)

        MDC.put("sessionId", session.id.toString())
        CurrentUserIdHolder.set(session.user.id)

        log.debug { "HTTP请求开始" }
        try {
            filterChain.doFilter(request, response)
            log.debug { "HTTP请求完成" }
        } finally {
            CurrentUserIdHolder.remove()
            MDC.remove("sessionId")
        }
    }

    private fun unauthorized(
        message: String,
        response: HttpServletResponse,
    ) {
        log.info { "认证失败：$message" }
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "text/plain"
        response.writer.write(message)
    }
}
