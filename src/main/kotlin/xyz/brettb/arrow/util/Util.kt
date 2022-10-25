package xyz.brettb.arrow.util

import com.cryptomorin.xseries.XMaterial
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import xyz.brettb.arrow.command.ArrowPluginCommand

fun MutableMap<String, Pair<ArrowPluginCommand, Boolean>>.putInsensitive(
    k: String,
    v: Pair<ArrowPluginCommand, Boolean>
) {
    this[k.lowercase()] = v
}

fun MutableMap<String, Pair<ArrowPluginCommand, Boolean>>.getInsensitive(k: String): Pair<ArrowPluginCommand, Boolean>? =
    this[k.lowercase()]

val ItemStack?.isInvalid : Boolean
    get() = this == null || this.type == XMaterial.AIR.parseMaterial()

fun String.colorizeText() : String {
    if (this.isEmpty()) return this
    return ChatColor.translateAlternateColorCodes('&', this)
}

fun Array<out String>.colorize() : Array<out String> {
    if (this.isEmpty()) return this
    return this.map { str -> "&r$str" }.onEach { it.colorizeText() }.toTypedArray()
}