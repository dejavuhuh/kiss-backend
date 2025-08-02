package kiss.business.e_commerce.product.es

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.math.BigDecimal

@Document(indexName = "spu")
data class Spu(
    @Id
    val id: Int,

    @Field(type = FieldType.Object)
    val category: ProductCategory,

    @Field(type = FieldType.Object)
    val brand: Brand,

    @Field(type = FieldType.Object)
    val store: Store,

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    val title: String,

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100.0)
    val price: BigDecimal,

    @Field(type = FieldType.Text, index = false)
    val banner: String,

    @Field(type = FieldType.Integer)
    val positiveCommentRatio: Int,

    @Field(type = FieldType.Integer)
    val commentCount: Int,

    @Field(type = FieldType.Integer)
    val salesCount: Int,
)

data class ProductCategory(
    @Field(type = FieldType.Integer)
    val id: Int,

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    val name: String,
)

data class Brand(
    @Field(type = FieldType.Integer)
    val id: Int,

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    val name: String,
)

data class Store(
    @Field(type = FieldType.Integer)
    val id: Int,

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    val name: String,
)