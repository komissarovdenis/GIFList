package com.giflist.adapter

import android.support.v7.util.DiffUtil
import com.giflist.model.GIFData

class DataDiffUtilCallback(private val old: List<GIFData>,
                           private val new: List<GIFData>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id &&
                old[oldItemPosition].title == new[newItemPosition].title &&
                old[oldItemPosition].images.original == new[newItemPosition].images.original &&
                old[oldItemPosition].images.preview == new[newItemPosition].images.preview
    }
}