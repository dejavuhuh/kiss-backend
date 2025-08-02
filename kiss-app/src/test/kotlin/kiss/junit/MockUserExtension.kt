package kiss.junit

import kiss.web.authentication.CurrentUserIdHolder
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

@Target(AnnotationTarget.FUNCTION)
annotation class MockUser(val id: Int)

class MockUserExtension : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        val mockUser = context.requiredTestMethod.getAnnotation(MockUser::class.java) ?: return
        CurrentUserIdHolder.set(mockUser.id)
    }

    override fun afterEach(context: ExtensionContext) {
        CurrentUserIdHolder.remove()
    }
}
