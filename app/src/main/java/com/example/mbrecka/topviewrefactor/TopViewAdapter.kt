package com.example.mbrecka.topviewrefactor

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView


import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.mbrecka.topviewrefactor.databinding.*
import java.lang.RuntimeException

class TopViewAdapter : RecyclerView.Adapter<TopViewHolder>() {

    private val data = mutableListOf<TopViewViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            2 -> ItemSignpostBinding.inflate(inflater).let(::SignpostViewHolder)
            3 -> ItemIncidentBinding.inflate(inflater).let(::IncidentViewHolder)
            4 -> ItemEndofrouteBinding.inflate(inflater).let(::EndOfRouteViewHolder)
            5 -> ItemOnlyvisibleBinding.inflate(inflater).let(::OnlyVisibleViewHolder)
            else -> throw RuntimeException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is SignpostViewModel -> 2
            is IncidentViewModel -> 3
            is EndOfRouteViewModel -> 4
            is OnlyVisibleViewModel -> 5
            else -> throw RuntimeException()
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        val item = data[position]
        when (holder) {
            is SignpostViewHolder -> {
                (holder.binding as ItemSignpostBinding).viewModel = item as SignpostViewModel
            }
            is IncidentViewHolder -> {
                (holder.binding as ItemIncidentBinding).viewModel = item as IncidentViewModel
            }
            is EndOfRouteViewHolder -> {
                (holder.binding as ItemEndofrouteBinding).viewModel = item as EndOfRouteViewModel
            }
            is OnlyVisibleViewHolder -> {
                (holder.binding as ItemOnlyvisibleBinding).viewModel = item as OnlyVisibleViewModel
            }
        }
    }

    fun submitList(viewmodels: List<TopViewViewModel>) {
        Log.d("matej", "submitList() called with: viewmodels = [$viewmodels]")
        DiffUtil.calculateDiff(diffCallback(viewmodels))
            .dispatchUpdatesTo(this)
        data.clear()
        data.addAll(viewmodels)
    }

    private fun diffCallback(viewmodels: List<TopViewViewModel>): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition] == viewmodels[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition] == viewmodels[newItemPosition]
            }

            override fun getOldListSize() = data.size

            override fun getNewListSize() = viewmodels.size
        }
    }

    fun isDismissable(viewHolder: RecyclerView.ViewHolder) = data[viewHolder.adapterPosition].isDismissable

    fun foo(viewHolder: RecyclerView.ViewHolder) {
        data[viewHolder.adapterPosition].onDismiss()
    }
}

sealed class TopViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {init {
    Log.d("matej", "TopViewHolder.init")
}
}

class SignpostViewHolder(binding: ItemSignpostBinding) : TopViewHolder(binding) {init {
    Log.d("matej", "SignpostViewHolder.init")
}
}

class IncidentViewHolder(binding: ItemIncidentBinding) : TopViewHolder(binding) {init {
    Log.d("matej", "IncidentViewHolder.init")
}
}

class EndOfRouteViewHolder(binding: ItemEndofrouteBinding) : TopViewHolder(binding) {init {
    Log.d("matej", "EndOfRouteViewHolder.init")
}
}

class OnlyVisibleViewHolder(binding: ItemOnlyvisibleBinding) : TopViewHolder(binding) {init {
    Log.d("matej", "OnlyVisibleViewHolder.init")
}
}
