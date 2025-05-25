package kiss.system.api

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.OneToMany

@Entity
interface ApiGroup : BaseEntity {

    @Key
    val name: String

    @OneToMany(mappedBy = "group")
    val apis: List<Api>
}