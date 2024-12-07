package org.kiss.rbac.service

import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.exception.SaveException
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.kiss.PageParam
import org.kiss.error.BusinessException
import org.kiss.jimmer.fetchPage
import org.kiss.jimmer.select
import org.kiss.rbac.entity.Menu
import org.kiss.rbac.entity.Role
import org.kiss.rbac.entity.dto.RoleInput
import org.kiss.rbac.entity.dto.RoleSpecification
import org.kiss.rbac.entity.dto.RoleView
import org.kiss.rbac.entity.fetchBy
import org.kiss.rbac.entity.id
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/roles")
class RoleService(val sqlClient: KSqlClient) {

    @GetMapping("/page")
    fun findByPage(pageParam: PageParam, specification: RoleSpecification): Page<RoleView> {
        return sqlClient.createQuery(Role::class) {
            where(specification)
            orderBy(table.id.desc())
            select(RoleView::class)
        }.fetchPage(pageParam)
    }

    @GetMapping
    fun findAll(): List<RoleView> {
        return sqlClient.executeQuery(Role::class) {
            select(RoleView::class)
        }
    }

    @PostMapping
    fun create(@RequestBody input: RoleInput) {
        try {
            sqlClient.insert(input)
        } catch (ex: SaveException.NotUnique) {
            throw BusinessException("Role name '${input.name}' already exists")
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody input: RoleInput) {
        try {
            sqlClient.update(input.toEntity { this.id = id })
        } catch (ex: SaveException.NotUnique) {
            throw BusinessException("Role name '${input.name}' already exists")
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long) {
        sqlClient.deleteById(Role::class, id)
    }

    @GetMapping("/{id}/menus")
    fun findMenus(@PathVariable id: Long): List<Long> {
        val role = sqlClient.createQuery(Role::class) {
            where(table.id eq id)
            select(table.fetchBy { menus() })
        }.fetchOne()
        return role.menus.map { it.id }
    }

    @PutMapping("/{id}/menus")
    fun saveMenus(@PathVariable id: Long, @RequestBody menuIds: List<Long>) {
        sqlClient.save(Role {
            this.id = id
            this.menus = menuIds.map {
                Menu { this.id = it }
            }
        })
    }
}