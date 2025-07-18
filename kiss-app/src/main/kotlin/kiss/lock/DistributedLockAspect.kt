package kiss.lock

import io.github.oshai.kotlinlogging.KotlinLogging
import kiss.web.BusinessException
import org.aspectj.lang.ProceedingJoinPoint
import java.time.Duration

private val log = KotlinLogging.logger {}

//@Aspect
class DistributedLockAspect {

    lateinit var distributedLockTemplate: DistributedLockTemplate

    //    @Around("@annotation(DistributedLock) && execution(* *(..))")
    fun intercept(joinPoint: ProceedingJoinPoint, annotation: DistributedLock): Any? {
        val parser = KeyExpressionParser(joinPoint)
        val lockKey = parser.parse(annotation.keyExpression)
        return try {
            distributedLockTemplate.execute(
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
