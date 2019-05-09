package com.giflist.model

import com.google.gson.annotations.SerializedName

data class ImageData(@SerializedName("url") val url: String,
                     @SerializedName("width") val width: Int,
                     @SerializedName("height") val height: Int)