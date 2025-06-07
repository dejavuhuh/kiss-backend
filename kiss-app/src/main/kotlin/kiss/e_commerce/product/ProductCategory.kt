package kiss.e_commerce.product

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OneToMany

@Entity
interface ProductCategory : BaseEntity {

    @Key
    @ManyToOne
    val parent: ProductCategory?

    @Key
    val name: String

    val isLeaf: Boolean

    @OneToMany(mappedBy = "parent")
    val children: List<ProductCategory>

    val sortOrder: Int
}