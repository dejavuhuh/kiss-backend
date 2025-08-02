package kiss.business.system.user

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.OneToOne

@Entity
interface Account : BaseEntity {

    @OneToOne
    val user: User

    @Key
    val username: String

    val password: String
}