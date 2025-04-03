package kiss.error

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Serialized

@Entity
interface SystemError : BaseEntity, Creator {

    val request: HttpRequest

    val traceId: String

    val screenshotS3Key: String

    val description: String?
}

@Serialized
data class HttpRequest(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val body: String?
)