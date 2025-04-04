package kiss.issue

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Serialized

@Entity
interface Issue : BaseEntity, Creator {

    val request: HttpRequest

    val traceId: String

    val title: String

    val description: String
}

@Serialized
data class HttpRequest(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val body: String?
)