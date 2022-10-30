package uk.co.sullenart.panda.summoner

import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import uk.co.sullenart.panda.*
import uk.co.sullenart.panda.mqtt.RxMqttClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SummonerPresenter @Inject constructor(
    private val client: RxMqttClient,
    private val gson: Gson
) : BasePresenter() {
    companion object {
        private const val SUMMON_COFFEE = 1
        private const val SUMMON_LUNCH = 2
        private const val SUMMON_DINNER = 3
        private const val SUMMON_TEA = 4

        private const val TOPIC = "summon/dial"
        private const val TEST_TOPIC = "summon-test"
    }

    lateinit var view: View

    fun coffeeClicked() {
        sendPayload(Summon(SUMMON_COFFEE), view.isTestMode())
    }

    fun lunchClicked() {
        sendPayload(Summon(SUMMON_LUNCH), view.isTestMode())
    }

    fun dinnerClicked() {
        sendPayload(Summon(SUMMON_DINNER), view.isTestMode())
    }

    fun teaClicked() {
        sendPayload(Summon(SUMMON_TEA), view.isTestMode())
    }

    private fun sendPayload(payload: Payload, testMode: Boolean) {
        view.showStatus("Summoning...")
        add(
            client.publish(if (testMode) TEST_TOPIC else TOPIC, gson.toJson(payload).toString())
                .andThen(if (testMode) Completable.complete() else awaitAck(payload))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(showTempStatus("Success"))
                .subscribeBy(
                    onError = { view.showStatus("Error: $it") }
                )
        )
    }

    private fun awaitAck(payload: Payload): Completable {
        return Completable.complete()
        /*return client.watch("ack")
            .map { gson.fromJson(it.toString(), Ack::class.java) }
            .filter { it.id == payload.id }
            .take(1)
            .ignoreElements()*/
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
        fun isTestMode(): Boolean
    }
}
