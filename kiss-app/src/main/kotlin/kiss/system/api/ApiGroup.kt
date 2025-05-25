package kiss.system.api

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key

@Entity
interface ApiGroup : BaseEntity {

    @Key(group = "uk_name")
    val name: String

    @Key(group = "uk_path_prefix")
    val pathPrefix: String
}