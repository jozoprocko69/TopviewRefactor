package com.example.mbrecka.topviewrefactor

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject

interface TopViewViewModel {
    val text: ObservableField<String>
    val isVisible: Observable<Boolean>
    fun setVisible(isVisible: Boolean)
}

class SignpostViewModel @Inject constructor() : ViewModel(), TopViewViewModel {

    override val text = ObservableField<String>("Signpost")

    private val relay = BehaviorRelay.create<Boolean>()

    override fun setVisible(isVisible: Boolean) {
        relay.accept(isVisible)
    }
    override val isVisible: Observable<Boolean> = relay
    //            Observable.interval(1000, TimeUnit.MILLISECONDS)
    //                    .map { it % 2L == 0L }
}

class IncidentViewModel @Inject constructor() : ViewModel(), TopViewViewModel {

    override val text = ObservableField<String>("Incident")

    private val relay = BehaviorRelay.create<Boolean>()

    override fun setVisible(isVisible: Boolean) {
        relay.accept(isVisible)
    }
    override val isVisible: Observable<Boolean> = relay
//        Observable.interval(1500, TimeUnit.MILLISECONDS)
//            .map { it % 2L == 0L }

}

class EndOfRouteViewModel @Inject constructor() : ViewModel(), TopViewViewModel {

    override val text = ObservableField<String>("EndOfRoute")

    private val relay = BehaviorRelay.create<Boolean>()

    override fun setVisible(isVisible: Boolean) {
        relay.accept(isVisible)
    }
    override val isVisible: Observable<Boolean> = relay
//        Observable.interval(2500, TimeUnit.MILLISECONDS)
//            .map { it % 2L == 0L }

}

class OnlyVisibleViewModel @Inject constructor() : ViewModel(), TopViewViewModel {

    override val text = ObservableField<String>("OnlyVisible")

    private val relay = BehaviorRelay.create<Boolean>()

    override fun setVisible(isVisible: Boolean) {
        relay.accept(isVisible)
    }
    override val isVisible: Observable<Boolean> = relay
//        Observable.interval(3000, TimeUnit.MILLISECONDS)
//            .map { it % 2L == 0L }

}