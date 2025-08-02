package kiss.infrastructure.broadcast

import kiss.infrastructure.json.JsonSerializer
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    val redisConnectionFactory: RedisConnectionFactory,
    eventListeners: List<EventListener<*>>,
) : ApplicationRunner {

    val eventListenerMap = eventListeners.associateBy { it.eventType.qualifiedName }
    val channelPrefix = "events"

    fun publish(event: Any) {
        val channelToPublish = "${channelPrefix}:${event::class.qualifiedName}"
        val serializedEvent = JsonSerializer.serialize(event)
        redisConnectionFactory.connection.use {
            it.publish(channelToPublish.toByteArray(), serializedEvent.toByteArray())
        }
    }

    override fun run(args: ApplicationArguments) {
        val channelsToSubscribe = eventListenerMap.keys.map { "$channelPrefix:$it" }
        redisConnectionFactory.connection.subscribe({ message, _ ->
            val channel = String(message.channel)
            val eventType = channel.removePrefix("$channelPrefix:")

            val listener = eventListenerMap[eventType]
                ?: throw IllegalStateException("No listener for event type $eventType")
            val event = JsonSerializer.deserialize(String(message.body), listener.eventType.java)
                ?: throw IllegalStateException("Deserialized event is null")

            @Suppress("UNCHECKED_CAST")
            (listener as EventListener<Any>).onEvent(event)

        }, *channelsToSubscribe.map(String::toByteArray).toTypedArray())
    }
}