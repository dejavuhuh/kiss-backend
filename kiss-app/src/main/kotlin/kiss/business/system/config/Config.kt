package kiss.business.system.config

import kiss.infrastructure.jimmer.BaseEntity
import kiss.infrastructure.jimmer.Creator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.Version

@Entity
interface Config : BaseEntity, Creator {

    @Key
    val name: String

    val yaml: String?

    @Version
    val version: Int
}