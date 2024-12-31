package archives.tater.neutron.mixin;

import archives.tater.neutron.Neutron;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(NearestPlayersSensor.class)
public class NearestPlayersSensorMixin {
    @ModifyExpressionValue(
            method = "sense",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0)
    )
    private Stream<ServerPlayerEntity> checkNonNeutral(Stream<ServerPlayerEntity> original, @Local(argsOnly = true) LivingEntity entity) {
        return Neutron.shouldKeepHostile(entity) ? original : original.filter(player -> !Neutron.beNeutralTo(entity, player));
    }
}
