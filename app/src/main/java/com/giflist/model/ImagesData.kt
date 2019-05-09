package com.giflist.model

import com.google.gson.annotations.SerializedName

data class ImagesData(@SerializedName("480w_still") val preview: ImageData,
                      @SerializedName("fixed_width") val original: ImageData)