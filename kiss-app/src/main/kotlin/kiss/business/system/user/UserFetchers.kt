package kiss.business.system.user

import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

class UserFetchers {
    companion object {
        val LIST_ITEM = newFetcher(User::class).by {
            allScalarFields()
            roles {
                name()
            }
        }
    }
}