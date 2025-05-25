package kiss.trace

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kiss.authentication.CurrentUserIdHolder
import kiss.authentication.SessionRepository
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

private val log = KotlinLogging.logger {}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TracingFilter(val sessionRepository: SessionRepository) : OncePerRequestFilter() {

    val whiteList = listOf("/sign-in", "/sign-up", "/ts.zip", "/favicon.ico", "/feishu/authorize")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId = request.getHeader("X-TraceId") ?: UUID.randomUUID().toString().replace("-", "")
        response.setHeader("X-TraceId", traceId)
        MDC.put("traceId", traceId)

        // 白名单
        if (whiteList.contains(request.requestURI)) {
            executeFilterChain(filterChain, request, response)
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
        val (id, userId) = sessionRepository.get(token) ?: run {
            unauthorized("Token is invalid", response)
            return
        }

        MDC.put("sessionId", id.toString())
        CurrentUserIdHolder.set(userId)

        executeFilterChain(filterChain, request, response)
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

    private fun executeFilterChain(
        filterChain: FilterChain,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        log.debug { "HTTP请求开始" }
        try {
            filterChain.doFilter(request, response)
            log.debug { "HTTP请求完成" }
        } finally {
            CurrentUserIdHolder.remove()
            MDC.remove("sessionId")
            MDC.remove("traceId")
        }
    }
}
