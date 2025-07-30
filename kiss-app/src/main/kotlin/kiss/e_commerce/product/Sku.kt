package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "sku")
interface Sku : BaseEntity {
}