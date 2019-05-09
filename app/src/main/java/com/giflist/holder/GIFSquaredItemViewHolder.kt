package com.giflist.holder

import android.view.View
import android.view.ViewGroup
import com.giflist.R

class GIFSquaredItemViewHolder(parent: ViewGroup,
                               clickListener: View.OnClickListener) : GIFItemViewHolder(parent, clickListener) {

    init {
        val params = itemView.layoutParams
        params.height = itemView.context.resources.getDimension(R.dimen.gif_view_item_height_squared).toInt()
        itemView.layoutParams = params
    }
}