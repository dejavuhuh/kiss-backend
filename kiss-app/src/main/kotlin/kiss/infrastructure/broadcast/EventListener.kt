package kiss.infrastructure.broadcast

import kotlin.reflect.KClass

interface EventListener<T : Any> {

    fun onEvent(event: T)

    val eventType: KClass<T>
}
