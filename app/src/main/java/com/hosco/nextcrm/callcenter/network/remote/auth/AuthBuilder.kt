package com.hosco.nextcrm.callcenter.network.remote.auth

import android.content.Context
import com.hosco.nextcrm.callcenter.BuildConfig
import com.hosco.nextcrm.callcenter.api.ConfigAPI
import com.hosco.nextcrm.callcenter.api.Properties.TIME_OUT
import com.hosco.nextcrm.callcenter.common.Const.Companion.DEBUG
import com.hosco.nextcrm.callcenter.common.Const.Companion.REQUEST_TIMEOUT_DURATION
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AuthBuilder() {

    companion object {
        private val authApi: AuthApi? = null
        fun getAuthService(): AuthApi {
            if (authApi != null) {
                return authApi
            }
            val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createRequestInterceptorClient())
                .build()
            return retrofit.create(AuthApi::class.java)
        }
    }

}

private fun createRequestInterceptorClient(): OkHttpClient {
    val interceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")
            .method(original.method, original.body)
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    return if (DEBUG) {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
            .build()
    } else {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
            .build()
    }
}


object Properties {
    const val TIME_OUT = 10L
}

fun createOkHttpCache(context: Context): Cache {
    val size = (10 * 1024 * 1024).toLong()
    return Cache(context.cacheDir, size)
}

fun createLoggingInterceptor(): Interceptor {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
    else HttpLoggingInterceptor.Level.NONE
    return logging
}

fun createHeaderInterceptor(): Interceptor {
    return Interceptor { chain ->
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .build()
        val newRequest = request.newBuilder()
            .url(newUrl)
            .header("Content-Type", "application/json")
            .method(request.method, request.body)
            .build()
        chain.proceed(newRequest)
    }
}

fun createOkHttpClient(
    cache: Cache,
    header: Interceptor,
    logging: Interceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(cache)
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(header)
        .addInterceptor(logging)
        .build()
}

