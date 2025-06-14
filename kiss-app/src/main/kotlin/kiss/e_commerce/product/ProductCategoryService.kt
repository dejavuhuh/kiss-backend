package kiss.e_commerce.product

import kiss.e_commerce.product.dto.ProductCategoryInput
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.ast.expression.max
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 商品分类管理
 */
@Transactional
@RestController
@RequestMapping("/product-categories")
class ProductCategoryService(val sql: KSqlClient) {

    /**
     * 创建商品分类
     */
    @PostMapping
    fun create(@RequestBody input: ProductCategoryInput) {
        val maxSortOrder = sql.createQuery(ProductCategory::class) {
            where(table.parentId eq input.parentId)
            select(max(table.sortOrder))
        }.fetchOneOrNull() ?: 0
        val entity = input.toEntity {
            sortOrder = maxSortOrder + 1
        }
        sql.save(entity, SaveMode.INSERT_ONLY)
    }

    /**
     * 查询商品分类树
     */
    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") ProductCategory> {
        return sql.executeQuery(ProductCategory::class) {
            where(table.parentId.isNull())
            orderBy(table.sortOrder)
            select(table.fetch(LIST_ITEM))
        }
    }

    companion object {
        val LIST_ITEM = newFetcher(ProductCategory::class).by {
            allScalarFields()
            `children*` {
                filter {
                    orderBy(table.sortOrder)
                }
            }
        }
    }
}