package kiss.migration

import kiss.e_commerce.product.ProductCategory
import kiss.e_commerce.product.addBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Component

@Component
class ProductCategoryMigration(val sql: KSqlClient) : Migration {
    override val version = 2

    override fun migrate() {
        val productCategories = listOf(
            ProductCategory {
                name = "家电"
                sortOrder = 1
                children()
                    .addBy {
                        name = "个护健康"
                        sortOrder = 1
                    }
                    .addBy {
                        name = "家电配件"
                        sortOrder = 2
                    }
                    .addBy {
                        name = "家电套装"
                        sortOrder = 3
                    }
                    .addBy {
                        name = "洗衣机"
                        sortOrder = 4
                    }
                    .addBy {
                        name = "冰箱"
                        sortOrder = 5
                    }
                    .addBy {
                        name = "厨房小电"
                        sortOrder = 6
                    }
                    .addBy {
                        name = "环境电器"
                        sortOrder = 7
                    }
                    .addBy {
                        name = "电视"
                        sortOrder = 8
                    }
                    .addBy {
                        name = "厨卫大电"
                        sortOrder = 9
                    }
                    .addBy {
                        name = "空调"
                        sortOrder = 10
                    }
                    .addBy {
                        name = "清洁电器"
                        sortOrder = 11
                    }
                    .addBy {
                        name = "视听影音"
                        sortOrder = 12
                    }
                    .addBy {
                        name = "特色类目"
                        sortOrder = 13
                    }
            }
        )
    }
}