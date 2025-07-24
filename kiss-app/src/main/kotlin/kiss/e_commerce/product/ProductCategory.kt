package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import kiss.jimmer.Enabled
import org.babyfish.jimmer.sql.*

@Entity
interface ProductCategory : BaseEntity, Enabled {

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

    val banner: String?
}