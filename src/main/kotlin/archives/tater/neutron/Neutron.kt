package archives.tater.neutron

import archives.tater.neutron.api.ShouldBeNeutralCallback
import archives.tater.neutron.mixin.PatrolEntityAccessor
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.PatrolEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Neutron : ModInitializer {
	const val MOD_ID = "neutron"
	@JvmField
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

	@JvmField
	val config = NeutronConfig().load()
	private val exceptions = config.exceptions.map(Registries.ENTITY_TYPE::get)

	@JvmStatic
	fun shouldKeepHostile(entity: LivingEntity) = shouldKeepHostile(entity.type)
	@JvmStatic
	fun shouldKeepHostile(entityType: EntityType<*>) = entityType in exceptions

	@JvmStatic
	fun beNeutralTo(entity: LivingEntity, target: LivingEntity) =
		(NeutronState.beNeutralTo(target)
				&& (!shouldKeepHostile(entity) || (config.excludePatrollers && entity is PatrolEntity && (entity as PatrolEntityAccessor).isPatrolling)))
				|| ShouldBeNeutralCallback.EVENT.invoker().shouldBeNeutralTo(entity, target)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register(::neutronCommands)

		ArgumentTypeRegistry.registerArgumentType(Identifier(MOD_ID, "enabled_mode"), Mode.ArgumentType::class.java, ConstantArgumentSerializer.of(Mode::ArgumentType))

		if (FabricLoader.getInstance().isDevelopmentEnvironment)
			ShouldBeNeutralCallback.registerPlayer { entity, target ->
				entity is ZombieEntity && Hand.entries.any { target.getStackInHand(it).isOf(Items.TOTEM_OF_UNDYING) }
			}
	}
}
