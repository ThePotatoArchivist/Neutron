package archives.tater.neutron

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.serialization.Codec
import net.minecraft.command.CommandException
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable

enum class Mode(val id: String) : StringIdentifiable {
    ENABLE("enable"),
    DISABLE("disable");

    override fun asString() = id

    fun toBoolean(): Boolean = this == ENABLE

    companion object {
        val CODEC: Codec<Mode> = StringIdentifiable.createCodec { entries.toTypedArray() }
    }

    class ArgumentType : EnumArgumentType<Mode>(CODEC, ::values)
}

fun neutronCommands(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    registryAccess: CommandRegistryAccess,
    environment: CommandManager.RegistrationEnvironment
) {

    dispatcher.command("neutron") {
        sub("global") {
            executes {
                it.source.sendFeedback(Text.translatable("command.neutron.global.${if(NeutronState[it].globalEnabled) "enable" else "disable"}"), false)

                0
            }

            argumentExec("mode", Mode.ArgumentType()) {
                val mode = it.getArgument("mode", Mode::class.java)
                val enable = mode.toBoolean()

                NeutronState[it].apply {
                    if (globalEnabled == enable) throw CommandException(Text.translatable("command.neutron.global.${mode.id}.fail"))
                    globalEnabled = enable
                }

                it.source.sendFeedback(Text.translatable("command.neutron.global.${mode.id}.success"), true)

                1
            }
        }

        sub("player") {
            argument("player", EntityArgumentType.player()) {
                executes {
                    val player = EntityArgumentType.getPlayer(it, "player")
                    val status = NeutronState[it].run {
                        if (globalEnabled) {
                            player.uuid !in disabledPlayers
                        } else {
                            player.uuid in enabledPlayers
                        }
                    }
                    it.source.sendFeedback(Text.translatable("command.neutron.player.$status"), false)
                    0
                }

                argumentExec("mode", Mode.ArgumentType()) {
                    val mode = it.getArgument("mode", Mode::class.java)
                    val player = EntityArgumentType.getPlayer(it, "player")

                    NeutronState[it].run {
                        if (mode.toBoolean()) {
                            if (globalEnabled) {
                                disabledPlayers.remove(player.uuid)
                            } else {
                                enabledPlayers.add(player.uuid)
                            }
                        } else {
                            if (globalEnabled) {
                                disabledPlayers.add(player.uuid)
                            } else {
                                enabledPlayers.remove(player.uuid)
                            }
                        }
                    }.let { success ->
                        if (!success) throw CommandException(
                            Text.translatable(
                                "command.neutron.player.${mode.id}.fail",
                                player.name
                            )
                        )
                    }
                    it.source.sendFeedback(
                        Text.translatable("command.neutron.player.${mode.id}.success", player.name),
                        true
                    )
                    1
                }
            }
        }

        subExec("resetall") {
            NeutronState[it].apply {
                enabledPlayers.clear()
                disabledPlayers.clear()
            }
            it.source.sendFeedback(Text.translatable("command.neutron.reset"), true)
            1
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

//fun ArgumentBuilder<ServerCommandSource, *>.arguments(vararg arguments: Pair<String, ArgumentType<*>>, init: RequiredArgumentBuilder<ServerCommandSource, *>.() -> Unit) {
//    var currentArgument = this
//    arguments.forEach { (name, type) ->
//        currentArgument.then(CommandManager.argument(name, type).also {
//            currentArgument = it
//        })
//    }
//    (currentArgument as RequiredArgumentBuilder<ServerCommandSource, *>).init()
//}
//
//fun ArgumentBuilder<ServerCommandSource, *>.argumentsExec(vararg arguments: Pair<String, ArgumentType<*>>, command: Command<ServerCommandSource>) {
//    arguments(*arguments) {
//        executes(command)
//    }
//}

// Experimental
//fun ArgumentBuilder<ServerCommandSource, *>.inferArguments(function: KFunction<Int>) {
//    val parameters = function.parameters.map {
//        it.name to when (it.type) {
//            PlayerEntity::class.starProjectedType -> EntityArgumentType.player()
//            else -> throw IllegalArgumentException("Parameter was not a valid type")
//        }
//    }
//
//    var action = fun ArgumentBuilder<ServerCommandSource, *>.() {
//        executes {
//            function.callBy()
//        }
//    }
//
//    parameters.reversed().forEach { (name, type) ->
//        val argument = CommandManager.argument(name, type).apply(action)
//        action = {
//            then(argument)
//        }
//    }
//
//    this.action()
//}
