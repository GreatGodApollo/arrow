package xyz.brettb.arrow.entities.plugin

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArrowPluginMeta(val chatPrefix: String)
