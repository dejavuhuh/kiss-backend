package kiss.cache

import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class LocalCacheTest {

    data class Generic<T>(val data: T)

    class Demo {
        @LocalCache(expireAfterWriteSeconds = 1)
        fun doSomething(key: String): Generic<String> {
            return internalCall(key)
        }

        fun internalCall(key: String): Generic<String> {
            return Generic("Hello, $key")
        }
    }

    @Test
    fun test() {
        val demo = spyk(Demo())
        val result1 = demo.doSomething("world")
        val result2 = demo.doSomething("world")

        verify(exactly = 1) {
            demo.internalCall(any())
        }
        result2.shouldBe(result1)

        demo.doSomething("world2")

        verify(exactly = 2) {
            demo.internalCall(any())
        }

        TimeUnit.SECONDS.sleep(1)
        demo.doSomething("world2")

        verify(exactly = 3) {
            demo.internalCall(any())
        }
    }
}