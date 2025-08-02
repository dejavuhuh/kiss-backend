package kiss.business.system.user

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.OneToOne

@Entity
interface FeishuUser {

    @Id
    val id: String

    @OneToOne
    val user: User
}