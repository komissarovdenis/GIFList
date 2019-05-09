package com.giflist.holder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.giflist.R
import com.giflist.model.GIFData

open class GIFItemViewHolder(parent: ViewGroup,
                             clickListener: View.OnClickListener) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.gif_item_view_holder, parent, false)
) {

    private val title = itemView.findViewById<TextView>(R.id.title)
    private val preview = itemView.findViewById<SimpleDraweeView>(R.id.preview)

    init {
        itemView.setOnClickListener(clickListener)
    }

    fun bind(item: GIFData) {
        itemView.tag = item
        title.text = item.title
        preview.setImageURI(item.images.preview.url)
    }
}