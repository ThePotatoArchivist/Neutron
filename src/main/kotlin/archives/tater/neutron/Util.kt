package archives.tater.neutron

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

fun <T> Iterable<T>.mapToNbt(transform: (value: T) -> NbtElement): NbtList {
    val list = NbtList()
    this.forEach {
        list.add(transform(it))
    }
    return list
}
