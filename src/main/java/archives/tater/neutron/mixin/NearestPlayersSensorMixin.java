package archives.tater.neutron.mixin;

import archives.tater.neutron.NeutronMemoryModuleType;
import archives.tater.neutron.NeutronState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NearestPlayersSensor.class)
public class NearestPlayersSensorMixin {
    @Inject(
            method = "sense",
            at = @At("TAIL")
    )
    private void checkNonNeutral(ServerWorld world, LivingEntity entity, CallbackInfo ci, @Local(ordinal = 1) List<PlayerEntity> playerList, @Local Brain<?> brain) {
        brain.remember(
                NeutronMemoryModuleType.NEAREST_VISIBLE_TARGETABLE_NONNEUTRAL_PLAYER,
                playerList.stream().filter(player -> NearestPlayersSensor.testAttackableTargetPredicate(entity, player) && !NeutronState.beNeutralTo(player)).findFirst()
        );
    }
}
