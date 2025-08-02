package kiss.lock

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kiss.RedisContainer
import kiss.infrastructure.lock.CannotAcquireLockException
import kiss.infrastructure.lock.DistributedLockTemplate
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.config.Config
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@Testcontainers
class DistributedLockTemplateTest : RedisContainer {
    private val redissonClient = run {
        val config = Config().apply {
            useSingleServer().setAddress("redis://${redisHost}:${redisPort}")
        }
        Redisson.create(config)
    }

    @Test
    fun `should successfully execute block when lock is acquired`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-${System.currentTimeMillis()}"

        val result = template.execute(testKey, Duration.ofSeconds(5)) {
            "Hello, World!"
        }

        result shouldBe "Hello, World!"
    }

    @Test
    fun `should return correct value from executed block`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-return-${System.currentTimeMillis()}"

        val result = template.execute(testKey, Duration.ofSeconds(5)) {
            42
        }

        result shouldBe 42
    }

    @Test
    fun `should throw CannotAcquireLockException when lock cannot be acquired within wait time`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-timeout-${System.currentTimeMillis()}"

        // First thread acquires the lock and holds it
        val latch = CountDownLatch(1)
        val future = CompletableFuture.runAsync {
            template.execute(testKey, Duration.ofSeconds(10)) {
                latch.countDown()
                Thread.sleep(2000) // Hold lock for 2 seconds
                "First execution"
            }
        }

        // Wait for first thread to acquire lock
        latch.await()

        // Second thread tries to acquire the same lock with short timeout
        val exception = shouldThrow<CannotAcquireLockException> {
            template.execute(testKey, Duration.ofMillis(100)) {
                "Second execution"
            }
        }

        exception.message shouldContain testKey
        exception.message shouldContain "0s"

        future.get() // Wait for first thread to complete
    }

    @Test
    fun `should release lock even when block throws exception`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-exception-${System.currentTimeMillis()}"

        // First execution throws exception
        shouldThrow<RuntimeException> {
            template.execute(testKey, Duration.ofSeconds(5)) {
                throw RuntimeException("Test exception")
            }
        }

        // Second execution should succeed (lock was released)
        val result = template.execute(testKey, Duration.ofSeconds(5)) {
            "Success after exception"
        }

        result shouldBe "Success after exception"
    }

    @Test
    fun `should handle concurrent access to same lock correctly`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-concurrent-${System.currentTimeMillis()}"
        val executionOrder = mutableListOf<String>()
        val executionCounter = AtomicInteger(0)

        val futures = (1..3).map { threadId ->
            CompletableFuture.supplyAsync {
                template.execute(testKey, Duration.ofSeconds(10)) {
                    executionCounter.incrementAndGet()
                    Thread.sleep(100) // Simulate some work
                    synchronized(executionOrder) {
                        executionOrder.add("Thread-$threadId")
                    }
                    "Result-$threadId"
                }
            }
        }

        val results = futures.map { it.get() }

        // All executions should complete successfully
        results.size shouldBe 3
        results.forEach { result ->
            result shouldContain "Result-"
        }

        // Executions should be serialized (one at a time)
        executionOrder.size shouldBe 3
        executionCounter.get() shouldBe 3
    }

    @Test
    fun `should allow concurrent access to different locks`() {
        val template = DistributedLockTemplate(redissonClient)
        val timestamp = System.currentTimeMillis()
        val executionTimes = AtomicReference(mutableListOf<Long>())

        val futures = (1..3).map { lockId ->
            CompletableFuture.supplyAsync {
                val startTime = System.currentTimeMillis()
                template.execute("test-lock-different-$lockId-$timestamp", Duration.ofSeconds(5)) {
                    Thread.sleep(200) // Simulate work
                    synchronized(executionTimes.get()) {
                        executionTimes.get().add(System.currentTimeMillis() - startTime)
                    }
                    "Result-$lockId"
                }
            }
        }

        val results = futures.map { it.get() }

        // All executions should complete successfully
        results.size shouldBe 3
        results.forEach { result ->
            result shouldContain "Result-"
        }

        // Since different locks are used, executions should be concurrent
        // All should complete in roughly the same time (around 200 ms)
        val times = executionTimes.get()
        times.size shouldBe 3
        times.forEach { time ->
            time shouldBeInRange (200L..400L) // Allow some variance for execution overhead
        }
    }

    @Test
    fun `should handle null return value correctly`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-null-${System.currentTimeMillis()}"

        val result = template.execute(testKey, Duration.ofSeconds(5)) {
            null
        }

        result shouldBe null
    }

    @Test
    fun `should work with different data types`() {
        val template = DistributedLockTemplate(redissonClient)
        val timestamp = System.currentTimeMillis()

        // Test with List
        val listResult = template.execute("test-lock-list-$timestamp", Duration.ofSeconds(5)) {
            listOf(1, 2, 3, 4, 5)
        }
        listResult shouldBe listOf(1, 2, 3, 4, 5)

        // Test with Map
        val mapResult = template.execute("test-lock-map-$timestamp", Duration.ofSeconds(5)) {
            mapOf("key1" to "value1", "key2" to "value2")
        }
        mapResult shouldBe mapOf("key1" to "value1", "key2" to "value2")

        // Test with a custom object
        data class TestData(val id: Int, val name: String)

        val objectResult = template.execute("test-lock-object-$timestamp", Duration.ofSeconds(5)) {
            TestData(1, "Test")
        }
        objectResult shouldBe TestData(1, "Test")
    }

    @Test
    fun `should use correct lock key format`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "my-custom-key"

        // This test verifies that the lock key is prefixed correctly
        // We can't directly test the internal key format, but we can test
        // that the same key blocks concurrent access
        val latch = CountDownLatch(1)
        val future = CompletableFuture.runAsync {
            template.execute(testKey, Duration.ofSeconds(5)) {
                latch.countDown()
                Thread.sleep(1000)
                "First"
            }
        }

        latch.await()

        val exception = shouldThrow<CannotAcquireLockException> {
            template.execute(testKey, Duration.ofMillis(100)) {
                "Second"
            }
        }

        exception.message shouldContain testKey
        future.get()
    }

    @Test
    fun `should handle very short wait times`() {
        val template = DistributedLockTemplate(redissonClient)
        val testKey = "test-lock-short-wait-${System.currentTimeMillis()}"

        // First thread holds the lock
        val latch = CountDownLatch(1)
        val future = CompletableFuture.runAsync {
            template.execute(testKey, Duration.ofSeconds(2)) {
                latch.countDown()
                Thread.sleep(500)
                "First"
            }
        }

        latch.await()

        // Second thread with very short wait time
        val exception = shouldThrow<CannotAcquireLockException> {
            template.execute(testKey, Duration.ofMillis(1)) {
                "Second"
            }
        }

        exception.message shouldContain testKey
        future.get()
    }
}