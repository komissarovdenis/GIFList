package com.giflist.view

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giflist.Contract
import com.giflist.R
import com.giflist.adapter.Adapter
import com.giflist.model.GIFData
import com.giflist.presenter.TrendingPresenter
import com.giflist.url.GiphyUrlRequestProvider
import com.giflist.util.ErrorUtil

class FragmentTrending : Fragment(), SwipeRefreshLayout.OnRefreshListener, Contract.TrendingView {

    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recycler: RecyclerView

    private val pageLoadingRunnable = Runnable {
        presenter.let { adapter.setLoadingMode(it.isLoading()) }
    }

    private val itemOnClickListener = View.OnClickListener { view ->
        (view?.tag as? GIFData)?.let {
            presenter.onItemClicked(view.context, it)
        }
    }

    private val handler = Handler()
    private val adapter = Adapter(itemOnClickListener)
    private val presenter = TrendingPresenter(GiphyUrlRequestProvider())
    private var lastCheckTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        presenter.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false).also { view ->
            swipeToRefresh = view.findViewById(R.id.swipe_to_refresh_view)
            swipeToRefresh.setOnRefreshListener(this)

            layoutManager = LinearLayoutManager(inflater.context)
            recycler = view.findViewById(R.id.recycler_view)
            recycler.addOnScrollListener(ScrollListener())
            recycler.layoutManager = layoutManager
            recycler.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
        handler.removeCallbacks(pageLoadingRunnable)
    }

    override fun onRefresh() {
        presenter.reload()
    }

    override fun getAdapter(): Adapter = adapter

    override fun updateGlobalLoader(loading: Boolean) {
        swipeToRefresh.isRefreshing = loading
    }

    override fun updatePageLoader(loading: Boolean) {
        handler.removeCallbacks(pageLoadingRunnable)
        if (loading) {
            recycler.post { adapter.setLoadingMode(loading) }
        } else {
            handler.postDelayed(pageLoadingRunnable, LOADING_HIDE_DELAY)
        }
    }

    override fun onError(throwable: Throwable) {
        context?.let { context ->
            ErrorUtil.showErrorToast(context, throwable)
        }
    }

    private inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 && !presenter.isLoading() && presenter.canLoadMore()) {
                layoutManager.let {
                    val total = recycler.adapter?.itemCount ?: return
                    val last = it.findLastVisibleItemPosition()
                    val time = SystemClock.elapsedRealtime()
                    if (total - last <= ITEMS_LEFT_COUNT && time - lastCheckTime >= DEBOUNCE_TIME) {
                        lastCheckTime = time
                        presenter.loadNext()
                    }
                }
            }
        }
    }

    companion object {
        const val ITEMS_LEFT_COUNT = 5
        const val DEBOUNCE_TIME = 2000
        const val LOADING_HIDE_DELAY = 100L
    }
}