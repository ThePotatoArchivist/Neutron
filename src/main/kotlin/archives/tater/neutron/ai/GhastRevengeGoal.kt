package archives.tater.neutron.ai

import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.TrackTargetGoal
import net.minecraft.entity.mob.GhastEntity

class GhastRevengeGoal(mob: GhastEntity, private vararg val revengeTypes: Class<*>) : TrackTargetGoal(mob, true) {
    private var lastAttackedTime = 0

    override fun canStart(): Boolean {
        val attacker = mob.attacker
        if (mob.lastAttackedTime == lastAttackedTime || attacker == null) return false
        if (revengeTypes.any { it.isAssignableFrom(attacker::class.java) }) return true

        return canTrack(attacker, VALID_AVOIDABLES_PREDICATE)
    }

    override fun start() {
        mob.target = mob.attacker
        target = mob.target
        lastAttackedTime = mob.lastAttackedTime
        maxTimeWithoutVisibility = 300

        super.start()
    }

    companion object {
        private val VALID_AVOIDABLES_PREDICATE: TargetPredicate =
            TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor()
    }
}
