package kiss.lock

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kiss.RedisContainer
import kiss.infrastructure.lock.DistributedLock
import kiss.infrastructure.lock.DistributedLockAspect
import kiss.infrastructure.lock.DistributedLockTemplate
import kiss.web.BusinessException
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.config.Config
import org.springframework.expression.spel.SpelEvaluationException
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

@Testcontainers
class DistributedLockAnnotationTest : RedisContainer {
    private val template = run {
        val config = Config().apply {
            useSingleServer().setAddress("redis://${redisHost}:${redisPort}")
        }
        val redissonClient = Redisson.create(config)
        DistributedLockTemplate(redissonClient)
    }

    init {
        DistributedLockAspect.template = template
    }

    // Test service class with various @DistributedLock methods
    class TestService {

        @DistributedLock(keyExpression = "'simple-key'", waitSeconds = 5)
        fun simpleMethod(): String {
            return "simple result"
        }

        @DistributedLock(keyExpression = "#param", waitSeconds = 3)
        fun methodWithParameter(param: String): String {
            return "result for $param"
        }

        @DistributedLock(keyExpression = "#user.id", waitSeconds = 2)
        fun methodWithObjectParameter(user: User): String {
            return "result for user ${user.id}"
        }

        @DistributedLock(keyExpression = "#user.id + '-' + #action", waitSeconds = 1)
        fun methodWithComplexExpression(user: User, action: String): String {
            return "result for user ${user.id} action $action"
        }

        @DistributedLock(keyExpression = "#items.size()", waitSeconds = 2)
        fun methodWithCollectionExpression(items: List<String>): String {
            return "result for ${items.size} items"
        }

        @DistributedLock(keyExpression = "#user.getName()", waitSeconds = 2)
        fun methodWithMethodCall(user: User): String {
            return "result for user ${user.name}"
        }

        @DistributedLock(keyExpression = "'timeout-test'", waitSeconds = 1, errorMessage = "Custom timeout message")
        fun methodWithCustomErrorMessage(): String {
            Thread.sleep(2000) // Hold lock longer than timeout
            return "should not reach here"
        }

        @DistributedLock(keyExpression = "'exception-test'", waitSeconds = 5)
        fun methodThatThrowsException(): String {
            throw RuntimeException("Method exception")
        }

        @DistributedLock(keyExpression = "#a + '-' + #b + '-' + #c", waitSeconds = 3)
        fun methodWithMultipleParameters(a: String, b: Int, c: Boolean): String {
            return "result: $a-$b-$c"
        }
    }

    data class User(val id: String, val name: String)

    @Test
    fun `should execute method with simple lock key`() {
        val service = TestService()
        val result = service.simpleMethod()
        result shouldBe "simple result"
    }

    @Test
    fun `should execute method with parameter-based lock key`() {
        val service = TestService()
        val result = service.methodWithParameter("test-param")
        result shouldBe "result for test-param"
    }

    @Test
    fun `should execute method with object property lock key`() {
        val service = TestService()
        val user = User("user123", "John")
        val result = service.methodWithObjectParameter(user)
        result shouldBe "result for user user123"
    }

    @Test
    fun `should execute method with complex SpEL expression`() {
        val service = TestService()
        val user = User("user456", "Jane")
        val result = service.methodWithComplexExpression(user, "login")
        result shouldBe "result for user user456 action login"
    }

    @Test
    fun `should execute method with collection size expression`() {
        val service = TestService()
        val items = listOf("item1", "item2", "item3")
        val result = service.methodWithCollectionExpression(items)
        result shouldBe "result for 3 items"
    }

    @Test
    fun `should execute method with method call expression`() {
        val service = TestService()
        val user = User("user789", "Bob")
        val result = service.methodWithMethodCall(user)
        result shouldBe "result for user Bob"
    }

    @Test
    fun `should execute method with multiple parameters expression`() {
        val service = TestService()
        val result = service.methodWithMultipleParameters("test", 42, true)
        result shouldBe "result: test-42-true"
    }

