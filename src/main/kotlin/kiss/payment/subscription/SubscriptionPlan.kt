package kiss.payment.subscription

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import java.math.BigDecimal

@Entity
interface SubscriptionPlan : BaseEntity {

    @Key
    val name: String

    val billingCycle: BillingCycle

    val price: BigDecimal
}

enum class BillingCycle {
    MONTHLY,
    YEARLY,
}