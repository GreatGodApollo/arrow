package xyz.brettb.arrow.entities.command

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Suppress("unused")
annotation class ArrowCommandMeta(
    val description: String = "A command.",
    val aliases: Array<String> = [],
    val usage: String = ""
)