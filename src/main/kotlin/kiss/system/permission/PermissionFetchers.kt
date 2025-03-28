package kiss.system.permission

import kiss.system.role.id
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

class PermissionFetchers {
    companion object {
        val LIST_ITEM = newFetcher(Permission::class).by {
            allScalarFields()
            parentId()
            roles({
                filter {
                    orderBy(table.id)
                }
            }) {
                name()
            }
            `children*`()
        }
    }
}