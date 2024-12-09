package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import archives.tater.neutron.NeutronState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin {
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/TargetPredicate;setBaseMaxDistance(D)Lnet/minecraft/entity/ai/TargetPredicate;")
    )
    private static TargetPredicate addNeutralCheck(TargetPredicate original) {
        if (Neutron.shouldKeepHostile(EntityType.ENDER_DRAGON)) return original;
        return original.setPredicate(NeutronState::beNeutralTo);
    }
}
