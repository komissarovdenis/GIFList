package com.giflist.holder

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ProgressBar
import com.giflist.R

class ProgressItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ProgressBar(parent.context)) {
    init {
        (itemView as ProgressBar).let {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent.context.resources.getDimension(R.dimen.progress_view_item_height).toInt())
            val padding = parent.context.resources.getDimension(R.dimen.progress_view_item_padding).toInt()
            it.setPadding(0, padding, 0, padding)
            it.isIndeterminate = true
        }
    }
}