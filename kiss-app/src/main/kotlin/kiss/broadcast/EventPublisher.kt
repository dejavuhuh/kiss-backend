package kiss.broadcast

import kiss.json.JsonSerializer
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

const val CHANNEL = "events"

@Component
class EventPublisher(
    val redisConnectionFactory: RedisConnectionFactory,
    val eventListeners: List<EventListener<*>>,
): ApplicationRunner {

    fun publish(event: Any) {
        redisConnectionFactory.connection.use {
            it.publish(CHANNEL.toByteArray(), JsonSerializer.serialize(event).toByteArray())
        }
    }

    override fun run(args: ApplicationArguments) {
        for (listener in eventListeners) {
            redisConnectionFactory.connection.subscribe({ message, _ ->
                val event = JsonSerializer.deserialize(String(message.body), listener.eventType.java) ?: throw IllegalStateException("Failed to deserialize event")
                @Suppress("UNCHECKED_CAST")
                (listener as EventListener<Any>).onEvent(event)
            }, CHANNEL.toByteArray())
        }
    }
}
