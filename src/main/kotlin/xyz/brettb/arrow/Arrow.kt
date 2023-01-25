package xyz.brettb.arrow

import org.bukkit.ChatColor
import xyz.brettb.arrow.entities.command.ArrowCommandMeta
import xyz.brettb.arrow.entities.command.ArrowPluginCommand
import xyz.brettb.arrow.entities.command.CommandContext
import xyz.brettb.arrow.events.ArrowGuiListener
import xyz.brettb.arrow.entities.plugin.ArrowPlugin
import xyz.brettb.arrow.entities.plugin.ArrowPluginMeta

@ArrowPluginMeta("&l&8[&bARW&8]&r")
class Arrow : ArrowPlugin() {

    override fun onPluginEnabled() {
        registerListener(ArrowGuiListener(this))
        registerCommand(ArrowCommand())
    }

    @ArrowCommandMeta(description = "Arrow plugin library", aliases = ["arw"])
    class ArrowCommand : ArrowPluginCommand("arrow") {
        override fun handleCommandUnspecific(ctx: CommandContext) {
            ctx.reply("${plugin!!.chatPrefix} ${ChatColor.DARK_AQUA} This server is using Arrow ${ChatColor.GREEN}v${ArrowInfo.VERSION}")
        }
    }

}