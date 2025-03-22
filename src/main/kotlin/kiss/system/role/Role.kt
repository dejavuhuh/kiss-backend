package kiss.system.role

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key

/**
 * 角色
 */
@Entity
interface Role : BaseEntity, Creator {

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