package kiss.system.api

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.Table
import org.springframework.web.bind.annotation.RequestMethod

@Entity
@Table(name = "user_api_permissions_mv")
interface UserApiPermissions {

    @Id
    val id: Int

    @Key
    val userId: Int

    @Key
    val apiMethod: RequestMethod

    @Key
    val apiPath: String
}
