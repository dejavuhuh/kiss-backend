package kiss.cache

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kiss.infrastructure.cache.Cacheable
import kiss.infrastructure.cache.CacheableAspect
import kiss.infrastructure.json.JsonSerializer
import kiss.infrastructure.redis.RedisClient
import org.junit.jupiter.api.Test

class CacheableAspectTest {

    @Test
    fun test() {

        data class Generic<T>(val data: T)

        class Demo {
            @Cacheable
            fun doSomething(key: String): Generic<String> {
                return Generic("Hello, $key")
            }
        }

        val redisClient = mockk<RedisClient>()
        every { redisClient.hashGet(any(), "World") } returnsMany listOf(
            null,
            JsonSerializer.serialize(Generic("Hello, World"))
        )
        every { redisClient.hashSet(any(), "World", any()) } returns Unit

        CacheableAspect.redisClient = redisClient

        val demo = Demo()
        val firstResult = demo.doSomething("World")

        verify(exactly = 1) { redisClient.hashGet(any(), "World") }
        verify(exactly = 1) { redisClient.hashSet(any(), "World", any()) }

        val secondResult = demo.doSomething("World")

        verify(exactly = 2) { redisClient.hashGet(any(), "World") }
        verify(exactly = 1) { redisClient.hashSet(any(), "World", any()) }

        secondResult.shouldBe(firstResult)
    }

}

