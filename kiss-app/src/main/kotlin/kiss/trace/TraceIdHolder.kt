package kiss.trace

import org.slf4j.MDC

object TraceIdHolder {
    
    fun get(): String {
        return MDC.get("traceId")
    }
}
