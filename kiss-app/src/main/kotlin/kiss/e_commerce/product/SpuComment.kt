package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import kiss.system.user.User
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OneToMany
import java.math.BigDecimal

/**
 * 商品评价
 */
@Entity
interface SpuComment : BaseEntity {

    /**
     * 评价的商品
     */
    @ManyToOne
    val spu: Spu

    /**
     * 评价的用户
     */
    @ManyToOne
    val user: User

    /**
     * 评价内容
     */
    val text: String

    /**
     * 评价媒体(图片+视频)
     */
    @OneToMany(mappedBy = "comment")
    val media: List<SpuCommentMedia>

    /**
     * 各维度评分
     */
    @OneToMany(mappedBy = "comment")
    val dimensionRatings: List<SpuCommentDimensionRating>

    /**
     * 加权评分
     */
    val weightedRating: BigDecimal
}
