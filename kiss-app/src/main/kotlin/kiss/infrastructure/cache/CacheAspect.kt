package kiss.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature

open class CacheAspect {

    lateinit var objectMapper: ObjectMapper

    fun configure(objectMapper: ObjectMapper) {
        this.objectMapper = objectMapper
    }

    fun getCacheName(joinPoint: ProceedingJoinPoint): String {
        val methodSignature = joinPoint.signature as MethodSignature
        return methodSignature.toString()
    }

    fun getCacheKey(joinPoint: ProceedingJoinPoint): String {
        val args = joinPoint.args
        return when (args.size) {
            0 -> ""
            1 -> args[0].toString()
            else -> objectMapper.writeValueAsString(args)
        }
    }
}