package kiss.web.trace

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

private val log = KotlinLogging.logger {}

@Aspect
class LoggingAspect {

    lateinit var objectMapper: ObjectMapper

    fun configure(objectMapper: ObjectMapper) {
        this.objectMapper = objectMapper
    }

    @Pointcut(
        """
            @annotation(org.springframework.web.bind.annotation.GetMapping) ||
            @annotation(org.springframework.web.bind.annotation.PostMapping) ||
            @annotation(org.springframework.web.bind.annotation.PutMapping) ||
            @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
            @annotation(org.springframework.web.bind.annotation.PatchMapping)
    """
    )
    fun restEndpoint() {
    }

    @Before("restEndpoint() && execution(* *(..))")
    fun beforeExecution(joinPoint: JoinPoint) {
        log.debug {
            val methodSignature = joinPoint.signature as MethodSignature

            val className = methodSignature.declaringTypeName
            val methodName = methodSignature.name

            val methodArgs = mutableMapOf<String, Any?>()
            for ((index, arg) in joinPoint.args.withIndex()) {
                if (arg is HttpServletRequest || arg is HttpServletResponse) {
                    continue
                }
                val argName = methodSignature.parameterNames[index]
                methodArgs[argName] = arg
            }

            val serializedArgs = objectMapper.writeValueAsString(methodArgs)

            "进入REST端点|${className}.${methodName}|入参：$serializedArgs"
        }
    }
}
