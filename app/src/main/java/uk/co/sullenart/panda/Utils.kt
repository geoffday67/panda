package uk.co.sullenart.panda

import kotlin.experimental.and
import kotlin.experimental.or

fun Byte.shl(count: Int) =
    this.toInt().shl(count).toByte()

fun Byte.shr(count: Int) =
    this.toInt().shr(count).toByte()

fun Byte.setBit(bit: Int) =
    this or 0x01.shl(bit).toByte()

fun Byte.resetBit(bit: Int) =
    this and 0x01.shl(bit).inv().toByte()

fun Byte.isBitSet(bit: Int): Boolean =
    this and (0x01 shl bit).toByte() != 0x00.toByte()

fun <T> MutableCollection<T>.set(items: Iterable<T>) {
    with(this) {
        clear()
        addAll(items)
    }
}

fun byteListOfAny(vararg values: Any): List<Byte> = ByteArray(values.size) { position ->
    when (val value = values[position]) {
        is Int -> value.toByte()
        is Char -> value.toByte()
        else -> 0x00
    }
}.toList()
