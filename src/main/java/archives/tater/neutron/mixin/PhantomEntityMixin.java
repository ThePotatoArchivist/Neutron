package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import archives.tater.neutron.ai.NeutronRevengeGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin extends FlyingEntity {
    protected PhantomEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At("TAIL")
    )
    private void addRevengeGoal(CallbackInfo ci) {
        if (!Neutron.shouldKeepHostile(this))
            targetSelector.add(2, new NeutronRevengeGoal((PhantomEntity) (Object) this, 128, PlayerEntity.class));
    }
}
