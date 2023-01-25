package xyz.brettb.arrow.errors

import org.bukkit.command.CommandException
import xyz.brettb.arrow.entities.command.ArrowPluginCommand

class UnhandledCommandException(val causingException: Exception) :
    CommandException("Unhandled exception " + causingException.message), FriendlyException {
    override fun getFriendlyMessage(command: ArrowPluginCommand): String {
        return message!!
    }
}