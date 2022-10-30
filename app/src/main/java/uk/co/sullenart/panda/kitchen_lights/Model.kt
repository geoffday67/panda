package uk.co.sullenart.panda.kitchen_lights

import com.google.gson.annotations.SerializedName

data class ColourCommand(
    @SerializedName("command") val command: String = "colour",
    @SerializedName("values") val values: ColourValues = ColourValues()
) : Command()

data class ColourValues(
    @SerializedName("red") var red: Int = 0,
    @SerializedName("green") var green: Int = 0,
    @SerializedName("blue") var blue: Int = 0
)

data class OffCommand(
    @SerializedName("command") val command: String = "off"
) : Command()

open class Command {
}

data class Light (
    var name: String,
    var macAddress: String
) {
    override fun toString(): String{
        return name
    }
}


