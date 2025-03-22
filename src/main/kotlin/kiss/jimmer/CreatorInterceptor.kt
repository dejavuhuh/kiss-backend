package kiss.jimmer

import kiss.authentication.CurrentUserIdHolder
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.stereotype.Component

@Component
class CreatorInterceptor : DraftInterceptor<Creator, CreatorDraft> {

    override fun beforeSave(draft: CreatorDraft, original: Creator?) {
        if (isLoaded(draft, Creator::creator)) {
            throw IllegalStateException("不允许手动设置创建人")
        }

        if (original == null) {
            val currentUserId = CurrentUserIdHolder.get() ?: throw IllegalStateException("未设置当前用户ID")
            draft.creatorId = currentUserId
        }
    }
}