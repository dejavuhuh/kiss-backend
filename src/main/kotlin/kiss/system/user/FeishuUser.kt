package kiss.system.user

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.OneToOne

//@Entity
interface FeishuUser : BaseEntity {

    @OneToOne
    val user: User
}