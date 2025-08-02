package kiss.infrastructure.lock

@Target(AnnotationTarget.FUNCTION)
annotation class DistributedLock(
    val keyExpression: String,
    val waitSeconds: Long,
    val errorMessage: String = "操作频繁，请稍后重试",
)
