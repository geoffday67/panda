package uk.co.sullenart.panda.hobby

import android.graphics.Color
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import uk.co.sullenart.panda.BasePresenter
import uk.co.sullenart.panda.Lights
import uk.co.sullenart.panda.Payload
import uk.co.sullenart.panda.Security
import uk.co.sullenart.panda.mqtt.RxMqttClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HobbyPresenter @Inject constructor(
    private val rxMqttClient: RxMqttClient,
    private val gson: Gson
) : BasePresenter() {
    companion object {
        const val MQTT_TOPIC = "hobby/lights"
    }

    lateinit var view: View

    fun colourClicked (value: Int) {
        val colour = Lights.Colour(
            red = Color.red(value),
            green = Color.green(value),
            blue = Color.blue(value)
        )
        val payload = Lights(colour)
        sendPayload(payload)
    }

    fun brightnessClicked(value: Int) {
        sendPayload(Lights.Brightness(value))
    }

    fun lightsOnClicked() {
        sendPayload(Lights.State(true))
    }

    fun lightsOffClicked() {
        sendPayload(Lights.State(false))
    }

    private fun sendPayload(payload: Payload) {
        //view.showStatus("Sending...")
        add(
            rxMqttClient.publish(MQTT_TOPIC, gson.toJson(payload).toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //.andThen(showTempStatus("Success"))
                .subscribeBy(
                    onError = {
                        Timber.e(it)
                        view.showStatus("Error sending")
                    }
                )
        )
    }

    private fun showTempStatus(text: String): Completable {
        return Completable.complete()
            .doOnSubscribe { view.showStatus(text) }
            .subscribeOn(AndroidSchedulers.mainThread())
            .delay(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .doOnComplete { view.clearStatus() }
    }

    interface View {
        fun clearStatus()
        fun showStatus(text: String)
    }
}
