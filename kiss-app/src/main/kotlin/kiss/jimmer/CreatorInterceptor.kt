package kiss.jimmer

import kiss.authentication.CurrentUserIdHolder
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.stereotype.Component

@Component
class CreatorInterceptor : DraftInterceptor<Creator, CreatorDraft> {

    override fun beforeSave(draft: CreatorDraft, original: Creator?) {
        if (original == null && !isLoaded(draft, Creator::creator)) {
            val currentUserId = CurrentUserIdHolder.get()
            draft.creatorId = currentUserId
        }
    }
}