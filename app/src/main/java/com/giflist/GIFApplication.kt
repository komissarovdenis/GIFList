package com.giflist

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class GIFApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}