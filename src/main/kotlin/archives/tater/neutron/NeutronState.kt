package archives.tater.neutron

import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.*

class NeutronState() : PersistentState() {
    var globalEnabled = false
    var enabledPlayers = mutableSetOf<UUID>()
    var disabledPlayers = mutableSetOf<UUID>()

    constructor(tag: NbtCompound) : this() {
        this.globalEnabled = tag.getBoolean(GLOBAL_ENABLED_KEY)
        this.enabledPlayers = tag.getList(ENABLED_PLAYERS_KEY, NbtElement.INT_ARRAY_TYPE.toInt()).map(NbtHelper::toUuid).toMutableSet()
        this.disabledPlayers = tag.getList(DISABLED_PLAYERS_KEY, NbtElement.INT_ARRAY_TYPE.toInt()).map(NbtHelper::toUuid).toMutableSet()
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        nbt.putBoolean(GLOBAL_ENABLED_KEY, globalEnabled)
        nbt.put(ENABLED_PLAYERS_KEY, enabledPlayers.mapToNbt(NbtHelper::fromUuid))
        nbt.put(DISABLED_PLAYERS_KEY, disabledPlayers.mapToNbt(NbtHelper::fromUuid))
        return nbt
    }

    override fun toString(): String {
        return "NeutronState { globalEnabled: $globalEnabled, enabledPlayers: $enabledPlayers, disabledPlayers: $disabledPlayers }"
    }

    fun shouldBeNeutralTo(entity: LivingEntity): Boolean {
        if (entity !is PlayerEntity) return false
        return if (globalEnabled) entity.uuid !in disabledPlayers else entity.uuid in enabledPlayers
    }

    companion object {
        private const val GLOBAL_ENABLED_KEY = "GlobalEnabled"
        private const val ENABLED_PLAYERS_KEY = "EnabledPlayers"
        private const val DISABLED_PLAYERS_KEY = "DisabledPlayers"

        operator fun get(server: MinecraftServer): NeutronState {
            val persistentStateManager = server.getWorld(World.OVERWORLD)!!.persistentStateManager

            val state = persistentStateManager.getOrCreate(::NeutronState, ::NeutronState, Neutron.MOD_ID)

            state.markDirty()

            return state
        }

        operator fun get(context: CommandContext<ServerCommandSource>): NeutronState {
            return get(context.source.server)
        }

        @JvmStatic
        fun beNeutralTo(entity: LivingEntity): Boolean {
            return entity.server?.let {
                NeutronState[it].shouldBeNeutralTo(entity)
            } ?: false
        }
    }
}
