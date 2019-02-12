package com.example.mbrecka.topviewrefactor

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.mbrecka.topviewrefactor.di.ViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mbrecka.topviewrefactor.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.view.animation.LayoutAnimationController
import android.view.animation.OvershootInterpolator
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlin.math.sign

// TODO: anchor na posledny item recyclera
// TODO: recycler wrap height
// TODO: rulebase
// TODO: animacia itemu
// TODO: item nech sa vysunie popod predosly item, nie ponad

class ViewModelRulebase {
    fun applyRules(it: List<TopViewViewModel>): List<TopViewViewModel> = it
}

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModelRulebase = ViewModelRulebase()

    val adapter = TopViewAdapter()

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signpostViewModel = ViewModelProviders.of(this, viewModelFactory).get(SignpostViewModel::class.java)
        val incidentViewModel = ViewModelProviders.of(this, viewModelFactory).get(IncidentViewModel::class.java)
        val endOfRouteViewModel = ViewModelProviders.of(this, viewModelFactory).get(EndOfRouteViewModel::class.java)
        val onlyVisibleViewModel = ViewModelProviders.of(this, viewModelFactory).get(OnlyVisibleViewModel::class.java)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(this@MainActivity)
            recycler.itemAnimator = SlideInDownAnimator()
//            recycler.itemAnimator = SlideInDownAnimator(OvershootInterpolator(1f))

            signpostSwitch.setOnCheckedChangeListener { _, isChecked -> signpostViewModel.setVisible(isChecked) }
            incidentSwitch.setOnCheckedChangeListener { _, isChecked -> incidentViewModel.setVisible(isChecked) }
            endOfRouteSwitch.setOnCheckedChangeListener { _, isChecked -> endOfRouteViewModel.setVisible(isChecked) }
            onlyVisibleSwitch.setOnCheckedChangeListener { _, isChecked -> onlyVisibleViewModel.setVisible(isChecked) }
        }

        val nevyskakujlintdpc = Observable.combineLatest(
            signpostViewModel.isVisible,
            incidentViewModel.isVisible,
            endOfRouteViewModel.isVisible,
            onlyVisibleViewModel.isVisible,
            Function4 { a: Boolean, b: Boolean, c: Boolean, d: Boolean ->
                Foo(signpostViewModel to a, incidentViewModel to b, endOfRouteViewModel to c, onlyVisibleViewModel to d)
            })
            .map<List<TopViewViewModel>> {
                val list = mutableListOf<TopViewViewModel>()
                if (it.a.second) list.add(it.a.first)
                if (it.b.second) list.add(it.b.first)
                if (it.c.second) list.add(it.c.first)
                if (it.d.second) list.add(it.d.first)

                list
            }
            .map { viewModelRulebase.applyRules(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.submitList(it)
                Log.d("matej", "${adapter.itemCount}")
            }, {
                Log.e("matej", "onCreate: ", it)
            })

        adapter.submitList(listOf(signpostViewModel, incidentViewModel, endOfRouteViewModel, onlyVisibleViewModel))
    }
}

data class Foo(
    val a: Pair<TopViewViewModel, Boolean>,
    val b: Pair<TopViewViewModel, Boolean>,
    val c: Pair<TopViewViewModel, Boolean>,
    val d: Pair<TopViewViewModel, Boolean>
)
