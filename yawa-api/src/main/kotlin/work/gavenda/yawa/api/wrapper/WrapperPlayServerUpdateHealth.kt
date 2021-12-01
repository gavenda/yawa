package work.gavenda.yawa.api.wrapper

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer

class WrapperPlayServerUpdateHealth : AbstractPacket(PacketContainer(type), type) {

    companion object {
        val type: PacketType = PacketType.Play.Server.UPDATE_HEALTH
    }

    fun writeHealth(value: Float) {
        handle.float.write(0, value)
    }

    fun writeFood(value: Int) {
        handle.integers.write(0, value)
    }

    fun writeFoodSaturation(value: Float) {
        handle.float.write(1, value)
    }

    init {
        handle.modifier.writeDefaults()
    }
}