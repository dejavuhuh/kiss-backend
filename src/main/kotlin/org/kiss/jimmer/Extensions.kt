package org.kiss.jimmer

import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.kiss.PageParam
import kotlin.reflect.KClass

fun <E : Any, S : View<E>> KMutableRootQuery<E>.select(staticType: KClass<S>) = select(table.fetch(staticType))

fun <E : Any, R> KConfigurableRootQuery<E, R>.fetchPage(pageParam: PageParam) =
    fetchPage(pageParam.pageIndex, pageParam.pageSize)