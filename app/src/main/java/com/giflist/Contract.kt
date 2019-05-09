package com.giflist

import android.content.Context
import com.giflist.adapter.Adapter
import com.giflist.model.GIFData

interface Contract {

    interface TrendingPresenter {
        fun reload()
        fun loadNext()
        fun canLoadMore(): Boolean
        fun isLoading(): Boolean

        fun onCreate()
        fun onDestroy()
        fun onViewCreated(view: TrendingView)
        fun onDestroyView()

        fun onItemClicked(context: Context, item: GIFData)
    }

    interface TrendingView {
        fun getAdapter(): Adapter
        fun updateGlobalLoader(loading: Boolean)
        fun updatePageLoader(loading: Boolean)
        fun onError(throwable: Throwable)
    }

    interface RandomGIFPresenter {
        fun onCreate()
        fun onDestroy()
        fun onViewCreated(view: RandomGIFView)
        fun onDestroyView()
    }

    interface RandomGIFView {
        fun showGIF(url: String)
        fun onError(throwable: Throwable)
    }
}