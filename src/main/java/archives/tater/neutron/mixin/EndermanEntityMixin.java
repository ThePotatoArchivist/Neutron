package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin extends HostileEntity {
    protected EndermanEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "isPlayerStaring",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkNeutrality(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (Neutron.config.preventEndermanEyeContact && Neutron.beNeutralTo(this, player))
            cir.setReturnValue(false);
    }
}
