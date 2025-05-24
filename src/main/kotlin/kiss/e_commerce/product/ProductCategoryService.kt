package kiss.e_commerce.product

import kiss.e_commerce.product.dto.ProductCategoryInput
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.isNull
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product-categories")
class ProductCategoryService(val sql: KSqlClient) {

    @PostMapping
    fun create(@RequestBody input: ProductCategoryInput) {
        sql.save(input, SaveMode.INSERT_ONLY)
    }

    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") ProductCategory> {
        return sql.executeQuery(ProductCategory::class) {
            where(table.parentId.isNull())
            select(table.fetch(LIST_ITEM))
        }
    }

    @GetMapping("/{id}")
    fun listByParentId(@PathVariable id: Int): List<@FetchBy("LIST_ITEM") ProductCategory> {
        return sql.executeQuery(ProductCategory::class) {
            where(table.parentId eq id)
            select(table.fetch(LIST_ITEM))
        }
    }

    companion object {
        val LIST_ITEM = newFetcher(ProductCategory::class).by {
            allScalarFields()
        }
    }
}