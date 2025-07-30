package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface SpuCommentMedia : BaseEntity {

    @ManyToOne
    val comment: SpuComment

    val type: MediaType

    val resource: String
}

enum class MediaType {
    IMAGE,
    VIDEO,
}