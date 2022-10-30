package uk.co.sullenart.panda

import java.util.*

data class Lights(
    val colour: Colour
) : Payload() {
    companion object {
        const val MAX_BRIGHTNESS = 10
    }

    data class Colour(
        val red: Int,
        val green: Int,
        val blue: Int
    )

    data class Brightness(
        val brightness: Int
    ) : Payload()

    data class State(
        val active: Boolean
    ) : Payload()
}

data class Security(
    val state: String
) : Payload()

data class Summon(
    val code: Int
) : Payload()

class Ack : Payload()

open class Payload {
    val id: String = UUID.randomUUID().toString()
}