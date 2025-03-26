package kiss.authentication

object CurrentUserIdHolder {
    val holder = ThreadLocal<Int>()

    fun get(): Int {
        return holder.get()
    }

    fun set(userId: Int) {
        holder.set(userId)
    }

    fun remove() {
        holder.remove()
    }
}