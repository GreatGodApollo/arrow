package xyz.brettb.arrow.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArrowCommandPermission(
    val value: String,
    val isOpExempt: Boolean = true,
    val userOverrides: Array<String> = []
)