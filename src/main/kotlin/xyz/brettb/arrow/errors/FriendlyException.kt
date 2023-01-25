package xyz.brettb.arrow.errors

import xyz.brettb.arrow.entities.command.ArrowPluginCommand

interface FriendlyException {
    fun getFriendlyMessage(command: ArrowPluginCommand): String
}