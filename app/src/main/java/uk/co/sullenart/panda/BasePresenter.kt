package uk.co.sullenart.panda

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter {
    private val compositeDisposable = CompositeDisposable()

    protected fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    open fun start() {}

    open fun stop() {
        compositeDisposable.clear()
    }

    open fun pause() {}
    open fun resume() {}
}
