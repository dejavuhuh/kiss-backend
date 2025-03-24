package kiss.system.role

import kiss.system.role.dto.RoleInput
import kiss.system.role.dto.RoleSpecification
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 角色服务
 */
@Transactional
@RestController
@RequestMapping("/roles")
class RoleService(val sql: KSqlClient) {

    /**
     * 分页查询
     *
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param specification 查询条件
     */
    @GetMapping("/page")
    fun page(
        @RequestParam pageIndex: Int,
        @RequestParam pageSize: Int,
        @ModelAttribute specification: RoleSpecification,
    ): Page<@FetchBy("PAGE") Role> {
        return sql.createQuery(Role::class) {
            where(specification)
            orderBy(table.createdTime.desc())
            select(table.fetch(PAGE))
        }.fetchPage(pageIndex, pageSize)
    }

    /**
     * 创建角色
     */
    @PostMapping
    fun create(@RequestBody input: RoleInput) {
        sql.insert(input)
    }

    /**
     * 更新角色
     *
     * @param id 角色ID
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody input: RoleInput) {
        sql.update(input.toEntity { this.id = id })
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        sql.deleteById(Role::class, id)
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     */
    @DeleteMapping
    fun deleteBatch(@RequestParam ids: List<Int>) {
        sql.deleteByIds(Role::class, ids)
    }

    companion object {
        val PAGE = newFetcher(Role::class).by {
            allScalarFields()
            creator {
                username()
            }
        }
    }
}