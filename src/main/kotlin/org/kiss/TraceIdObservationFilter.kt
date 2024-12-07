package org.kiss

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.Tracer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.ServerHttpObservationFilter

@Component
class TraceIdObservationFilter(private val tracer: Tracer, observationRegistry: ObservationRegistry) :
    ServerHttpObservationFilter(observationRegistry) {

    override fun onScopeOpened(scope: Observation.Scope, request: HttpServletRequest, response: HttpServletResponse) {
        tracer.currentSpan()?.let {
            response.setHeader("X-Trace-Id", it.context().traceId())
        }
    }
}