package kiss.system.config

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
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