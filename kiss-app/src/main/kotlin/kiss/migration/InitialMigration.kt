package kiss.migration

import kiss.system.api.addBy
import kiss.system.permission.Permission
import kiss.system.permission.PermissionType
import kiss.system.permission.addBy
import kiss.system.permission.id
import kiss.system.role.Role
import kiss.system.user.Account
import kiss.system.user.User
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod

@Component
class InitialMigration(val sql: KSqlClient) : Migration {

    override val version = 1

    override fun migrate() {
        // 初始化菜单
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
                        name = "页面权限"
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:api"
                        name = "接口权限"
                        apis()
                            .addBy {
                                method = RequestMethod.GET
                                path = "/permissions/{id}/unbound-apis"
                            }
                            .addBy {
                                method = RequestMethod.GET
                                path = "/permissions/{id}/bound-apis"
                            }
                            .addBy {
                                method = RequestMethod.GET
                                path = "/permissions"
                            }
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:job"
                        name = "定时任务"
                    }
                    .addBy {
                        type = PermissionType.PAGE
                        code = "system:config"
                        name = "配置中心"
                        apis()
                            .addBy {
                                method = RequestMethod.GET
                                path = "/config"
                            }
                            .addBy {
                                method = RequestMethod.GET
                                path = "/config/{id}"
                            }
                            .addBy {
                                method = RequestMethod.GET
                                path = "/config/{id}/histories"
                            }
                            .addBy {
                                method = RequestMethod.PUT
                                path = "/config/{id}/save-yaml"
                            }
                            .addBy {
                                method = RequestMethod.POST
                                path = "/config"
                            }
                            .addBy {
                                method = RequestMethod.DELETE
                                path = "/config/{id}"
                            }
                    }
            },
            Permission {
                type = PermissionType.DIRECTORY
                code = "user"
                name = "用户中心"
                children()
                    .addBy {
                        type = PermissionType.PAGE
                        code = "user:my-application"
                        name = "我的申请"
                    }
            },
            Permission {
                type = PermissionType.DIRECTORY
                code = "flow"
                name = "流程管理"
                children()
                    .addBy {
                        type = PermissionType.PAGE
                        code = "flow:editor"
                        name = "流程编辑器"
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
            },
            Permission {
                type = PermissionType.PAGE
                code = "fault"
                name = "故障演练"
                apis()
                    .addBy {
                        method = RequestMethod.POST
                        path = "/fault/high-cpu"
                    }
                    .addBy {
                        method = RequestMethod.GET
                        path = "/fault/cpu-intensive"
                    }
                    .addBy {
                        method = RequestMethod.POST
                        path = "/fault/server-error"
                    }
            },
            Permission {
                type = PermissionType.DIRECTORY
                code = "e-commerce"
                name = "电商业务"
                children()
                    .addBy {
                        type = PermissionType.DIRECTORY
                        code = "e-commerce:product"
                        name = "商品管理"
                        children()
                            .addBy {
                                type = PermissionType.PAGE
                                code = "e-commerce:product:category"
                                name = "商品分类"
                                apis()
                                    .addBy {
                                        method = RequestMethod.GET
                                        path = "/product-categories"
                                    }
                                    .addBy {
                                        method = RequestMethod.POST
                                        path = "/product-categories"
                                    }
                            }
                    }
            },
            Permission {
                type = PermissionType.DIRECTORY
                code = "export"
                name = "数据导出"
                children()
                    .addBy {
                        type = PermissionType.PAGE
                        code = "export:big-data"
                        name = "海量数据导出"
                        apis()
                            .addBy {
                                method = RequestMethod.POST
                                path = "/export/demo/big-data/generate"
                            }
                            .addBy {
                                method = RequestMethod.POST
                                path = "/export/demo/big-data/export-task"
                            }
                            .addBy {
                                method = RequestMethod.GET
                                path = "/export-tasks"
                            }
                            .addBy {
                                method = RequestMethod.GET
                                path = "/s3/preSignedUrl"
                            }
                    }
            }
        )
        sql.saveEntities(permissions) {
            setMode(SaveMode.INSERT_ONLY)
            setAssociatedMode(Permission::children, AssociatedSaveMode.APPEND)
            setKeyOnlyAsReferenceAll()
        }

        // 创建系统用户
        val systemUser = sql.save(User {
            displayName = "系统"
        }, SaveMode.INSERT_ONLY).modifiedEntity

        // 创建超级管理员角色，同时绑定所有权限
        val allPermissionIds = sql.executeQuery(Permission::class) {
            select(table.id)
        }
        val superAdminRole = sql.save(Role {
            name = "超级管理员"
            permissionIds = allPermissionIds
            creatorId = systemUser.id
        }, SaveMode.INSERT_ONLY).modifiedEntity

        // 创建超级管理员用户
        val superAdminPassword = System.getenv("SUPER_ADMIN_PASSWORD") ?: throw IllegalStateException(
            "Environment variable `SUPER_ADMIN_PASSWORD` not found"
        )
        sql.save(Account {
            username = "kiss"
            password = BCrypt.hashpw(superAdminPassword, BCrypt.gensalt())
            user {
                displayName = "kiss"
                roleIds = listOf(superAdminRole.id)
            }
        }, SaveMode.INSERT_ONLY, AssociatedSaveMode.APPEND)
    }
}
