package xyz.brettb.arrow.command

import org.bukkit.command.CommandSender

class CommandContext(
    val sender: CommandSender,
    @Suppress("unused")
    val alias: String,
    @Suppress("unused")
    val args: Array<String>
) {

    companion object {
        fun with(sender: CommandSender, alias: String, args: Array<String>): CommandContext =
            CommandContext(sender, alias, args)
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun reply(vararg message: String) {
        sender.sendMessage(message)
    }

}
