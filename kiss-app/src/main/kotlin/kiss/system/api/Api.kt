package kiss.system.api

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne
import org.springframework.web.bind.annotation.RequestMethod

@Entity
interface Api : BaseEntity {

    @ManyToOne
    @Key(group = "uk_group_name")
    @Key(group = "uk_method_path")
    val group: ApiGroup

    @Key(group = "uk_group_name")
    val name: String

    @Key(group = "uk_method_path")
    val method: RequestMethod

    @Key(group = "uk_method_path")
    val path: String
}