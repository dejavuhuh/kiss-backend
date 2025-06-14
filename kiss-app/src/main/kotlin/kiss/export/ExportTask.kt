package kiss.export

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import kiss.jimmer.Traceable
import org.babyfish.jimmer.sql.Default
import org.babyfish.jimmer.sql.Entity

@Entity
interface ExportTask : BaseEntity, Creator, Traceable {

    val scene: ExportTaskScene

    @Default("PENDING")
    val status: ExportTaskStatus
}

enum class ExportTaskStatus {
    PENDING,
    DONE,
    FAILED,
}

enum class ExportTaskScene {
    BIG_DATA
}