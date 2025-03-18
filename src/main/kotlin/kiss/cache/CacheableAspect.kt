package kiss.cache

import kiss.json.JsonSerializer
import kiss.redis.RedisClient
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class CacheableAspect {

    companion object {
        lateinit var redisClient: RedisClient
    }

    @Around("@annotation(Cacheable) && execution(* *(..))")
    fun intercept(joinPoint: ProceedingJoinPoint): Any? {
        val args = joinPoint.args
        val methodSignature = joinPoint.signature as MethodSignature

        val hashKey = methodSignature.toString()
        val hashField = args.joinToString("-")

        val cachedValue = redisClient.hashGet(hashKey, hashField)
        if (cachedValue != null) {
            val returnType = methodSignature.method.genericReturnType
            return JsonSerializer.deserialize(cachedValue, returnType)
        }

        val returnValue = joinPoint.proceed()
        val serializedValue = JsonSerializer.serialize(returnValue)
        redisClient.hashSet(hashKey, hashField, serializedValue)

        return returnValue
    }
}
