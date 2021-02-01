package ru.andreyhoco.androidacademyproject.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.create
import ru.andreyhoco.androidacademyproject.BuildConfig

object RetrofitModule {
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val contentType = "application/json".toMediaType()
    private val client = OkHttpClient().newBuilder()
        .addNetworkInterceptor(TmdbApiQueryInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.TMDB_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
    val tmdbApiService: TmdbApiService = retrofit.create()

    private class TmdbApiQueryInterceptor : Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val originalHttpUrl = originalRequest.url

            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()

            val request = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(request)
        }
    }
}