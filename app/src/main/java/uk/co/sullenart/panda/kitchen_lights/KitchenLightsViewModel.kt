package uk.co.sullenart.panda.kitchen_lights

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.sullenart.ble_coroutine.BleHandler
import java.util.*

class KitchenLightsViewModel(
    activity: ActivityResultCaller,
    context: Context,
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        private const val LEFT_DEVICE_ADDRESS = "7C:9E:BD:45:52:86"
        private const val RIGHT_DEVICE_ADDRESS = "7C:9E:BD:47:91:5E"
        private const val SERVICE_UUID = "1f2fefd6-b4e6-4fbb-8db9-41f9b2e18bf2"
        private const val CHARACTERISTIC_UUID = "41e2610d-eebd-4379-9cdb-a055a265de17"
    }

    private val leftHandler = BleHandler.fromAddress(activity, context, LEFT_DEVICE_ADDRESS)
    private val rightHandler = BleHandler.fromAddress(activity, context, RIGHT_DEVICE_ADDRESS)
    private val handlers = setOf(leftHandler, rightHandler)

    private var firstCommand = true
    private var btEnabledAtStart = false
    private var enableInProgress = false

    var loading: Boolean by mutableStateOf(false)
    var error: String by mutableStateOf("")
    var left: Boolean by mutableStateOf(true)

    val swatches = listOf(
        Swatch(0xFF, 0x00, 0x00),
        Swatch(0xAA, 0x55, 0x00),
        Swatch(0x55, 0xAA, 0x00),
        Swatch(0x00, 0xFF, 0x00),
        Swatch(0x00, 0xAA, 0x55),
        Swatch(0x00, 0x55, 0xAA),
        Swatch(0x00, 0x00, 0xFF),
        Swatch(0x55, 0x00, 0xAA),
        Swatch(0xAA, 0x00, 0x55),
        Swatch(0x00, 0x00, 0x00),
        Swatch(0xFF, 0xFF, 0xFF),
    )

    override fun onResume(owner: LifecycleOwner) {
        if (enableInProgress) {
            enableInProgress = false
        } else {
            firstCommand = true
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        handlers.forEach { handler ->
            viewModelScope.launch {
                handler.disconnect()
            }

            if (handler.isEnabled && !btEnabledAtStart && !firstCommand) {
                handler.disable()
            }
        }
    }

    fun onLeft() {
        left = true
    }

    fun onRight() {
        left = false
    }

    private fun Swatch.toJSON() =
        "{" +
                "\"command\":\"colour\", " +
                "\"red\":$red, " +
                "\"green\":$green, " +
                "\"blue\":$blue" +
                "}"

    fun onSwatch(swatch: Swatch) {
        viewModelScope.launch {
            loading = true
            val handler = if (left) leftHandler else rightHandler
            try {
                // Check for state of Bluetooth the first time - turn it on if needed
                // and remember the state in order to turn it off again.
                if (firstCommand) {
                    btEnabledAtStart = handler.isEnabled
                    firstCommand = false

                    if (!btEnabledAtStart) {
                        enableInProgress = true
                        handler.enable()
                    }
                }
                handler.connect()
                val service = handler.getService(UUID.fromString(SERVICE_UUID))
                val characteristic = handler.getCharacteristic(service, UUID.fromString(CHARACTERISTIC_UUID))
                handler.setValue(characteristic, swatch.toJSON())
            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
            }
            loading = false
        }
    }
}
