package kiss.jimmer

import org.babyfish.jimmer.Input
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient

fun <E : Any> KSqlClient.insertOnly(input: Input<E>) = entities.save(input) {
    setMode(SaveMode.INSERT_ONLY)
    setAssociatedModeAll(AssociatedSaveMode.REPLACE)
}

fun <E : Any> KSqlClient.insertOnly(entity: E) = entities.save(entity) {
    setMode(SaveMode.INSERT_ONLY)
    setAssociatedModeAll(AssociatedSaveMode.REPLACE)
}

fun <E : Any> KSqlClient.updateOnly(entity: E) = entities.save(entity) {
    setMode(SaveMode.UPDATE_ONLY)
    setAssociatedModeAll(AssociatedSaveMode.REPLACE)
}
