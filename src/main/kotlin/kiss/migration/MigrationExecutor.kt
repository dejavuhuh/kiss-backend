package kiss.migration

import org.springframework.core.Ordered

interface MigrationExecutor : Ordered {

    val version: Int

    fun execute()

    override fun getOrder() = version
}