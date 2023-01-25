package xyz.brettb.arrow.errors

import org.bukkit.ChatColor
import org.bukkit.command.CommandException
import xyz.brettb.arrow.entities.command.ArrowPluginCommand

class PermissionException(message: String) : CommandException(message), FriendlyException {
    override fun getFriendlyMessage(command: ArrowPluginCommand): String {
        return ChatColor.RED.toString() + "You don't have permission for this!"
    }
}