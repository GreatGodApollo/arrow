package xyz.brettb.arrow.command

import org.bukkit.ChatColor
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandException
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import xyz.brettb.arrow.plugin.ArrowPlugin
import xyz.brettb.arrow.util.RunnableShorthand
import xyz.brettb.arrow.util.getInsensitive
import xyz.brettb.arrow.util.putInsensitive
import java.lang.IllegalArgumentException

abstract class ArrowPluginCommand(
    @Suppress("MemberVisibilityCanBePrivate")
    val name: String,
    @Suppress("MemberVisibilityCanBePrivate")
    val useSubCommandsOnly: Boolean = false,
    @Suppress("MemberVisibilityCanBePrivate")
    val shouldGenerateHelp: Boolean = true
) : CommandExecutor, TabCompleter {

    // name, Pair<Command,Alias?>
    private var _subCommands: HashMap<String, Pair<ArrowPluginCommand, Boolean>> = hashMapOf()
    private var superCommand: ArrowPluginCommand? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val commandMeta: ArrowCommandMeta? =
        this.javaClass.annotations.find { it is ArrowCommandMeta } as? ArrowCommandMeta

    @Suppress("MemberVisibilityCanBePrivate")
    val commandPermission: ArrowCommandPermission? =
        this.javaClass.annotations.find { it is ArrowCommandPermission } as? ArrowCommandPermission

    internal var plugin: ArrowPlugin? = null
        get() = superCommand?.plugin ?: field

    @Suppress("unused")
    val subCommands: Map<String, Pair<ArrowPluginCommand, Boolean>>
        get() = _subCommands.filter { !it.value.second }.toMap()

    val formattedName: String = if (superCommand == null) name else superCommand!!.formattedName + name

    @Suppress("unused")
    constructor(name: String, vararg subCommands: ArrowPluginCommand) : this(name) {
        registerSubCommand(*subCommands)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerSubCommand(vararg subCommands: ArrowPluginCommand) {
        subCommands.forEach { cmd ->
            if (superCommand != null) throw IllegalArgumentException("The command you attempted to register already has a super command!")
            this._subCommands.putInsensitive(cmd.name, Pair(cmd, false))
            if (cmd.commandMeta != null && cmd.commandMeta.aliases.isNotEmpty())
                cmd.commandMeta.aliases.filter { it.isNotEmpty() }.forEach { a ->
                    this._subCommands.putInsensitive(a, Pair(cmd, true))
                }
            cmd.superCommand = this
            cmd.plugin = this.plugin
        }
        regenerateHelpCommand()
    }

    @Suppress("unused")
    fun unregisterSubCommand(vararg subCommands: ArrowPluginCommand) {
        subCommands.forEach { cmd ->
            this._subCommands.remove(cmd.name)
            cmd.commandMeta?.aliases?.forEach { this._subCommands.remove(it) }
            cmd.superCommand = null
        }
        regenerateHelpCommand()
    }

    private fun regenerateHelpCommand() {
        if (!shouldGenerateHelp) return
        val sortedSubCommands: MutableMap<String, Pair<ArrowPluginCommand, Boolean>> =
            _subCommands.filter { !it.value.second }.toSortedMap()

        val superHelpCommand = this
        _subCommands.putInsensitive("help", Pair(object : ArrowPluginCommand("help") {
            override fun handleCommandUnspecific(ctx: CommandContext) {
                val builder = StringBuilder()

                // Message Header
                builder.append(ChatColor.GOLD).append(" Help for ").append(ChatColor.GREEN).append("/")
                    .append(superHelpCommand.formattedName).append("\n")

                // Add the base command if we aren't limited to sub-commands
                builder.append(ChatColor.DARK_AQUA).append(" >").append(ChatColor.GREEN).append("/")
                    .append(superHelpCommand.formattedName).append("\n")

                // Add all the sub-commands
                sortedSubCommands.forEach {
                    builder.append(ChatColor.DARK_AQUA).append(" >").append(ChatColor.GREEN).append("/")
                        .append(it.value.first.formattedName).append("\n")
                }

                val s = builder.toString()
                ctx.sender.sendMessage(plugin!!.chatPrefix + s)
            }
        }, false))
        _subCommands.getInsensitive("help")?.first?.superCommand = this
    }

    final override fun onCommand(sender: CommandSender, command: Command, alias: String, args: Array<String>): Boolean {
        try {
            var subCommand: ArrowPluginCommand? = null

            // Does this command require a permission?
            if (commandPermission != null) {
                // Default to true, only set to false if the player can't run it.
                var playerOverridden = true

                // The command source is a player
                if (sender is Player)
                    if (!(commandPermission.userOverrides.contains(sender.uniqueId.toString())))
                        playerOverridden = false

                // Check if the sender has permissions
                if (!sender.hasPermission(commandPermission.value)
                    && !(sender.isOp && commandPermission.isOpExempt)
                    && !playerOverridden
                )
                    throw PermissionException("You do not have permission for this command!")

                // If we have to use sub-commands, make sure we set one.
                if (useSubCommandsOnly) {
                    if (args.isEmpty())
                        throw ArgumentRequirementException("You must specify a sub-command for this command!")

                    subCommand = getSubCommandFor(args.first())

                    if (subCommand == null)
                        throw ArgumentRequirementException("The sub-command you specified is invalid!")
                }

                // We have a sub command!
                if (subCommand != null) {
                    val choppedArgs = if (args.size < 2) arrayOf() else args.copyOfRange(1, args.size)
                    handlePreSubCommand(sender, choppedArgs, subCommand)
                    subCommand.onCommand(sender, command, alias, choppedArgs)
                    try {
                        handlePostSubCommand(sender, args, subCommand)
                    } catch (ignored: EmptyHandlerException) {
                    }
                    return true
                }

                if (javaClass.annotations.find { it is AsyncCommand } != null) {
                    RunnableShorthand(this.plugin!!).async().with {
                        try {
                            actualDispatch(sender, alias, args)
                        } catch (t: CommandException) {
                            handleCommandException(t, args, sender)
                        } catch (t: Exception) {
                            handleCommandException(UnhandledCommandException(t), args, sender)
                        }
                    }.go()
                } else
                    actualDispatch(sender, alias, args)

            }
        } catch (t: CommandException) {
            handleCommandException(t, args, sender)
        } catch (t: Exception) {
            handleCommandException(UnhandledCommandException(t), args, sender)
        }
        return true
    }

    @Throws(CommandException::class)
    private fun actualDispatch(sender: CommandSender, alias: String, args: Array<String>) {
        val ctx = CommandContext.with(sender, alias, args)
        try {
            when (sender) {
                is Player -> handleCommandPlayer(ctx)
                is ConsoleCommandSender -> handleCommandConsole(ctx)
                is BlockCommandSender -> handleCommandBlock(ctx)
            }
        } catch (t: EmptyHandlerException) {
            handleCommandUnspecific(ctx)
        }
    }

    @Suppress("unused_parameter")
    private fun handleCommandException(ex: CommandException, args: Array<String>, sender: CommandSender) {
        if (ex is FriendlyException) sender.sendMessage(ex.getFriendlyMessage(this))
        else sender.sendMessage(ChatColor.RED.toString() + ex.javaClass.simpleName + ": " + ex.message + "!")
        if (ex is UnhandledCommandException) ex.causingException.printStackTrace()
    }

    private fun getSubCommandFor(s: String): ArrowPluginCommand? {
        return _subCommands.getInsensitive(s)?.first
    }

    private fun getSubCommandsForPartial(s: String): List<String> {
        /* Although we could check if it's a full command, there's a chance that names share the same beginning
        (val subCommand = getSubCommandFor(s)
        if (subCommand != null)
            return listOf(subCommand.name)
        */

        val s1 = s.lowercase()
        val commands: MutableList<String> = mutableListOf()
        _subCommands.keys.forEach { s2 ->
            if (s2.startsWith(s1))
                commands.add(s2)
        }
        return commands
    }

    // Overridable Handling Methods //
    /**
     * Runs before a sub command is executed
     */
    protected open fun handlePreSubCommand(
        sender: CommandSender,
        args: Array<String>,
        subCommand: ArrowPluginCommand
    ) {
    }

    /**
     * Runs after a sub command is executed
     */
    protected open fun handlePostSubCommand(
        sender: CommandSender,
        args: Array<String>,
        subCommand: ArrowPluginCommand
    ) {
    }

    /**
     * Runs when a player executes the command
     */
    @Throws(CommandException::class)
    protected open fun handleCommandPlayer(ctx: CommandContext) {
        throw EmptyHandlerException()
    }

    /**
     * Runs when the console executes the command
     */
    @Throws(CommandException::class)
    protected open fun handleCommandConsole(ctx: CommandContext) {
        throw EmptyHandlerException()
    }

    /**
     * Runs when a command block executes the command
     */
    @Throws(CommandException::class)
    protected open fun handleCommandBlock(ctx: CommandContext) {
        throw EmptyHandlerException()
    }

    /**
     * Runs when a specific handler isn't defined
     */
    @Throws(CommandException::class)
    protected open fun handleCommandUnspecific(ctx: CommandContext) {
        throw EmptyHandlerException()
    }

    /**
     * Runs when a player attempts to tab complete
     */
    protected open fun handleTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (useSubCommandsOnly || _subCommands.size > 0) return emptyList()
        val ss = mutableListOf<String>()
        val arg = if (args.isNotEmpty()) args[args.size - 1].lowercase() else ""
        plugin!!.server.onlinePlayers.forEach {
            if (it.name.lowercase().startsWith(arg)) ss.add(it.name)
        }
        return ss
    }

    // Tab Complete
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (commandPermission != null) {
            if (!sender.hasPermission(commandPermission.value) && !(sender.isOp && commandPermission.isOpExempt)) return emptyList()
        }

        if (args.size > 1) {
            val possibleHigherLevelCommand: ArrowPluginCommand? = getSubCommandFor(args[0])
            if (possibleHigherLevelCommand != null) {
                return possibleHigherLevelCommand.onTabComplete(sender, command, alias, args.copyOfRange(1, args.size))
            }
        } else if (args.size == 1) {
            val subCommandsForPartial = getSubCommandsForPartial(args[0]).toMutableList()
            if (subCommandsForPartial.size != 0) {
                subCommandsForPartial.addAll(handleTabComplete(sender, command, alias, args))
                return subCommandsForPartial
            }
        }
        return handleTabComplete(sender, command, alias, args)
    }

    override fun toString(): String {
        return "ArrowPluginCommand -> $formattedName"
    }
}