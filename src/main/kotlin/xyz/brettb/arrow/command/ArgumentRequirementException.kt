package xyz.brettb.arrow.command

import org.bukkit.ChatColor
import org.bukkit.command.CommandException

class ArgumentRequirementException(message: String) : CommandException(message), FriendlyException {
    override fun getFriendlyMessage(command: ArrowPluginCommand): String {
        return ChatColor.RED.toString() + this.message
    }
}