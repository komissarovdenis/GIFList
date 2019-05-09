package com.giflist.url

import okhttp3.HttpUrl

interface UrlRequestProvider {
    fun createTrendingRequestUrl(offset: Int, pageSize: Int): HttpUrl
    fun createRandomRequestUrl(): HttpUrl
}