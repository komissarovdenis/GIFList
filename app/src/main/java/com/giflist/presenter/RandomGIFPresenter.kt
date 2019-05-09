package com.giflist.presenter

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import com.giflist.Contract
import com.giflist.model.GIFData
import com.giflist.url.UrlRequestProvider
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class RandomGIFPresenter(baseUrl: String,
                         private val urlRequestProvider: UrlRequestProvider) : Contract.RandomGIFPresenter {

    private val handlerThread = HandlerThread("Random GIF Timer", HandlerThread.MIN_PRIORITY)
    private val gifSubscriber = PrefetchSubscriber()
    private val timer = Runnable { loadRandom() }
    private val httpClient = OkHttpClient()
    private var disposable: Disposable? = null
    private var view: Contract.RandomGIFView? = null
    private var currentUrl: String = baseUrl
    private val handler: Handler

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    override fun onCreate() {
        loadRandomDelayed()
    }

    override fun onViewCreated(view: Contract.RandomGIFView) {
        this.view = view
        loadAnimation(currentUrl)
    }

    override fun onDestroyView() {
        view = null
    }

    override fun onDestroy() {
        handler.removeCallbacks(timer)
        disposable?.dispose()
    }

    private fun loadRandomDelayed() {
        handler.postDelayed(timer, GIF_LOADING_INTERVAL_MS)
    }

    private fun loadRandom() {
        disposable = Observable.defer<Response> {
            try {
                val url = urlRequestProvider.createRandomRequestUrl()
                val request = Request.Builder().url(url).get().build()
                Observable.just(httpClient.newCall(request).execute())
            } catch (e: Throwable) {
                Observable.error(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .map { response ->
                val data = response.body()?.string()
                val root = JSONObject(data)
                GsonBuilder()
                    .create()
                    .fromJson(root.getString(TrendingPresenter.DATA), GIFData::class.java)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { gifData ->
                    currentUrl = gifData.images.original.url
                    loadAnimation(currentUrl)
                    loadRandomDelayed()
                }, { e ->
                    Log.e(TAG, e.message)
                    view?.onError(e)
                    loadRandomDelayed()
                }
            )
    }

    private fun loadAnimation(url: String) {
        val dataSource = Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(url), null)
        dataSource.subscribe(gifSubscriber, UiThreadImmediateExecutorService.getInstance())
    }

    inner class PrefetchSubscriber : BaseDataSubscriber<Void>() {
        override fun onNewResultImpl(dataSource: DataSource<Void>?) {
            view?.showGIF(currentUrl)
        }

        override fun onFailureImpl(dataSource: DataSource<Void>?) {
            dataSource?.failureCause?.let {
                view?.onError(it)
            }
        }
    }

    companion object {
        const val TAG = "RandomGIFPresenter"
        const val GIF_LOADING_INTERVAL_MS = 10_000L
    }
}