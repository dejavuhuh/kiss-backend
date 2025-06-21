package kiss.llm

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface LlmModel : BaseEntity {

    val name: String

    @ManyToOne
    val config: LlmConfig
}