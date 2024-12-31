package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(WitherEntity.class)
public class WitherEntityMixin extends HostileEntity {
    @Shadow @Final private static Predicate<LivingEntity> CAN_ATTACK_PREDICATE;

    protected WitherEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "mobTick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/boss/WitherEntity;HEAD_TARGET_PREDICATE:Lnet/minecraft/entity/ai/TargetPredicate;")
    )
    private TargetPredicate checkNeutral(TargetPredicate original) {
        if (Neutron.shouldKeepHostile(EntityType.WITHER)) return original;
        return original.copy().setPredicate(CAN_ATTACK_PREDICATE.and(target -> !Neutron.beNeutralTo(this, target)));
    }
}