    @Test
    fun `should throw BusinessException when lock cannot be acquired`() {
        // Create a service with the same lock key
        class ConcurrentTestService {
            @DistributedLock(keyExpression = "'concurrent-test'", waitSeconds = 1)
            fun holdLock(): String {
                Thread.sleep(2000) // Hold lock for 2 seconds
                return "first execution"
            }
        }

        val concurrentService = ConcurrentTestService()

        // First thread acquires the lock
        val future = CompletableFuture.runAsync {
            concurrentService.holdLock()
        }

        // Give first thread time to acquire lock
        Thread.sleep(100)

        // Second thread should fail to acquire lock and throw BusinessException
        val exception = shouldThrow<BusinessException> {
            concurrentService.holdLock()
        }

        exception.message shouldBe "操作频繁，请稍后重试"

        future.get() // Wait for first thread to complete
    }

    @Test
    fun `should throw BusinessException with custom error message`() {
        val service = TestService()

        // First thread holds the lock
        val future = CompletableFuture.runAsync {
            try {
                service.methodWithCustomErrorMessage()
            } catch (_: Exception) {
                // Expected to fail due to sleep
            }
        }

        // Give first thread time to acquire lock
        Thread.sleep(100)

        // Second thread should fail with a custom error message
        val exception = shouldThrow<BusinessException> {
            service.methodWithCustomErrorMessage()
        }

        exception.message shouldBe "Custom timeout message"

        future.get()
    }

    @Test
    fun `should release lock when method throws exception`() {
        val service = TestService()

        // First execution throws exception
        shouldThrow<RuntimeException> {
            service.methodThatThrowsException()
        }

        // Second execution should succeed (lock was released)
        shouldThrow<RuntimeException> {
            service.methodThatThrowsException()
        }
    }

    @Test
    fun `should handle concurrent access with different lock keys`() {
        val service = TestService()
        val executionCounter = AtomicInteger(0)

        val futures = (1..3).map { index ->
            CompletableFuture.supplyAsync {
                val result = service.methodWithParameter("key-$index")
                executionCounter.incrementAndGet()
                result
            }
        }

        val results = futures.map { it.get() }

        // All executions should complete successfully since they use different keys
        results.size shouldBe 3
        executionCounter.get() shouldBe 3
        results.forEachIndexed { index, result ->
            result shouldBe "result for key-${index + 1}"
        }
    }

    @Test
    fun `should handle null parameter in SpEL expression`() {
        class NullTestService {
            @DistributedLock(keyExpression = "#param ?: 'default'", waitSeconds = 2)
            fun methodWithNullableParameter(param: String?): String {
                return "result for ${param ?: "null"}"
            }
        }

        val service = NullTestService()

        // Test with null parameter
        val result1 = service.methodWithNullableParameter(null)
        result1 shouldBe "result for null"

        // Test with non-null parameter
        val result2 = service.methodWithNullableParameter("test")
        result2 shouldBe "result for test"
    }

    @Test
    fun `should handle empty collection in SpEL expression`() {
        val service = TestService()

        val emptyList = emptyList<String>()
        val result = service.methodWithCollectionExpression(emptyList)
        result shouldBe "result for 0 items"
    }

    @Test
    fun `should handle nested object properties in SpEL expression`() {
        data class Address(val city: String, val country: String)
        data class UserWithAddress(val id: String, val address: Address)

        class NestedTestService {
            @DistributedLock(keyExpression = "#user.address.city + '-' + #user.address.country", waitSeconds = 2)
            fun methodWithNestedProperty(user: UserWithAddress): String {
                return "result for ${user.address.city}, ${user.address.country}"
            }
        }

        val service = NestedTestService()
        val user = UserWithAddress("user1", Address("Beijing", "China"))
        val result = service.methodWithNestedProperty(user)
        result shouldBe "result for Beijing, China"
    }

    @Test
    fun `should handle arithmetic operations in SpEL expression`() {
        class ArithmeticTestService {
            @DistributedLock(keyExpression = "#a + #b", waitSeconds = 2)
            fun methodWithArithmetic(a: Int, b: Int): String {
                return "sum is ${a + b}"
            }
        }

        val service = ArithmeticTestService()
        val result = service.methodWithArithmetic(10, 20)
        result shouldBe "sum is 30"
    }

