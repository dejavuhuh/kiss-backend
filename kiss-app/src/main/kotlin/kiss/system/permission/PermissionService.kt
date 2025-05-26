package kiss.system.permission

import kiss.jimmer.Immutables
import kiss.system.api.Api
import kiss.system.api.permissions
import kiss.system.permission.dto.PermissionInput
import kiss.system.role.Role
import kiss.system.role.RoleFetchers
import kiss.system.role.permissions
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 权限管理
 */
@Transactional
@RestController
@RequestMapping("/permissions")
@DefaultFetcherOwner(PermissionFetchers::class)
class PermissionService(val sql: KSqlClient) {

    /**
     * 创建权限
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody input: PermissionInput): Permission {
        val savedEntity = sql.save(input, SaveMode.INSERT_ONLY).modifiedEntity

        // audit log
        sql.save(PermissionAuditLog {
            this.permissionId = savedEntity.id
            this.operation = Operation.CREATE
            this.operationDetails = CreateDetails(input)
        }, SaveMode.INSERT_ONLY)

        return savedEntity
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody input: PermissionInput) {
        // check if the input is different from the old one
        val oldEntity = sql.findOneById(PermissionInput.METADATA.fetcher, id)
        val newEntity = input.toEntity { this.id = id }
        if (Immutables.equals(oldEntity, newEntity)) {
            return
        }

        // do save
        sql.save(newEntity, SaveMode.UPDATE_ONLY)

        // audit log
        val diff = Immutables.diff(oldEntity, newEntity)
        sql.save(PermissionAuditLog {
            this.permissionId = id
            this.operation = Operation.UPDATE
            this.operationDetails = UpdateDetails(diff)
        }, SaveMode.INSERT_ONLY)
    }

    /**
     * 查询权限树
     */
    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") Permission> {
        return sql.executeQuery(Permission::class) {
            where(table.parentId.isNull())
            select(table.fetch(PermissionFetchers.LIST_ITEM))
        }
    }

    /**
     * 查询权限关联的角色
     */
    @GetMapping("/{id}/roles")
    fun roles(@PathVariable id: Int): List<@FetchBy("SIMPLE", ownerType = RoleFetchers::class) Role> {
        return sql.executeQuery(Role::class) {
            where(table.permissions {
                this.id eq id
            })
            select(table.fetch(RoleFetchers.SIMPLE))
        }
    }

    /**
     * 绑定角色
     */
    @PostMapping("/{id}/bindRoles")
    fun bindRoles(@PathVariable id: Int, @RequestBody roleIds: List<Int>) {
        sql.save(Permission {
            this.id = id
            this.roleIds = roleIds
        })

        // audit log
        sql.save(PermissionAuditLog {
            this.permissionId = id
            this.operation = Operation.BIND_ROLES
            this.operationDetails = BindRolesDetails(roleIds)
        }, SaveMode.INSERT_ONLY)
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        sql.deleteById(Permission::class, id)

        // audit log
        sql.save(PermissionAuditLog {
            this.permissionId = id
            this.operation = Operation.DELETE
        }, SaveMode.INSERT_ONLY)
    }

    /**
     * 查询未绑定的接口
     */
    @GetMapping("/{id}/unbound-apis")
    fun unboundApis(@PathVariable id: Int): List<@FetchBy("API_LIST_ITEM") Api> {
        return sql.executeQuery(Api::class) {
            where += table.permissions {
                this.id ne id
            }
            select(table.fetch(PermissionFetchers.API_LIST_ITEM))
        }
    }

    /**
     * 查询已绑定的接口
     */
    @GetMapping("/{id}/bound-apis")
    fun boundApis(@PathVariable id: Int): List<@FetchBy("API_LIST_ITEM") Api> {
        return sql.executeQuery(Api::class) {
            where += table.permissions {
                this.id eq id
            }
            select(table.fetch(PermissionFetchers.API_LIST_ITEM))
        }
    }
}