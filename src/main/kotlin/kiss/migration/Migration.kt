package kiss.migration

import org.springframework.core.Ordered

interface Migration : Ordered {

    val version: Int

    fun execute()

    override fun getOrder() = version
}