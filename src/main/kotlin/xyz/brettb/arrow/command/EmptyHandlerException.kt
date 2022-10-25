package xyz.brettb.arrow.command

import org.bukkit.ChatColor
import org.bukkit.command.CommandException

class EmptyHandlerException : CommandException("There was no handler found for this command!"), FriendlyException {
    override fun getFriendlyMessage(command: ArrowPluginCommand): String {
        return message!!
    }
}