package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.BreezeAttackablesSensor;
import net.minecraft.entity.mob.BreezeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(BreezeAttackablesSensor.class)
public class BreezeAttackablesSensorMixin {
    @ModifyExpressionValue(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/BreezeEntity;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0)
    )
    private Stream<LivingEntity> checkNeutral(Stream<LivingEntity> original, @Local(argsOnly = true) BreezeEntity breezeEntity) {
        if (Neutron.shouldKeepHostile(breezeEntity)) return original;
        return original.filter(entity -> !Neutron.beNeutralTo(breezeEntity, entity));
    }
}
