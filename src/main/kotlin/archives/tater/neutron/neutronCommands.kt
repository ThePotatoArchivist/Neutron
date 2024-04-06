package archives.tater.neutron

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.command.CommandException
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

fun neutronCommands(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    registryAccess: CommandRegistryAccess,
    environment: CommandManager.RegistrationEnvironment
) {
    dispatcher.command("neutron") {
        sub("global") {
            subExec("enable") {
                NeutronState[it].apply {
                    if (globalEnabled) throw CommandException(Text.translatable("command.neutron.global.enable.fail"))
                    globalEnabled = true
                }
                it.source.sendFeedback(Text.translatable("command.neutron.global.enable"), true)
                1
            }
            subExec("disable") {
                NeutronState[it].apply {
                    if (!globalEnabled) throw CommandException(Text.translatable("command.neutron.global.disable.fail"))
                    globalEnabled = false
                }
                it.source.sendFeedback(Text.translatable("command.neutron.global.disable"), true)
                1
            }
        }
        sub("player") {
            sub("enable") {
                argumentExec("player", EntityArgumentType.player()) {
                    val player = EntityArgumentType.getPlayer(it, "player")
                    NeutronState[it].run {
                        disabledPlayers.remove(player.uuid)
                        enabledPlayers.add(player.uuid)
                    }.let { success ->
                        if (!success) throw CommandException(Text.translatable("command.neutron.enable.player.fail", player.name))
                    }
                    it.source.sendFeedback(Text.translatable("command.neutron.enable.player.fail", player.name), true)
                    1
                }
            }
            sub("disable") {
                argumentExec("player", EntityArgumentType.player()) {
                    val player = EntityArgumentType.getPlayer(it, "player")
                    NeutronState[it].run {
                        enabledPlayers.remove(player.uuid)
                        disabledPlayers.add(player.uuid)
                    }.let { success ->
                        if (!success) throw CommandException(Text.translatable("command.neutron.disable.player.fail", player.name))
                    }
                    it.source.sendFeedback(Text.translatable("command.neutron.disable.player", player.name), true)
                    1
                }
            }
            subExec("reset") {
                NeutronState[it].apply {
                    enabledPlayers.clear()
                    disabledPlayers.clear()
                }
                it.source.sendFeedback(Text.translatable("command.neutron.reset"), true)
                1
            }
//            sub("query") {
//                argumentExec("player", EntityArgumentType.player()) {
//                    val player = EntityArgumentType.getPlayer(it, "player")
//
//                    0
//                }
//            }
        }
    }
}


// Libs -----------------------

private fun ServerCommandSource.sendFeedback(feedback: Text, broadcastToOps: Boolean) {
    this.sendFeedback({ feedback }, broadcastToOps)
}

inline fun <S> CommandDispatcher<S>.command(name: String, init: LiteralArgumentBuilder<S>.() -> Unit) {
    register(LiteralArgumentBuilder.literal<S>(name).apply(init))
}

fun <S> CommandDispatcher<S>.commandExec(name: String, command: Command<S>) {
    command(name) {
        executes(command)
    }
}

inline fun <S> ArgumentBuilder<S, *>.sub(name: String, init: LiteralArgumentBuilder<S>.() -> Unit) {
    then(LiteralArgumentBuilder.literal<S>(name).apply(init))
}

fun <S> ArgumentBuilder<S, *>.subExec(name: String, command: Command<S>) {
    sub(name) {
        executes(command)
    }
}

inline fun ArgumentBuilder<ServerCommandSource, *>.argument(name: String, type: ArgumentType<*>, init: RequiredArgumentBuilder<ServerCommandSource, *>.() -> Unit) {
    then(CommandManager.argument(name, type).apply(init))
}

fun ArgumentBuilder<ServerCommandSource, *>.argumentExec(name: String, type: ArgumentType<*>, command: Command<ServerCommandSource>) {
    argument(name, type) {
        executes(command)
    }
}

fun ArgumentBuilder<ServerCommandSource, *>.arguments(vararg arguments: Pair<String, ArgumentType<*>>, init: RequiredArgumentBuilder<ServerCommandSource, *>.() -> Unit) {
    var currentArgument = this
    arguments.forEach { (name, type) ->
        currentArgument.then(CommandManager.argument(name, type).also {
            currentArgument = it
        })
    }
    (currentArgument as RequiredArgumentBuilder<ServerCommandSource, *>).init()
}

fun ArgumentBuilder<ServerCommandSource, *>.argumentsExec(vararg arguments: Pair<String, ArgumentType<*>>, command: Command<ServerCommandSource>) {
    arguments(*arguments) {
        executes(command)
    }
}
