package kiss.business.e_commerce.product

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface Brand : BaseEntity {

    val name: String
}