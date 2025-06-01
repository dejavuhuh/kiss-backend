package kiss.trace

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

private val log = KotlinLogging.logger {}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TracingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId = request.getHeader("X-TraceID") ?: UUID.randomUUID().toString().replace("-", "")
        response.setHeader("X-TraceID", traceId)
        MDC.put("traceId", traceId)

        log.debug { "HTTP请求开始" }
        try {
            filterChain.doFilter(request, response)
            log.debug { "HTTP请求完成" }
        } finally {
            MDC.remove("traceId")
        }
    }
}