package com.example.automacorp.services

import com.example.automacorp.models.RoomCommandDto
import com.example.automacorp.models.RoomDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

private const val API_USERNAME = "user"
private const val API_PASSWORD = "password"
private const val BASE_URL = "https://automacorp.devmind.cleverapps.io/api/"

object ApiServices {
    private val networkLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val roomsApiService: RoomsApiService by lazy {
        createRoomApiService()
    }

    private fun createRoomApiService(): RoomsApiService {
        val jsonConverter = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val httpClient = createHttpClient()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(jsonConverter))
            .client(httpClient)
            .build()
            .create(RoomsApiService::class.java)
    }

    private fun createHttpClient(): OkHttpClient {
        return try {
            val trustManager = createTrustManager()
            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, arrayOf(trustManager), null)
            }

            OkHttpClient.Builder()
                .addInterceptor(networkLoggingInterceptor)
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize secure client", e)
        }
    }

    private fun createTrustManager() = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            // Development environment - no certificate validation
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            // Development environment - no certificate validation
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
}

interface RoomsApiService {
    @GET("rooms")
    fun findAll(): Call<List<RoomDto>>

    @GET("rooms/{id}")
    fun findById(@Path("id") id: Long): Call<RoomDto>

    @PUT("rooms/{id}")
    fun updateRoom(@Path("id") id: Long, @Body room: RoomCommandDto): Call<RoomDto>
}

private class BasicAuthInterceptor(
    private val username: String,
    private val password: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authenticatedRequest = chain.request()
            .newBuilder()
            .header("Authorization", Credentials.basic(username, password))
            .build()
        return chain.proceed(authenticatedRequest)
    }
}