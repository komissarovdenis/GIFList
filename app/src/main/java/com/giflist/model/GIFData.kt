package com.giflist.model

import com.google.gson.annotations.SerializedName

data class GIFData(@SerializedName("id") val id: String,
                   @SerializedName("title") val title: String,
                   @SerializedName("images") val images: ImagesData)