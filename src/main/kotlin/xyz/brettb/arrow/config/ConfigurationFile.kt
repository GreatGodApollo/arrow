package xyz.brettb.arrow.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Level

data class ConfigurationFile(val plugin: JavaPlugin, val file: File) {
    private val filename: String
    private val configFile: File
    private var fileConfiguration: FileConfiguration? = null

    @Suppress("unused")
    constructor(plugin: JavaPlugin, fileName: String) : this(plugin, File(plugin.dataFolder, fileName)) {}

    init {
        require(plugin.isEnabled) { "plugin has to be enabled" }
        filename = file.name
        configFile = file
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reloadConfiguration() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile)
        val inputStream = plugin.getResource(filename)
        if (inputStream != null) {
            val defaultConfiguration = YamlConfiguration.loadConfiguration(InputStreamReader(inputStream))
            fileConfiguration!!.setDefaults(defaultConfiguration)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val configuration: FileConfiguration?
        get() {
            if (fileConfiguration == null) {
                reloadConfiguration()
            }
            return fileConfiguration
        }

    @Suppress("unused")
    fun saveConfiguration() {
        if (fileConfiguration == null) {
            return
        }
        try {
            configuration!!.save(configFile)
        } catch (ex: IOException) {
            plugin.logger.log(Level.SEVERE, "Could not save configuration to $configFile", ex)
        }
    }

    @Suppress("unused")
    fun saveDefaultConfiguration() {
        if (!configFile.exists()) {
            plugin.saveResource(filename, false)
        }
    }
}