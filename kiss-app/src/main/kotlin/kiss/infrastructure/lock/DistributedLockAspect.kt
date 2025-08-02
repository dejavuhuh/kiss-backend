package kiss.infrastructure.lock

import io.github.oshai.kotlinlogging.KotlinLogging
import kiss.web.BusinessException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import java.time.Duration

private val log = KotlinLogging.logger {}

@Aspect
class DistributedLockAspect {

    companion object {
        lateinit var template: DistributedLockTemplate
    }

    @Around("@annotation(annotation) && execution(* *(..))")
    fun intercept(joinPoint: ProceedingJoinPoint, annotation: DistributedLock): Any? {
        val parser = KeyExpressionParser(joinPoint)
        val lockKey = parser.parse(annotation.keyExpression) ?: throw NullPointerException("Parsed lock key is null, expression: ${annotation.keyExpression}")
        return try {
            template.execute(
                key = lockKey,
                waitTime = Duration.ofSeconds(annotation.waitSeconds),
                block = joinPoint::proceed
            )
        } catch (ex: CannotAcquireLockException) {
            log.error { ex.message }
            throw BusinessException(annotation.errorMessage)
        }
    }
}
