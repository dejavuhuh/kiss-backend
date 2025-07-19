package kiss

import com.redis.testcontainers.RedisContainer
import org.testcontainers.junit.jupiter.Container

interface RedisContainer {
    companion object {
        @Container
        val redis = RedisContainer(
            RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG)
        ).also { it.start() }
    }

    val redisHost: String get() = redis.redisHost
    val redisPort: Int get() = redis.redisPort
}
