package com.giflist.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.giflist.holder.GIFItemViewHolder
import com.giflist.holder.GIFSquaredItemViewHolder
import com.giflist.holder.ProgressItemViewHolder
import com.giflist.model.GIFData

class Adapter(private val clickListener: View.OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataSet = ArrayList<GIFData>()
    private var loadingMode = false

    fun setLoadingMode(loading: Boolean) {
        if (loadingMode != loading) {
            loadingMode = loading
            notifyItemChanged(itemCount)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (loadingMode && position == itemCount - 1) VIEW_TYPE_PROGRESS
        else if (isSquared(dataSet[position])) VIEW_TYPE_SQUARED
        else VIEW_TYPE_DEFAULT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> GIFItemViewHolder(parent, clickListener)
            VIEW_TYPE_SQUARED -> GIFSquaredItemViewHolder(parent, clickListener)
            else -> ProgressItemViewHolder(parent)
        }
    }

    override fun getItemCount(): Int = dataSet.size + (if (loadingMode) 1 else 0)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_DEFAULT,
            VIEW_TYPE_SQUARED -> (holder as GIFItemViewHolder).bind(dataSet[position])
            else -> Unit
        }
    }

    companion object {
        const val VIEW_TYPE_PROGRESS = 0
        const val VIEW_TYPE_DEFAULT = 1
        const val VIEW_TYPE_SQUARED = 2

        private fun isSquared(data: GIFData): Boolean {
            return data.images.preview.height >= data.images.preview.width
        }
    }
}
