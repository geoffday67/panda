package uk.co.sullenart.panda.mqtt

import uk.co.sullenart.panda.*
import java.io.InputStream
import java.io.OutputStream

open class Packet() {
    companion object {
        const val CONTROL_PACKET_TYPE_CONNECT = 1.toByte()
        const val CONTROL_PACKET_TYPE_CONNACK = 2.toByte()
        const val CONTROL_PACKET_TYPE_PUBLISH = 3.toByte()
        const val CONTROL_PACKET_TYPE_DISCONNECT = 14.toByte()

        fun create(input: InputStream): Packet {
            val response = ByteArray(1024)
            val count = input.read(response)
            if (count < 2) {
                return UnknownPacket()
            }

            return when (response[0].shr(4)) {
                CONTROL_PACKET_TYPE_CONNACK -> ConnackPacket(response)
                else -> UnknownPacket()
            }
        }
    }

    protected val fixedHeader = mutableListOf<Byte>()
    protected val variableHeader = mutableListOf<Byte>()
    protected val payload = mutableListOf<Byte>()

    fun send(output: OutputStream) {
        with(output) {
            write(fixedHeader.toByteArray())
            write(variableHeader.toByteArray())
            write(payload.toByteArray())
        }
    }

    protected fun encodeString(text: String): List<Byte> =
        mutableListOf<Byte>().apply {
            add((text.length / 256).toByte())
            add((text.length % 256).toByte())
            addAll(text.toByteArray().toList())
        }

    protected fun encodeRemainingLength(length: Int): List<Byte> {
        val result = mutableListOf<Byte>()
        var remaining = length

        while (true) {
            val b = (remaining % 128).toByte()
            remaining -= b;
            if (remaining == 0) {
                result.add(b)
                break
            }
            b.setBit(7)
            result.add(b)
        }

        return result
    }

    protected fun encodeFixedHeader(type: Byte): List<Byte> =
        mutableListOf<Byte>().apply {
            add(type.shl(4))
            addAll(encodeRemainingLength(variableHeader.size + payload.size))
        }
}

class ConnectPacket(
    clientId: String
) : Packet() {
    init {
        variableHeader.set(byteListOfAny(0x00, 0x04, 'M', 'Q', 'T', 'T', 0x04, 0x02, 0x00, 0x00))
        payload.set(encodeString(clientId))
        fixedHeader.set(encodeFixedHeader(CONTROL_PACKET_TYPE_CONNECT))
    }
}

class ConnackPacket() : Packet() {
    constructor(data: ByteArray) : this() {
        status = data[3].toInt()
    }

    private var status: Int = 0

    val isAccepted: Boolean
        get() = status == 0
}

class PublishPacket(
    topic: String,
    content: String
) : Packet() {
    init {
        variableHeader.set(encodeString(topic))
        payload.set(content.toByteArray().toList())

        // Just set the packet type, leave the other bits as zero => QoS = 0 and don't retain
        fixedHeader.set(encodeFixedHeader(CONTROL_PACKET_TYPE_PUBLISH))
    }
}

class DisconnectPacket() : Packet() {
    init {
        fixedHeader.set(encodeFixedHeader(CONTROL_PACKET_TYPE_DISCONNECT))
    }
}

class UnknownPacket : Packet()
