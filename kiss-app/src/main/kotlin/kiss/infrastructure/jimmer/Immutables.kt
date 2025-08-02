package kiss.infrastructure.jimmer

import org.babyfish.jimmer.DraftObjects
import org.babyfish.jimmer.ImmutableObjects
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.Internal
import kotlin.collections.iterator

object Immutables {

    @Suppress("UNCHECKED_CAST")
    fun <T> diff(old: T, new: T): T {
        val type = (old as ImmutableSpi).__type()
        val declaredProps = type.entityProps
        return Internal.produce(type, new) { draft ->
            for ((_, prop) in declaredProps) {
                if (ImmutableObjects.isLoaded(old, prop) && ImmutableObjects.isLoaded(new, prop)) {
                    if (ImmutableObjects.get(old, prop) == ImmutableObjects.get(new, prop)) {
                        DraftObjects.unload(draft, prop)
                    }
                }
            }
        } as T
    }

    fun <T> equals(a: T, b: T): Boolean {
        return ImmutableSpi.equals(a, b, true)
    }
}
