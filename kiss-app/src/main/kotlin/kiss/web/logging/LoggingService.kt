package kiss.web.logging

import kiss.infrastructure.broadcast.EventListener
import kiss.infrastructure.broadcast.EventPublisher
import kiss.web.logging.LoggingService.ConfigureLevelEvent
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/logging")
class LoggingService(val eventPublisher: EventPublisher) {

    @PostMapping("/configureLevel")
    fun configureLevel(@RequestBody request: ConfigureLevelEvent) {
        eventPublisher.publish(request)
    }

    data class ConfigureLevelEvent(
        val logger: String,
        val level: LogLevel,
    )
}

@Component
class ConfigureLevelEventListener(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    val loggingSystem: LoggingSystem,
) : EventListener<ConfigureLevelEvent> {

    override val eventType = ConfigureLevelEvent::class

    override fun onEvent(event: ConfigureLevelEvent) {
        loggingSystem.setLogLevel(event.logger, event.level)
    }
}
