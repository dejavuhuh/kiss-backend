package kiss.infrastructure.job

@Target(AnnotationTarget.CLASS)
annotation class JobDescription(
    val title: String,
    val cron: String,
)
