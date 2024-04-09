package archives.tater.neutron.mixin;

import archives.tater.neutron.NeutronState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$FindTargetGoal")
public class PhantomFindTargetGoalMixin {
    @Unique
    private boolean shouldAttack(LivingEntity target) {
        return !NeutronState.beNeutralTo(target);
    }

    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/TargetPredicate;setBaseMaxDistance(D)Lnet/minecraft/entity/ai/TargetPredicate;")
    )
    private TargetPredicate checkNeutral(TargetPredicate original) {
        return original.setPredicate(this::shouldAttack);
    }
}
