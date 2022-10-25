package xyz.brettb.arrow.command

interface FriendlyException {
    fun getFriendlyMessage(command: ArrowPluginCommand): String
}