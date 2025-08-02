package kiss.business.system.permission

import kiss.business.system.api.Api
import kiss.business.system.api.by
import kiss.business.system.role.id
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

        val API_LIST_ITEM = newFetcher(Api::class).by {
            name()
            method()
            path()
            group {
                name()
            }
        }
    }
}