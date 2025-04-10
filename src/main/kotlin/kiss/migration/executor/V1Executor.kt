package kiss.migration.executor

import kiss.migration.MigrationExecutor
import kiss.system.permission.Permission
import kiss.system.permission.PermissionType
import kiss.system.permission.addBy
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Component

@Component
class V1Executor(val sql: KSqlClient) : MigrationExecutor {

    override val version: Int = 1

    override fun execute() {
        val permissions = listOf(
            Permission {
                type = PermissionType.DIRECTORY
                code = "system"
                name = "系统管理"
                children()
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:user"
                        name = "用户管理"
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:role"
                        name = "角色管理"
                        children()
                            .addBy {
                                type = PermissionType.BUTTON
                                code = "system:role:create"
                                name = "创建角色"
                            }
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:permission"
                        name = "权限管理"
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:job"
                        name = "定时任务"
                    }
            },
            Permission {
                type = PermissionType.DIRECTORY
                code = "trace"
                name = "链路追踪"
                children()
                    .addBy {
                        type = PermissionType.PAGE
                        code = "trace:session"
                        name = "会话管理"
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "trace:issue"
                        name = "问题反馈"
                    }
            }
        )

        sql.entities.saveEntities(permissions) {
            setMode(SaveMode.INSERT_ONLY)
            setAssociatedModeAll(AssociatedSaveMode.APPEND)
        }
    }
}