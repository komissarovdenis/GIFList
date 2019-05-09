package com.giflist.view

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.giflist.Contract
import com.giflist.presenter.RandomGIFPresenter
import com.giflist.url.GiphyUrlRequestProvider
import com.giflist.util.ErrorUtil


class FragmentViewer : Fragment(), Contract.RandomGIFView {

    private lateinit var animationView: SimpleDraweeView
    private var presenter: RandomGIFPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        arguments?.let { bundle ->
            presenter = RandomGIFPresenter(bundle.getString(URL_KEY, ""), GiphyUrlRequestProvider())
            (activity as? AppCompatActivity)?.supportActionBar?.title = bundle.getString(TITLE_KEY)
        }
        presenter?.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.giflist.R.layout.fragment_viewer, container, false).also { view ->
            animationView = view.findViewById(com.giflist.R.id.animation_view)
            animationView.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
            presenter?.onViewCreated(this)
        }
    }

    override fun showGIF(url: String) {
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build()
        val controller = Fresco.newDraweeControllerBuilder()
            .setOldController(animationView.controller)
            .setUri(imageRequest.sourceUri)
            .setAutoPlayAnimations(true)
            .build()

        animationView.controller = controller
    }

    override fun onError(throwable: Throwable) {
        context?.let { context ->
            ErrorUtil.showErrorToast(context, throwable)
        }
    }

    companion object {
        const val URL_KEY = "url_key"
        const val DATA_KEY = "data_key"
        const val TITLE_KEY = "title_key"
    }
}