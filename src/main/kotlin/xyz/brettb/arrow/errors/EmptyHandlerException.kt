package xyz.brettb.arrow.errors

import org.bukkit.command.CommandException
import xyz.brettb.arrow.entities.command.ArrowPluginCommand

class EmptyHandlerException : CommandException("There was no handler found for this command!"), FriendlyException {
    override fun getFriendlyMessage(command: ArrowPluginCommand): String {
        return message!!
    }
}