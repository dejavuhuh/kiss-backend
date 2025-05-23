package kiss.web

import kiss.e_commerce.product.ProductCategoryProps
import kiss.payment.subscription.SubscriptionPlanProps
import kiss.system.config.ConfigProps
import kiss.system.permission.PermissionProps
import kiss.system.role.RoleProps
import kiss.system.user.AccountProps
import org.babyfish.jimmer.sql.exception.SaveException
import org.babyfish.jimmer.sql.runtime.ExceptionTranslator
import org.springframework.stereotype.Component

@Component
class NotUniqueExceptionTranslator : ExceptionTranslator<SaveException.NotUnique> {

    override fun translate(ex: SaveException.NotUnique, args: ExceptionTranslator.Args): Exception? {
        return when {
            ex.isMatched(RoleProps.NAME) -> BusinessException("角色名称已存在")
            ex.isMatched(AccountProps.USERNAME) -> BusinessException("用户名已存在")
            ex.isMatched(PermissionProps.CODE) -> BusinessException("权限编码已存在")
            ex.isMatched(ConfigProps.NAME) -> BusinessException("配置名称已存在")
            ex.isMatched(SubscriptionPlanProps.NAME) -> BusinessException("订阅计划名称已存在")
            ex.isMatched(ProductCategoryProps.PARENT, ProductCategoryProps.NAME) -> {
                val parent = ex.getValue(ProductCategoryProps.PARENT)
                if (parent == null) {
                    BusinessException("根分类名称已存在")
                } else {
                    BusinessException("子分类名称已存在")
                }
            }

            else -> null
        }
    }
}
