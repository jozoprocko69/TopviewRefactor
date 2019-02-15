package com.example.mbrecka.topviewrefactor

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

@Suppress("unused")
class AnchoredToTopViewBehavior(context: Context, attributeSet: AttributeSet) :
    androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<View>(context, attributeSet) {

    override fun layoutDependsOn(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (dependency.id == R.id.recycler) return true
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        if (dependency.id == R.id.recycler) {

            val recycler = (dependency as RecyclerView)

            // TODO: extension function
            (0 until recycler.childCount)
                .maxBy {
                    recycler.getChildAt(it).let { it.y + it.height }
                }
                .let {
                    child.translationY = if (it != null) recycler.getChildAt(it).let { it.y + it.height } else 0f
                }
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }
}