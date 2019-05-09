package com.giflist.presenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.util.Log
import com.giflist.Contract
import com.giflist.MainActivity
import com.giflist.adapter.DataDiffUtilCallback
import com.giflist.model.GIFData
import com.giflist.url.GiphyUrlRequestProvider.Companion.OFFSET
import com.giflist.url.UrlRequestProvider
import com.giflist.view.FragmentViewer
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class TrendingPresenter(private val urlRequestProvider: UrlRequestProvider) : Contract.TrendingPresenter {

    private val httpClient = OkHttpClient()
    private var disposable: Disposable? = null
    private var view: Contract.TrendingView? = null
    private val content = ArrayList<GIFData>()
    private var loading = false
    private var total = 0
    private var offset = 0

    override fun onViewCreated(view: Contract.TrendingView) {
        this.view = view
        view.updateGlobalLoader(loading)
        view.getAdapter().apply {
            dataSet.clear()
            dataSet.addAll(content)
            notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        view = null
    }

    override fun onCreate() {
        reload()
    }

    override fun onDestroy() {
        content.clear()
        disposable?.dispose()
    }

    override fun reload() {
        offset = 0
        total = 0
        load(urlRequestProvider.createTrendingRequestUrl(0, PAGE_SIZE), reload = true)
    }

    override fun loadNext() {
        load(urlRequestProvider.createTrendingRequestUrl(offset + PAGE_SIZE, PAGE_SIZE))
    }

    override fun canLoadMore(): Boolean = offset < total || total == 0

    override fun isLoading(): Boolean = loading

    override fun onItemClicked(context: Context, item: GIFData) {
        val bundle = Bundle()
        bundle.putString(FragmentViewer.TITLE_KEY, item.title)
        bundle.putString(FragmentViewer.URL_KEY, item.images.original.url)
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(FragmentViewer.DATA_KEY, bundle)
        context.startActivity(intent)
    }

    private fun load(url: HttpUrl, reload: Boolean = false) {
        loading = true
        updateLoader(reload)
        disposable = Observable.defer<Response> {
            try {
                val request = Request.Builder().url(url).get().build()
                Observable.just(httpClient.newCall(request).execute())
            } catch (e: Throwable) {
                Observable.error(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .map { response ->
                if (reload) {
                    content.clear()
                }
                val data = response.body()?.string()
                val root = JSONObject(data)
                val pagination = root.getJSONObject(PAGINATION)
                offset = pagination.getInt(OFFSET)
                total = pagination.getInt(TOTAL)
                content.addAll(
                    GsonBuilder()
                        .create()
                        .fromJson(root.getString(DATA), Array<GIFData>::class.java)
                        .toList()
                )
                content
            }
            .subscribeOn(Schedulers.computation())
            .map { data ->
                val adapter = view?.getAdapter()
                if (adapter == null) {
                    Pair(data, null)
                } else {
                    Pair(data, DiffUtil.calculateDiff(DataDiffUtilCallback(adapter.dataSet, data)))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { pair ->
                    view?.getAdapter()?.let { adapter ->
                        adapter.dataSet.clear()
                        adapter.dataSet.addAll(pair.first)
                        pair.second?.dispatchUpdatesTo(adapter)
                    }

                    loading = false
                    updateLoader(reload)
                }, { e ->
                    Log.e(TAG, e.message)
                    view?.onError(e)
                    loading = false
                    updateLoader(reload)
                }
            )
    }

    private fun updateLoader(reload: Boolean) {
        view?.apply {
            if (reload) updateGlobalLoader(isLoading()) else updatePageLoader(isLoading())
        }
    }

    companion object {
        const val TAG = "TrendingPresenter"
        const val DATA = "data"
        const val PAGINATION = "pagination"
        const val TOTAL = "total_count"
        const val PAGE_SIZE = 30
    }
}
