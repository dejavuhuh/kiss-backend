package kiss.e_commerce.product

import kiss.e_commerce.product.dto.ProductCategoryInput
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.web.bind.annotation.*

/**
 * 商品分类管理
 */
@RestController
@RequestMapping("/product-categories")
class ProductCategoryService(val sql: KSqlClient) {

    /**
     * 创建商品分类
     */
    @PostMapping
    fun create(@RequestBody input: ProductCategoryInput) {
        sql.save(input, SaveMode.INSERT_ONLY)
    }

    /**
     * 查询一级分类
     */
    @GetMapping
    fun listRoots(): List<@FetchBy("LIST_ITEM") ProductCategory> {
        return sql.executeQuery(ProductCategory::class) {
            where(table.parentId.isNull())
            orderBy(table.sortOrder)
            select(table.fetch(LIST_ITEM))
        }
    }

    /**
     * 根据上级分类 ID 查询下级分类
     */
    @GetMapping("/{id}")
    fun listByParentId(@PathVariable id: Int): List<@FetchBy("LIST_ITEM") ProductCategory> {
        return sql.executeQuery(ProductCategory::class) {
            where(table.parentId eq id)
            orderBy(table.sortOrder)
            select(table.fetch(LIST_ITEM))
        }
    }

    companion object {
        val LIST_ITEM = newFetcher(ProductCategory::class).by {
            allScalarFields()
        }
    }
}