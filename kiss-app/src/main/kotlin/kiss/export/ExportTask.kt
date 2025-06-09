package kiss.export

import kiss.jimmer.BaseEntity
import kiss.jimmer.Creator
import kiss.jimmer.Traceable
import org.babyfish.jimmer.sql.Entity
import java.time.Instant

@Entity
interface ExportTask : BaseEntity, Creator, Traceable {

    val finishedTime: Instant?
}
