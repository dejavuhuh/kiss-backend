package kiss.lock

import org.redisson.api.RedissonClient
import java.time.Duration
import java.util.concurrent.TimeUnit

class DistributedLockTemplate(val redissonClient: RedissonClient) {

    fun <R> execute(
        key: String,
        waitTime: Duration,
        block: () -> R,
    ): R {
        val lock = redissonClient.getLock("DISTRIBUTED_LOCK:$key")
        val acquired = lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS)
        if (!acquired) throw CannotAcquireLockException(key, waitTime)

        return try {
            block()
        } finally {
            lock.unlock()
        }
    }
}

class CannotAcquireLockException(key: String, waitTime: Duration) : Exception("Cannot acquire lock for $key within ${waitTime.toSeconds()}s")
