package com.example.mbrecka.topviewrefactor

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

@Suppress("unused")
class TopViewBehavior(context: Context, attributeSet: AttributeSet) :
    androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<View>(context, attributeSet) {

    override fun layoutDependsOn(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        Log.d(
            "matej",
            "layoutDependsOn() called with: parent = [$parent], child = [$child], dependency = [$dependency]"
        )
        if (dependency.id == R.id.recycler) return true
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onMeasureChild(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        Log.d(
            "matej",
            "onMeasureChild() called with: parent = [$parent], child = [$child], parentWidthMeasureSpec = [$parentWidthMeasureSpec], widthUsed = [$widthUsed], parentHeightMeasureSpec = [$parentHeightMeasureSpec], heightUsed = [$heightUsed]"
        )
        return super.onMeasureChild(
            parent,
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed
        )
    }

    override fun onDependentViewChanged(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        if (dependency.id == R.id.recycler) {

            val recycler = (dependency as RecyclerView)

            val inWindow = IntArray(2).apply {
                val lastChildPosition = recycler.childCount - 1

                Log.d("matej", "FOO $lastChildPosition")

                if (lastChildPosition >= 0) {
                    dependency.getChildAt(lastChildPosition).getLocationInWindow(this)
                }
            }

            val onScreen = IntArray(2).apply {
                val lastChildPosition = recycler.childCount - 1

                if (lastChildPosition >= 0) {
                    dependency.getChildAt(lastChildPosition).getLocationOnScreen(this)
                }
            }

            Log.d(
                "matej",
                "measured_height = [${dependency.measuredHeight}] height = [${dependency.height}] foo = [${dependency.measuredHeightAndState}] inWindow = [${inWindow[0]}, ${inWindow[1]}] onScreen = [${onScreen[0]}, ${onScreen[1]}]"
            )

            child.translationY = onScreen[1].toFloat()
        }
//        if (dependency?.id == R.id.signposts) {
//            translateVertically(child, dependency)
//            return true
//        }


//
        return super.onDependentViewChanged(parent, child, dependency)
    }

//    private fun translateVertically(view: View?, dependency: View) {
//        view?.translationY = max(dependency.height + dependency.translationY + (dependency.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin, 0f)
//    }

}