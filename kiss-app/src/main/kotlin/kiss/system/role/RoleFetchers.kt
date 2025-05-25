package kiss.system.role

import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

class RoleFetchers {
    companion object {
        val SIMPLE = newFetcher(Role::class).by {
            name()
        }
        val LIST_ITEM = newFetcher(Role::class).by {
            allScalarFields()
            creator {
                displayName()
            }
        }
    }
}