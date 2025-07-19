package kiss.lock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser

class KeyExpressionParser(private val joinPoint: ProceedingJoinPoint) {
    private val parser = SpelExpressionParser()

    fun parse(keyExpression: String): String? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val args = joinPoint.args

        // SpEL上下文
        val context = MethodBasedEvaluationContext(
            emptyMap<Unit, Unit>(),
            method,
            args,
            DefaultParameterNameDiscoverer()
        )

        val expression = parser.parseExpression(keyExpression)
        return expression.getValue(context, String::class.java)
    }
}