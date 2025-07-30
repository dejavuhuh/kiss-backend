package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key

/**
 * 商品评价维度规格
 */
@Entity
interface SpuCommentDimensionSpec : BaseEntity {

    @Key
    val name: String
}