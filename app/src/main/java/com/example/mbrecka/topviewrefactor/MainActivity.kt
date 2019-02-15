package com.example.mbrecka.topviewrefactor

import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import java.lang.RuntimeException
import kotlin.random.Random

// TODO: item nech sa vysunie popod predosly item, nie ponad

class ViewModelRulebase {

    private val hiddenBy = mapOf(
        TopVm.SIGNPOST to listOf(TopVm.ONLY_VISIBLE),
        TopVm.INCIDENT to listOf(TopVm.ONLY_VISIBLE, TopVm.END_OF_ROUTE),
        TopVm.END_OF_ROUTE to listOf(TopVm.ONLY_VISIBLE),
        TopVm.ONLY_VISIBLE to emptyList()
    )

    private fun isAllowedByState(viewModel: TopViewViewModel, vms: Map<TopVm, Boolean>): Boolean {
        val hiddenBy = hiddenBy[viewModel.toEnum()]!!
        val currentState = vms.keys

        return hiddenBy.intersect(currentState).isEmpty()
    }

    fun applyRules(it: Map<TopViewViewModel, Boolean>): List<TopViewViewModel> {

        val vms = it.toList()

        val mappedVms = vms
            .asSequence()
            .filter { it.second }
            .associate {
                it.first.toEnum() to it.second
            }

        return vms
            .asSequence()
            .filter { it.second && isAllowedByState(it.first, mappedVms) }
            .map { it.first }
            .toList()
    }

    private fun TopViewViewModel.toEnum(): TopVm {
        return when (this) {
            is SignpostViewModel -> TopVm.SIGNPOST
            is IncidentViewModel -> TopVm.INCIDENT
            is EndOfRouteViewModel -> TopVm.END_OF_ROUTE
            is OnlyVisibleViewModel -> TopVm.ONLY_VISIBLE
            else -> throw RuntimeException()
        }
    }

    enum class TopVm {
        SIGNPOST, INCIDENT, END_OF_ROUTE, ONLY_VISIBLE
    }
}

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModelRulebase = ViewModelRulebase()

    val adapter = TopViewAdapter()

    lateinit var binding: ActivityMainBinding

    private val disposable = CompositeDisposable()

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
            recycler.itemAnimator = SlideInDownAnimator(AccelerateDecelerateInterpolator())
            recycler.setChildDrawingOrderCallback { childCount, i -> childCount - (i + 1) } // reverse drawing order
            recycler.layoutManager = object : LinearLayoutManager(this@MainActivity) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    return if(adapter.isDismissable(viewHolder)){
                        ItemTouchHelper.LEFT
                    }else{
                        0
                    }
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.foo(viewHolder)
                }
            }).attachToRecyclerView(recycler)

            signpostSwitch.setOnCheckedChangeListener { _, isChecked -> signpostViewModel.setVisible(isChecked) }
            disposable += signpostViewModel.isVisible.subscribe { signpostSwitch.isChecked = it }

            incidentSwitch.setOnCheckedChangeListener { _, isChecked -> incidentViewModel.setVisible(isChecked) }
            disposable += incidentViewModel.isVisible.subscribe { incidentSwitch.isChecked = it }

            endOfRouteSwitch.setOnCheckedChangeListener { _, isChecked -> endOfRouteViewModel.setVisible(isChecked) }
            disposable += endOfRouteViewModel.isVisible.subscribe { endOfRouteSwitch.isChecked = it }

            onlyVisibleSwitch.setOnCheckedChangeListener { _, isChecked -> onlyVisibleViewModel.setVisible(isChecked) }
            disposable += onlyVisibleViewModel.isVisible.subscribe { onlyVisibleSwitch.isChecked = it }

            fab.setOnClickListener {
                it.alpha = Random.nextFloat()
            }
        }

        val visibilityStreams = listOf(
            signpostViewModel,
            incidentViewModel,
            endOfRouteViewModel,
            onlyVisibleViewModel
        )

        disposable +=
                Observable.combineLatest(visibilityStreams.map { it.isVisible.startWith(false) }) {
                    linkedMapOf<TopViewViewModel, Boolean>()
                        .apply {
                            it.forEachIndexed { index, b ->
                                this[visibilityStreams[index]] = b as Boolean
                            }
                        }
                }
                    .map { viewModelRulebase.applyRules(it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        adapter.submitList(it)
                        Log.d("matej", "${adapter.itemCount}")
                    }, {
                        Log.e("matej", "onCreate: ", it)
                    })
    }
}

data class Foo(
    val a: Pair<TopViewViewModel, Boolean>,
    val b: Pair<TopViewViewModel, Boolean>,
    val c: Pair<TopViewViewModel, Boolean>,
    val d: Pair<TopViewViewModel, Boolean>
)
