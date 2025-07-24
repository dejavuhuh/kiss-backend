package kiss.jimmer

import org.babyfish.jimmer.sql.Default
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface Enabled {

    @Default("true")
    val enabled: Boolean
}