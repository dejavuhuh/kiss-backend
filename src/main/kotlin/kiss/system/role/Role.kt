package kiss.system.role

import kiss.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key

/**
 * 角色
 */
@Entity
interface Role : BaseEntity {

    /**
     * 角色名称
     */
    @Key
    val name: String

    /**
     * 角色描述
     */
    val description: String?
}