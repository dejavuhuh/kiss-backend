package kiss.system.config

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import org.babyfish.jimmer.sql.DissociateAction
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OnDissociate

@Entity
interface ConfigHistory : BaseEntity, Creator {

    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    val config: Config

    val yaml: String?

    val reason: String
}