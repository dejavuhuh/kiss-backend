package kiss.jimmer

import kiss.trace.TraceIdHolder
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.stereotype.Component

@Component
class TraceableInterceptor : DraftInterceptor<Traceable, TraceableDraft> {

    override fun beforeSave(draft: TraceableDraft, original: Traceable?) {
        if (original == null) {
            draft.traceId = TraceIdHolder.get()
        }
    }
}
