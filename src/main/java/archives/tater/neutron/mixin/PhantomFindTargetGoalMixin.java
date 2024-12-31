package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$FindTargetGoal")
public class PhantomFindTargetGoalMixin {
    @Shadow @Final PhantomEntity field_7319;

    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/TargetPredicate;setBaseMaxDistance(D)Lnet/minecraft/entity/ai/TargetPredicate;")
    )
    private TargetPredicate checkNeutral(TargetPredicate original) {
        if (Neutron.shouldKeepHostile(EntityType.PHANTOM)) return original;
        return original.setPredicate(target -> !Neutron.beNeutralTo(this.field_7319, target));
    }
}
