package archives.tater.neutron

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.mob.*
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Neutron : ModInitializer {
	const val MOD_ID = "neutron"
	@JvmField
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

	@JvmStatic
	fun shouldKeepHostile(entity: LivingEntity): Boolean {
		return when(entity) {
			is PillagerEntity -> false
			is WitherSkeletonEntity, is PiglinBruteEntity, is GuardianEntity, is ShulkerEntity, is IllagerEntity, is RavagerEntity, is VexEntity, is WardenEntity, is WitherEntity -> true
			else -> false
		}
	}

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register(::neutronCommands)

		ArgumentTypeRegistry.registerArgumentType(Identifier(MOD_ID, "enabled_mode"), Mode.ArgumentType::class.java, ConstantArgumentSerializer.of(Mode::ArgumentType))
	}
}
