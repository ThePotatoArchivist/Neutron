package archives.tater.neutron

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import org.spaceserve.config.IConfigure

@Serializable
data class NeutronConfig(
    var exceptions: List<@Serializable(with = IdentifierSerializer::class) Identifier> = listOf(
        // From woodland mansion & Raids only
        EntityType.VINDICATOR,
        EntityType.EVOKER,
        EntityType.VEX,
        EntityType.RAVAGER,
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
        // From end city only
        EntityType.SHULKER,
        // From ender pear only
        EntityType.ENDERMITE,
        // Bosses
        EntityType.WITHER,
        EntityType.ENDER_DRAGON
    ).map(Registries.ENTITY_TYPE::getId).toMutableList()
) : IConfigure {
    override val fileName: String = "neutron"

    override fun load() = super.load() as NeutronConfig

    object IdentifierSerializer : KSerializer<Identifier> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("identifier", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Identifier = Identifier(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: Identifier) {
            encoder.encodeString(value.toString())
        }
    }
}
