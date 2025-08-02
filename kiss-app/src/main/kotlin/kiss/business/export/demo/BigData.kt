package kiss.business.export.demo

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType
import org.babyfish.jimmer.sql.Id

@Entity
interface BigData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long
    val a: String
    val b: String
    val c: String
    val d: String
    val e: String
    val f: String
    val g: String
    val h: String
    val i: String
    val j: String
}