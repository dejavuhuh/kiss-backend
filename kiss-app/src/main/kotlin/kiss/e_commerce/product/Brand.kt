package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface Brand : BaseEntity {

    val name: String
}