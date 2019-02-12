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
    fun onDismiss()
}

abstract class BaseTopViewModel : ViewModel(), TopViewViewModel {
    private val relay = BehaviorRelay.createDefault(false)

    override fun setVisible(isVisible: Boolean) { relay.accept(isVisible) }

    override val isVisible: Observable<Boolean> = relay

    override fun onDismiss() {
        // TODO: nejaka drsnejsia logika
        setVisible(false)
    }
}

class SignpostViewModel @Inject constructor() : BaseTopViewModel() {

    override val text = ObservableField<String>("Signpost")

    //            Observable.interval(1000, TimeUnit.MILLISECONDS)
    //                    .map { it % 2L == 0L }
}

class IncidentViewModel @Inject constructor() : BaseTopViewModel() {

    override val text = ObservableField<String>("Incident")

//        Observable.interval(1500, TimeUnit.MILLISECONDS)
//            .map { it % 2L == 0L }

}

class EndOfRouteViewModel @Inject constructor() : BaseTopViewModel() {

    override val text = ObservableField<String>("EndOfRoute")

//        Observable.interval(2500, TimeUnit.MILLISECONDS)
//            .map { it % 2L == 0L }

}

class OnlyVisibleViewModel @Inject constructor() : BaseTopViewModel() {

    override val text = ObservableField<String>("OnlyVisible")

//        Observable.interval(3000, TimeUnit.MILLISECONDS)
//            .map { it % 2L == 0L }

}