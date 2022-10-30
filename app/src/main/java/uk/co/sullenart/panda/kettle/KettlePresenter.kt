package uk.co.sullenart.panda.kettle

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import uk.co.sullenart.panda.BasePresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class KettlePresenter @Inject constructor(
    private val remoteApi: RemoteApi
) : BasePresenter() {

    lateinit var view: View

    fun onClicked() {
        add(
            remoteApi.turnOn3()
                .doOnSubscribe { view.showStatus("Turning on...") }
                .andThen(showTempStatus("Success"))
                .subscribeBy(
                    onError = {
                        Timber.e(it)
                        view.showStatus("Error turning on")
                    }
                )
        )
    }

    fun offClicked() {
        add(
            remoteApi.turnOff3()
                .doOnSubscribe { view.showStatus("Turning off..") }
                .andThen(showTempStatus("Success"))
                .subscribeBy(
                    onError = {
                        Timber.e(it)
                        view.showStatus("Error turning off")
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