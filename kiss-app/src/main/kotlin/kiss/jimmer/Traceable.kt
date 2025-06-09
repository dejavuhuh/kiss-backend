package kiss.jimmer

import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface Traceable {

    val traceId: String
}
