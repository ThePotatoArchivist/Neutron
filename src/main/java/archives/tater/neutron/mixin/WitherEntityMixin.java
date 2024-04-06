package archives.tater.neutron.mixin;

import archives.tater.neutron.NeutronState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherEntity.class)
public class WitherEntityMixin {
    @WrapOperation(
            method = "method_6873",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isMobOrPlayer()Z")
    )
    private static boolean checkNeutral(LivingEntity instance, Operation<Boolean> original) {
        return !NeutronState.beNeutralTo(instance) && original.call(instance);
    }
}
