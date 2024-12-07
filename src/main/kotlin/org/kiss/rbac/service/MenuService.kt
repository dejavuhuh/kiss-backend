package org.kiss.rbac.service

import org.babyfish.jimmer.sql.exception.SaveException
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.kiss.error.BusinessException
import org.kiss.jimmer.select
import org.kiss.rbac.entity.Menu
import org.kiss.rbac.entity.dto.MenuInput
import org.kiss.rbac.entity.dto.MenuView
import org.kiss.rbac.entity.order
import org.kiss.rbac.entity.parentId
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/menus")
class MenuService(val sqlClient: KSqlClient) {

    @GetMapping
    fun findAll(): List<MenuView> {
        return sqlClient.executeQuery(Menu::class) {
            where(table.parentId.isNull())
            orderBy(table.order)
            select(MenuView::class)
        }
    }

    @PostMapping
    @Transactional
    fun create(@RequestBody input: MenuInput) {
        try {
            val maxOrder = fetchMaxOrder(input.parentId)
            sqlClient.insert(input.toEntity {
                order = maxOrder + 1
            })
        } catch (ex: SaveException.NotUnique) {
            throw BusinessException("Menu name '${input.name}' already exists")
        }
    }

    @PostMapping("/moveInto")
    @Transactional
    fun moveInto(@RequestBody request: MoveIntoRequest) {
        val maxOrder = fetchMaxOrder(request.targetId)
        sqlClient.update(Menu {
            id = request.sourceId
            parentId = request.targetId
            order = maxOrder + 1
        })
    }

    @PostMapping("/moveTo")
    @Transactional
    fun moveTo(@RequestBody request: MoveToRequest) {
        sqlClient.createUpdate(Menu::class) {
            set(table.order, table.order + 1)
            where(table.parentId eq request.parentId)
            where(table.order ge request.order)
        }.execute()
        sqlClient.update(Menu {
            id = request.sourceId
            parentId = request.parentId
            order = request.order
        })
    }


    @DeleteMapping
    fun deleteByIds(@RequestParam ids: List<Long>) {
        sqlClient.deleteByIds(Menu::class, ids)
    }

    private fun fetchMaxOrder(parentId: Long?): Int {
        return sqlClient.createQuery(Menu::class) {
            where(table.parentId eq parentId)
            select(max(table.order))
        }.fetchOne() ?: 0
    }

    data class MoveIntoRequest(val sourceId: Long, val targetId: Long)
    data class MoveToRequest(val sourceId: Long, val parentId: Long?, val order: Int)
}