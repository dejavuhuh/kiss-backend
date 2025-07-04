package kiss.llm

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface LlmConfig : BaseEntity {

    val provider: Provider

    val apiKey: String
}

enum class Provider {
    GEMINI,
}