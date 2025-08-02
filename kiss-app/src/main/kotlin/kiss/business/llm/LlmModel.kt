package kiss.business.llm

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface LlmModel : BaseEntity {

    val name: String

    @ManyToOne
    val config: LlmConfig
}