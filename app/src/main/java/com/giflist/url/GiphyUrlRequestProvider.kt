package com.giflist.url

import okhttp3.HttpUrl

class GiphyUrlRequestProvider : UrlRequestProvider {

    override fun createTrendingRequestUrl(offset: Int, pageSize: Int): HttpUrl = HttpUrl.Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegments(TRENDING_PATH)
        .addQueryParameter(OFFSET, offset.toString())
        .addQueryParameter(LIMIT, pageSize.toString())
        .addQueryParameter(API_KEY, DEVELOPER_KEY)
        .build()

    override fun createRandomRequestUrl(): HttpUrl = HttpUrl.Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegments(RANDOM_PATH)
        .addQueryParameter(API_KEY, DEVELOPER_KEY)
        .build()

    companion object {
        const val SCHEME = "https"
        const val HOST = "api.giphy.com"
        const val TRENDING_PATH = "v1/gifs/trending"
        const val RANDOM_PATH = "v1/gifs/random"
        const val OFFSET = "offset"
        const val API_KEY = "api_key"
        const val LIMIT = "limit"

        const val DEVELOPER_KEY = "" // put your API key here
    }
}