package kiss.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Target(AnnotationTarget.FUNCTION)
annotation class LocalCache(
    val expireAfterWriteSeconds: Long,
)

@Aspect
class LocalCacheAspect : CacheAspect() {

    val cacheMap = mutableMapOf<String, Cache<String, Any?>>()
    val lock = ReentrantLock()

    @Around("@annotation(localCache) && execution(* *(..))")
    fun intercept(joinPoint: ProceedingJoinPoint, localCache: LocalCache): Any? {
        val cache = getCache(joinPoint, localCache)
        val cacheKey = getCacheKey(joinPoint)
        return cache.get(cacheKey) {
            joinPoint.proceed()
        }
    }

    private fun getCache(joinPoint: ProceedingJoinPoint, localCache: LocalCache): Cache<String, Any?> {
        val cacheName = getCacheName(joinPoint)
        var cache = cacheMap[cacheName]
        if (cache == null) {
            lock.lock()
            try {
                cache = cacheMap[cacheName]
                if (cache == null) {
                    cache = Caffeine
                        .newBuilder()
                        .expireAfterWrite(localCache.expireAfterWriteSeconds, TimeUnit.SECONDS)
                        .build()
                    cacheMap[cacheName] = cache
                }
            } finally {
                lock.unlock()
            }
        }
        return cache
    }
}