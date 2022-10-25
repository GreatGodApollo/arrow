package xyz.brettb.arrow

import org.bukkit.ChatColor
import xyz.brettb.arrow.command.ArrowCommandMeta
import xyz.brettb.arrow.command.ArrowPluginCommand
import xyz.brettb.arrow.command.CommandContext
import xyz.brettb.arrow.gui.ArrowGuiListener
import xyz.brettb.arrow.plugin.ArrowPlugin
import xyz.brettb.arrow.plugin.ArrowPluginMeta

@ArrowPluginMeta("&l&8[&bARW&8]&r")
class Arrow : ArrowPlugin() {

    override fun onPluginEnabled() {
        registerListener(ArrowGuiListener(this))
        registerCommand(ArrowCommand())
    }

    @ArrowCommandMeta("Arrow plugin library", ["arw"])
    class ArrowCommand : ArrowPluginCommand("arrow") {
        override fun handleCommandUnspecific(ctx: CommandContext) {
            ctx.reply("${plugin!!.chatPrefix} ${ChatColor.DARK_AQUA} This server is using Arrow ${ChatColor.GREEN}v${ArrowInfo.VERSION}")
        }
    }

}