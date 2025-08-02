package kiss.infrastructure.jimmer

import kiss.business.system.user.User
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface Creator {

    @ManyToOne
    val creator: User
}