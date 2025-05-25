package kiss.system.config

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface ConfigHistory : BaseEntity, Creator {

    @ManyToOne
    val config: Config

    val yaml: String?

    val reason: String
}
