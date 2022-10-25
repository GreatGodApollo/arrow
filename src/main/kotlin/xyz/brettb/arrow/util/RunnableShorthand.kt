package xyz.brettb.arrow.util

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class RunnableShorthand(val plugin: JavaPlugin) {
    private val runnables: MutableList<Runnable> = ArrayList()
    private var async = false
    private var delay: Long = 0
    fun repeat(ticks: Long): BukkitTask {
        val bukkitRunnable = toRunnable()
        return if (async) bukkitRunnable.runTaskTimerAsynchronously(
            plugin,
            delay,
            ticks
        ) else bukkitRunnable.runTaskTimer(
            plugin, delay, ticks
        )
    }

    fun later(ticks: Long): BukkitTask {
        val bukkitRunnable = toRunnable()
        return if (async) bukkitRunnable.runTaskLaterAsynchronously(
            plugin,
            ticks + delay
        ) else bukkitRunnable.runTaskLater(
            plugin, ticks + delay
        )
    }

    fun go(): BukkitTask {
        val bukkitRunnable = toRunnable()
        return if (async) bukkitRunnable.runTaskAsynchronously(plugin) else bukkitRunnable.runTask(plugin)
    }

    private fun toRunnable(): BukkitRunnable {
        return object : BukkitRunnable() {
            override fun run() {
                for (runnable in runnables) runnable.run()
            }
        }
    }

    fun async(): RunnableShorthand {
        async = !async
        return this
    }

    fun delay(ticks: Long): RunnableShorthand {
        delay += ticks
        return this
    }

    fun resetDelay(): RunnableShorthand {
        delay = 0
        return this
    }

    fun with(runnable: Runnable): RunnableShorthand {
        runnables.add(runnable)
        return this
    }
}