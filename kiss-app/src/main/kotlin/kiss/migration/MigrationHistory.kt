package kiss.migration

import kiss.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface MigrationHistory : BaseEntity {

    val version: Int
}