    @Test
    fun `should handle string operations in SpEL expression`() {
        class StringTestService {
            @DistributedLock(keyExpression = "#text.toUpperCase()", waitSeconds = 2)
            fun methodWithStringOperation(text: String): String {
                return "processed: $text"
            }
        }

        val service = StringTestService()
        val result = service.methodWithStringOperation("hello")
        result shouldBe "processed: hello"
    }

    @Test
    fun `should handle conditional expressions in SpEL`() {
        class ConditionalTestService {
            @DistributedLock(keyExpression = "#value > 10 ? 'high' : 'low'", waitSeconds = 2)
            fun methodWithConditional(value: Int): String {
                return "value is $value"
            }
        }

        val service = ConditionalTestService()

        val result1 = service.methodWithConditional(15)
        result1 shouldBe "value is 15"

        val result2 = service.methodWithConditional(5)
        result2 shouldBe "value is 5"
    }

    @Test
    fun `should throw ExpressionParseException for invalid SpEL expression`() {
        class InvalidExpressionService {
            @DistributedLock(keyExpression = "#nonExistentParam.invalidProperty", waitSeconds = 2)
            fun methodWithInvalidExpression(): String {
                return "should not reach here"
            }
        }

        val service = InvalidExpressionService()

        shouldThrow<SpelEvaluationException> {
            service.methodWithInvalidExpression()
        }
    }

    @Test
    fun `should handle very short wait time`() {
        class ShortWaitService {
            @DistributedLock(keyExpression = "'short-wait-test'", waitSeconds = 0, errorMessage = "Very short timeout")
            fun methodWithVeryShortWait(): String {
                Thread.sleep(100)
                return "completed"
            }
        }

        val service = ShortWaitService()

        // First thread holds the lock
        val future = CompletableFuture.runAsync {
            try {
                service.methodWithVeryShortWait()
            } catch (_: Exception) {
                // May fail due to short timeout
            }
        }

        Thread.sleep(50) // Give first thread time to acquire lock

        // Second thread should fail immediately
        val exception = shouldThrow<BusinessException> {
            service.methodWithVeryShortWait()
        }

        exception.message shouldBe "Very short timeout"
        future.get()
    }

    @Test
    fun `should handle return value types correctly`() {
        class ReturnTypeTestService {
            @DistributedLock(keyExpression = "'return-test'", waitSeconds = 2)
            fun methodReturningInt(): Int = 42

            @DistributedLock(keyExpression = "'return-test-list'", waitSeconds = 2)
            fun methodReturningList(): List<String> = listOf("a", "b", "c")

            @DistributedLock(keyExpression = "'return-test-null'", waitSeconds = 2)
            fun methodReturningNull(): String? = null

            @DistributedLock(keyExpression = "'return-test-object'", waitSeconds = 2)
            fun methodReturningObject(): User = User("123", "Test User")
        }

        val service = ReturnTypeTestService()

        service.methodReturningInt() shouldBe 42
        service.methodReturningList() shouldBe listOf("a", "b", "c")
        service.methodReturningNull() shouldBe null
        service.methodReturningObject() shouldBe User("123", "Test User")
    }

    @Test
    fun `should handle concurrent executions with same lock key properly`() {
        val results = mutableListOf<String>()

        class ConcurrentTestService {

            @DistributedLock(keyExpression = "'concurrent-execution'", waitSeconds = 10)
            fun synchronizedMethod(threadId: String) {
                Thread.sleep(100) // Simulate work
                synchronized(results) {
                    results.add(threadId)
                }
            }
        }

        val service = ConcurrentTestService()

        val futures = (1..3).map { threadId ->
            CompletableFuture.supplyAsync {
                service.synchronizedMethod("thread-$threadId")
            }
        }

        futures.map { it.get() }

        // The last result should contain all thread IDs (since they execute sequentially)
        results.size shouldBe 3
        results shouldContain "thread-1"
        results shouldContain "thread-2"
        results shouldContain "thread-3"
    }

    @Test
    fun `should work with inheritance and method overriding`() {
        open class BaseService {
            @DistributedLock(keyExpression = "'base-method'", waitSeconds = 2)
            open fun baseMethod(): String = "base result"
        }

        class DerivedService : BaseService() {
            override fun baseMethod(): String = "derived result"
        }

        val service = DerivedService()
        val result = service.baseMethod()
        result shouldBe "derived result"
    }
}