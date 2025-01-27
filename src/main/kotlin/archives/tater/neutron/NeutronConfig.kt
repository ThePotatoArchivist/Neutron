package archives.tater.neutron

import kotlinx.serialization.Serializable
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import org.spaceserve.config.IConfigure
import org.spaceserve.config.serializers.IdentifierSerializer

@Serializable
data class NeutronConfig(
    @JvmField
    var exceptions: List<@Serializable(with = IdentifierSerializer::class) Identifier> = listOf(
        // From woodland mansion, raids, & outposts
        EntityType.VINDICATOR,
        EntityType.EVOKER,
        EntityType.VEX,
        EntityType.RAVAGER,
        EntityType.PILLAGER,
        // From ancient city only
        EntityType.WARDEN,
        // From Ocean monument only
        EntityType.GUARDIAN,
        EntityType.ELDER_GUARDIAN,
        // From nether fortress only
        EntityType.WITHER_SKELETON,
        EntityType.BLAZE,
        // From bastion only
        EntityType.PIGLIN_BRUTE,
        // From trial chamber only
        EntityType.BREEZE,
        // From end city only
        EntityType.SHULKER,
        // From ender pear only
        EntityType.ENDERMITE,
        // Bosses
        EntityType.WITHER,
        // Ender dragon is unmodified
    ).map(Registries.ENTITY_TYPE::getId).toMutableList(),
    @JvmField
    var preventEndermanEyeContact: Boolean = false,
    @JvmField
    var excludePatrollers: Boolean = true,
) : IConfigure {
    override val fileName get() = "neutron"

    override fun load() = super.load() as NeutronConfig
}
