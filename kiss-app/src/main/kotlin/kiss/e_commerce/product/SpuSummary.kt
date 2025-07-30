package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface SpuSummary : BaseEntity {

    @Key
    @ManyToOne
    val spu: Spu

    @Key
    val content: String
}