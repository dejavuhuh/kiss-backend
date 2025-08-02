package kiss.business.e_commerce.product

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OneToMany
import org.babyfish.jimmer.sql.Table
import java.math.BigDecimal

/**
 * 商品
 */
@Entity
@Table(name = "spu")
interface Spu : BaseEntity {

    /**
     * 所属分类
     */
    @ManyToOne
    val category: ProductCategory

    /**
     * 所属品牌
     */
    @ManyToOne
    val brand: Brand

    /**
     * 所属店铺
     */
    @ManyToOne
    val store: Store

    /**
     * 商品标题
     */
    val title: String

    /**
     * 商品价格
     */
    val price: BigDecimal

    /**
     * 商品主图
     */
    val banner: String

    /**
     * 商品评价维度
     */
    @OneToMany(mappedBy = "spu")
    val commentDimensions: List<SpuCommentDimension>

    /**
     * 商品评价
     */
    @OneToMany(mappedBy = "spu")
    val comments: List<SpuComment>

    /**
     * 商品标签
     */
    @OneToMany(mappedBy = "spu")
    val tags: List<SpuTag>
}
