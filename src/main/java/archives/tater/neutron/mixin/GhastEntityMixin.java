package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import archives.tater.neutron.ai.GhastRevengeGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhastEntity.class)
public class GhastEntityMixin extends FlyingEntity {
    protected GhastEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At("TAIL")
    )
    private void addRevengeGoal(CallbackInfo ci) {
        if (!Neutron.shouldKeepHostile(EntityType.GHAST))
            targetSelector.add(2, new GhastRevengeGoal((GhastEntity) (Object) this, PlayerEntity.class));
    }
}
