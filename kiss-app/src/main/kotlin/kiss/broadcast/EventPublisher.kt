package kiss.broadcast

import com.fasterxml.jackson.core.JacksonException
import kiss.json.JsonSerializer
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    val redisConnectionFactory: RedisConnectionFactory,
    val eventListeners: List<EventListener<*>>,
) : ApplicationRunner {

    val channel = "events".toByteArray()

    fun publish(event: Any) {
        redisConnectionFactory.connection.use {
            it.publish(channel, JsonSerializer.serialize(event).toByteArray())
        }
    }

    override fun run(args: ApplicationArguments) {
        redisConnectionFactory.connection.subscribe({ message, _ ->
            for (listener in eventListeners) {
                try {
                    tryConsumeEvent(message, listener)
                } catch (_: JacksonException) {
                }
            }
        }, channel)
    }

    private fun tryConsumeEvent(
        message: Message,
        listener: EventListener<*>
    ) {
        val event = JsonSerializer.deserialize(String(message.body), listener.eventType.java)
            ?: throw IllegalStateException("Deserialized event is null")
        @Suppress("UNCHECKED_CAST")
        (listener as EventListener<Any>).onEvent(event)
    }
}
