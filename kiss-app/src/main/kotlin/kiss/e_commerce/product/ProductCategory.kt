package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.*

@Entity
interface ProductCategory : BaseEntity {

    @Key
    @ManyToOne
    val parent: ProductCategory?

    @Key
    val name: String

    @Default("false")
    val isLeaf: Boolean

    @OneToMany(mappedBy = "parent")
    val children: List<ProductCategory>

    val sortOrder: Int
}