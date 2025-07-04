package kiss.system.api

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kiss.authentication.CurrentUserIdHolder
import kiss.system.permission.roles
import kiss.system.role.users
import kiss.system.user.id
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

private val log = KotlinLogging.logger {}

/**
 * 接口权限过滤器
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
class ApiPermissionFilter(
    val sql: KSqlClient,
    val apiCollector: ApiCollector,
    val requestMappingHandlerMapping: RequestMappingHandlerMapping,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (apiCollector.ignoredApis.contains(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val handler = requestMappingHandlerMapping.getHandler(request)?.handler ?: run {
            filterChain.doFilter(request, response)
            return
        }

        if (handler !is HandlerMethod) {
            filterChain.doFilter(request, response)
            return
        }

        val controllerRequestMapping = handler.beanType.getAnnotation(RequestMapping::class.java)
            ?: throw IllegalStateException("Controller must be annotated with `@RequestMapping`")
        val methodRequestMapping = handler.getMethodAnnotation(RequestMapping::class.java)
            ?: throw IllegalStateException("Controller method must be annotated with `@RequestMapping`")

        val method = methodRequestMapping.method[0]
        val path = controllerRequestMapping.value[0] + (methodRequestMapping.value.firstOrNull() ?: "")

        val authorized = sql.createQuery(Api::class) {
            where(table.method eq method)
            where(table.path eq path)
            where += table.permissions {
                this.roles {
                    this.users {
                        this.id eq CurrentUserIdHolder.get()
                    }
                }
            }
            select(table)
        }.exists()

        if (!authorized) {
            log.error { "接口权限校验失败：${request.method} ${request.requestURI}" }
            response.status = HttpServletResponse.SC_FORBIDDEN
            return
        }

        filterChain.doFilter(request, response)
    }
}
