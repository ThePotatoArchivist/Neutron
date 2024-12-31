package archives.tater.neutron.api

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity


fun interface ShouldBeNeutralCallback {
    fun shouldBeNeutralTo(entity: LivingEntity, target: LivingEntity): Boolean

    companion object {
        @JvmField
        val EVENT: Event<ShouldBeNeutralCallback> = EventFactory.createArrayBacked(ShouldBeNeutralCallback::class.java) { listeners ->
            ShouldBeNeutralCallback { entity, target ->
                listeners.any { it.shouldBeNeutralTo(entity, target) }
            }
        }

        @JvmStatic
        fun register(callBack: ShouldBeNeutralCallback) {
            EVENT.register(callBack)
        }

        @JvmStatic
        inline fun registerPlayer(crossinline callback: (entity: LivingEntity, target: PlayerEntity) -> Boolean) {
            register { entity, target -> target is PlayerEntity && callback(entity, target) }
        }
    }
}
