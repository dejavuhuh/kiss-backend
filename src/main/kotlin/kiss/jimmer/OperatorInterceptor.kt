package kiss.jimmer

import kiss.authentication.CurrentUserIdHolder
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.stereotype.Component

@Component
class OperatorInterceptor : DraftInterceptor<Operator, OperatorDraft> {

    override fun beforeSave(draft: OperatorDraft, original: Operator?) {
        if (original == null) {
            val currentUserId = CurrentUserIdHolder.get()
            draft.operatorId = currentUserId
        }
    }
}
