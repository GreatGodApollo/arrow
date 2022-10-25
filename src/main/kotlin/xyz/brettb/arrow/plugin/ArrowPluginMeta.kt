package xyz.brettb.arrow.plugin

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArrowPluginMeta(val chatPrefix: String)
