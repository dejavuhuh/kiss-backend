package kiss.web

import kiss.system.permission.PermissionProps
import kiss.system.role.RoleProps
import kiss.system.user.UserProps
import org.babyfish.jimmer.sql.exception.SaveException
import org.babyfish.jimmer.sql.runtime.ExceptionTranslator
import org.springframework.stereotype.Component

@Component
class NotUniqueExceptionTranslator : ExceptionTranslator<SaveException.NotUnique> {

    override fun translate(ex: SaveException.NotUnique, args: ExceptionTranslator.Args): Exception? {
        return when {
            ex.isMatched(RoleProps.NAME) -> BusinessException("角色名称已存在")
            ex.isMatched(UserProps.USERNAME) -> BusinessException("用户名已存在")
            ex.isMatched(PermissionProps.PARENT, PermissionProps.CODE) -> BusinessException("权限编码已存在")
            else -> null
        }
    }
}
