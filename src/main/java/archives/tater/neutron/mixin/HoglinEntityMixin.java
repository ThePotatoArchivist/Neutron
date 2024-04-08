package archives.tater.neutron.mixin;

import archives.tater.neutron.NeutronMemoryModuleType;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HoglinEntity.class)
public class HoglinEntityMixin {
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/brain/MemoryModuleType;NEAREST_VISIBLE_TARGETABLE_PLAYER:Lnet/minecraft/entity/ai/brain/MemoryModuleType;")
    )
    private static MemoryModuleType<PlayerEntity> replaceType(MemoryModuleType<PlayerEntity> original) {
        return NeutronMemoryModuleType.NEAREST_VISIBLE_TARGETABLE_NONNEUTRAL_PLAYER;
    }
}
