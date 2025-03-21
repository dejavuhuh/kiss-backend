package kiss.cache

import kiss.json.JsonSerializer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature

open class CacheAspect {

    fun getCacheName(joinPoint: ProceedingJoinPoint): String {
        val methodSignature = joinPoint.signature as MethodSignature
        return methodSignature.toString()
    }

    fun getCacheKey(joinPoint: ProceedingJoinPoint): String {
        val args = joinPoint.args
        return when (args.size) {
            0 -> ""
            1 -> args[0].toString()
            else -> JsonSerializer.serialize(args)
        }
    }
}