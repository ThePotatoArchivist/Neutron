package archives.tater.neutron

import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import java.util.*

object NeutronMemoryModuleType {
    @JvmField
    val NEAREST_VISIBLE_TARGETABLE_NONNEUTRAL_PLAYER: MemoryModuleType<PlayerEntity> =
        Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier(Neutron.MOD_ID, "nearest_visible_targetable_player"), MemoryModuleType<PlayerEntity>(Optional.empty()))

}
