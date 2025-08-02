package kiss.migration

import kiss.infrastructure.jimmer.BaseEntity
import org.babyfish.jimmer.sql.Entity

@Entity
interface MigrationHistory : BaseEntity {

    val version: Int
}