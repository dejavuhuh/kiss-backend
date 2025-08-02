package kiss.json

import io.kotest.matchers.shouldBe
import kiss.infrastructure.json.JsonSerializer
import org.junit.jupiter.api.Test
import java.lang.reflect.ParameterizedType

class JsonSerializerTest {

    data class Generic<T>(val data: T)

    @Test
    fun `Serialize and deserialize`() {
        val original = Generic("Hello")
        val serialized = JsonSerializer.serialize(original)

        val genericType = object : ParameterizedType {
            override fun getActualTypeArguments() = arrayOf(String::class.java)
            override fun getRawType() = Generic::class.java
            override fun getOwnerType() = null
        }
        val deserialized = JsonSerializer.deserialize(serialized, genericType)
        deserialized.shouldBe(original)
    }

    @Test
    fun `Nested generic type`() {
        val original = listOf(Generic("Hello"), Generic("World"))
        val serialized = JsonSerializer.serialize(original)

        val genericType = object : ParameterizedType {
            override fun getActualTypeArguments() = arrayOf(object : ParameterizedType {
                override fun getActualTypeArguments() = arrayOf(String::class.java)
                override fun getRawType() = Generic::class.java
                override fun getOwnerType() = null
            })

            override fun getRawType() = List::class.java
            override fun getOwnerType() = null
        }

        val deserialized = JsonSerializer.deserialize(serialized, genericType)
        deserialized.shouldBe(original)
    }
}