package kiss.llm

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface LlmAbility : BaseEntity {

    @Key
    val name: String

    val systemPrompt: String

    val userPrompt: String

    @ManyToOne
    val model: LlmModel

    val jsonSchema: String
}