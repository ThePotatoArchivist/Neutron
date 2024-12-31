package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinSpecificSensor.class)
public class PiglinSpecificSensorMixin {
    @WrapOperation(
            method = "sense",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PiglinBrain;wearsGoldArmor(Lnet/minecraft/entity/LivingEntity;)Z")
    )
    private boolean checkNeutral(LivingEntity entity, Operation<Boolean> original, @Local(argsOnly = true) LivingEntity entity2) {
        if (Neutron.shouldKeepHostile(EntityType.PIGLIN)) return original.call(entity);
        return original.call(entity) || Neutron.beNeutralTo(entity2, entity);
    }
}
