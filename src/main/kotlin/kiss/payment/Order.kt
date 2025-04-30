package kiss.payment

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "\"order\"")
interface Order : BaseEntity {

    val status: OrderStatus
}

enum class OrderStatus {
    PENDING,
}