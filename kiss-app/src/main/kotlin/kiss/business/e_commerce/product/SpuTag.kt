package kiss.business.e_commerce.product

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface SpuTag : BaseEntity {

    @Key
    @ManyToOne
    val spu: Spu

    @Key
    val text: String
}