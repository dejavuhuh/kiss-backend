package kiss.business.system.config

import kiss.infrastructure.jimmer.BaseEntity
import kiss.infrastructure.jimmer.Creator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface ConfigHistory : BaseEntity, Creator {

    @ManyToOne
    val config: Config

    val yaml: String?

    val reason: String
}
