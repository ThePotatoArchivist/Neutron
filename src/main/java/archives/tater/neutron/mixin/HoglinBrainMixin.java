package archives.tater.neutron.mixin;

import archives.tater.neutron.NeutronMemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HoglinBrain.class)
public abstract class HoglinBrainMixin {
    @ModifyArg(
            method = "getNearestVisibleTargetablePlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;getOptionalRegisteredMemory(Lnet/minecraft/entity/ai/brain/MemoryModuleType;)Ljava/util/Optional;"),
            index = 0
    )
    private static MemoryModuleType<PlayerEntity> checkNeutral(MemoryModuleType<PlayerEntity> type) {
        return NeutronMemoryModuleType.NEAREST_VISIBLE_TARGETABLE_NONNEUTRAL_PLAYER;
    }
}
