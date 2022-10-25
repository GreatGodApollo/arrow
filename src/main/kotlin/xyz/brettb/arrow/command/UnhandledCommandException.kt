package xyz.brettb.arrow.command

import org.bukkit.ChatColor
import org.bukkit.command.CommandException

class UnhandledCommandException(val causingException: Exception) :
    CommandException("Unhandled exception " + causingException.message), FriendlyException {
    override fun getFriendlyMessage(command: ArrowPluginCommand): String {
        return message!!
    }
}