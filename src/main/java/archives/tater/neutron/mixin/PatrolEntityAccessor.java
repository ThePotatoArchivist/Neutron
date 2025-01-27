package archives.tater.neutron.mixin;

import net.minecraft.entity.mob.PatrolEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PatrolEntity.class)
public interface PatrolEntityAccessor {
    @Accessor("patrolling")
    boolean isPatrolling();
}
