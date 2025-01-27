package archives.tater.neutron.ai

import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.TrackTargetGoal
import net.minecraft.entity.mob.MobEntity

class NeutronRevengeGoal(mob: MobEntity, private val maxDistance: Double, private vararg val revengeTypes: Class<*>) : TrackTargetGoal(mob, true) {
    private var lastAttackedTime = 0

    constructor(mob: MobEntity, vararg revengeTypes: Class<*>) : this(mob, 0.0, *revengeTypes)

    override fun canStart(): Boolean {
        val attacker = mob.attacker
        if (mob.lastAttackedTime == lastAttackedTime || attacker == null) return false
        if (revengeTypes.any { it.isInstance(attacker) }) return true

        return canTrack(attacker, VALID_AVOIDABLES_PREDICATE)
    }

    override fun start() {
        mob.target = mob.attacker
        target = mob.target
        lastAttackedTime = mob.lastAttackedTime
        maxTimeWithoutVisibility = 300

        super.start()
    }

    override fun getFollowRange(): Double = if (maxDistance == 0.0) super.getFollowRange() else maxDistance

    companion object {
        private val VALID_AVOIDABLES_PREDICATE: TargetPredicate =
            TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor()
    }
}
