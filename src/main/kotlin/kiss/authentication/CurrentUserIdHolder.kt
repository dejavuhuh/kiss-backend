package kiss.authentication

object CurrentUserIdHolder {
    val holder = ThreadLocal<Int?>()

    fun get(): Int {
        return holder.get() ?: throw IllegalStateException("Current user ID is not set")
    }

    fun exists(): Boolean {
        return holder.get() != null
    }

    fun set(userId: Int) {
        holder.set(userId)
    }

    fun remove() {
        holder.remove()
    }
}