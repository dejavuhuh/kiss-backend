package kiss.util

import org.slf4j.MDC

fun go(fn: () -> Unit) {
    val contextMap = MDC.getCopyOfContextMap()
    Thread.ofVirtual().start {
        MDC.setContextMap(contextMap)
        try {
            fn()
        } finally {
            MDC.clear()
        }
    }
}
