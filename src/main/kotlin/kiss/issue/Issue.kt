package kiss.issue

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import org.babyfish.jimmer.sql.*

@Entity
interface Issue : BaseEntity, Creator {

    val request: HttpRequest

    val traceId: String

    val title: String

    val description: String

    @Default("OPEN")
    val state: IssueState

    @ManyToOne
    val relatedTo: Issue?

    @OneToMany(mappedBy = "relatedTo")
    val relatedFrom: List<Issue>
}

enum class IssueState {
    OPEN,
    CLOSED,
}

@Serialized
data class HttpRequest(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val query: Map<String, String>,
    val body: String?
)