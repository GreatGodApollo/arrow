package xyz.brettb.arrow.entities.plugin

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import xyz.brettb.arrow.entities.command.ArrowPluginCommand
import xyz.brettb.arrow.util.colorizeText


abstract class ArrowPlugin : JavaPlugin() {
    /**
     * Runs when the plugin is enabled
     */
    @Throws(Exception::class)
    protected open fun onPluginEnabled() {}

    /**
     * Runs when the plugin is disabled
     */
    @Throws(Exception::class)
    protected open fun onPluginDisabled() {}

    final override fun onEnable() {
        try {
            onPluginEnabled()
        } catch (t: Throwable) {
            logger.severe("Unable to enable the plugin!")
            t.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }

    final override fun onDisable() {
        try {
            onPluginDisabled()
        } catch (t: Throwable) {
            logger.severe("Unable to properly disable the plugin!")
            t.printStackTrace()
        }
    }

    fun <T : ArrowPluginCommand> registerCommand(command: T): T {
        var pluginCommand = getCommand(command.name)
        if (pluginCommand == null) {
            try {
                val commandConstructor =
                    PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
                commandConstructor.isAccessible = true
                pluginCommand = commandConstructor.newInstance(command.name, this) as PluginCommand
            } catch (t: Exception) {
                throw IllegalStateException("Could not register command ${command.name}")
            }

            val commandMap: CommandMap
            try {
                val pm = Bukkit.getPluginManager()
                val commandMapField = pm.javaClass.getDeclaredField("commandMap")
                commandMapField.isAccessible = true
                commandMap = commandMapField.get(pm) as CommandMap
            } catch (t: Exception) {
                throw IllegalStateException("Could not register command ${command.name}")
            }

            val annot = command.commandMeta
            if (annot != null) {
                pluginCommand.aliases = annot.aliases.asList()
                pluginCommand.description = annot.description
                pluginCommand.usage = annot.usage
            }
            commandMap.register(this.description.name, pluginCommand)
        }
        pluginCommand.setExecutor(command)
        pluginCommand.tabCompleter = command

        command.plugin = this

        return command
    }

    fun <T : ArrowPluginCommand> registerCommand(vararg commands: T) {
        commands.forEach {
            registerCommand(it)
        }
    }

    fun <T : Listener> registerListener(listener: T): T {
        server.pluginManager.registerEvents(listener, this)
        return listener
    }

    fun <T : Listener> registerListeners(vararg listeners: T) {
        listeners.forEach {
            server.pluginManager.registerEvents(it, this)
        }
    }

    val chatPrefix: String by lazy {
        val meta = this.javaClass.annotations.find { it is ArrowPluginMeta } as? ArrowPluginMeta
        val prefix = meta?.chatPrefix ?: "&l&8[&bPLUGIN&8]&r"
        prefix.colorizeText()
    }

}
