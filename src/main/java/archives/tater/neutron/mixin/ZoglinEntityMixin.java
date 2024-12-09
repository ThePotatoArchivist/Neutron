package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import archives.tater.neutron.NeutronState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ZoglinEntity.class)
public class ZoglinEntityMixin {
    @WrapOperation(
            method = "shouldAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/sensor/Sensor;testAttackableTargetPredicate(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;)Z")
    )
    boolean checkNeutral(LivingEntity entity, LivingEntity target, Operation<Boolean> original) {
        if (Neutron.shouldKeepHostile(EntityType.ZOGLIN)) return original.call(entity, target);
        return !NeutronState.beNeutralTo(target) && original.call(entity, target);
    }
}
