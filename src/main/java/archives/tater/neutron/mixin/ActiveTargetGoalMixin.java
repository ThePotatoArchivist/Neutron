package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import archives.tater.neutron.NeutronState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ActiveTargetGoal.class)
public class ActiveTargetGoalMixin {
    @Unique
    private boolean shouldAttack(LivingEntity target) {
        return !NeutronState.beNeutralTo(target);
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/TargetPredicate;setPredicate(Ljava/util/function/Predicate;)Lnet/minecraft/entity/ai/TargetPredicate;"),
            index = 0
    )
    private <T> Predicate<LivingEntity> checkAngerable(@Nullable Predicate<LivingEntity> predicate, @Local(argsOnly = true) Class<T> targetClass, @Local(argsOnly = true) MobEntity mob) {
        if (!targetClass.isAssignableFrom(PlayerEntity.class) || Neutron.shouldKeepHostile(mob)) return predicate;

        return predicate == null ? this::shouldAttack : predicate.and(this::shouldAttack);

    }
}
