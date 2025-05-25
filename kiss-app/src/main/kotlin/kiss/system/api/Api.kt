package kiss.system.api

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne
import org.springframework.web.bind.annotation.RequestMethod

@Entity
interface Api : BaseEntity {

    @ManyToOne
    val group: ApiGroup

    val name: String

    val method: RequestMethod

    @Key
    val path: String
}