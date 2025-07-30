package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne
import java.math.BigDecimal

/**
 * 商品评价维度评分
 */
@Entity
interface SpuCommentDimensionRating : BaseEntity {

    @Key
    @ManyToOne
    val dimension: SpuCommentDimension

    @Key
    @ManyToOne
    val comment: SpuComment

    val rating: BigDecimal
}