package kiss.business.e_commerce.product

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "sku")
interface Sku : BaseEntity {
}