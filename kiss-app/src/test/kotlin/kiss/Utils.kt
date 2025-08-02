package kiss

import kiss.web.authentication.CurrentUserIdHolder

fun withTestUser(block: () -> Unit) {
    CurrentUserIdHolder.set(1)
    try {
        block()
    } finally {
        CurrentUserIdHolder.remove()
    }
}
