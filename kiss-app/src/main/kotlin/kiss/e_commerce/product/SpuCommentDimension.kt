package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne
import java.math.BigDecimal

/**
 * 商品评价维度
 */
@Entity
interface SpuCommentDimension : BaseEntity {

    @Key
    @ManyToOne
    val spu: Spu

    @Key
    @ManyToOne
    val spec: SpuCommentDimensionSpec

    val weight: BigDecimal
}
