package com.example.data.remote

import com.example.data.model.Fragrance
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class HealthResponse(
    val status: String? = null,
    val timestamp: String? = null
)

data class LayerRequest(
    val fragrance1_id: Int,
    val fragrance2_id: Int,
    val user_id: Int = 1
)

data class LayerApiResponse(
    val success: Boolean = true,
    val score: Int = 0,
    val chord: String? = null,
    val explanation: String? = null,
    val method: String? = null,
    val cross_origin: Boolean = false
)

data class SaveCombinationRequest(
    val fragrance1_id: Int,
    val fragrance2_id: Int,
    val score: Int,
    val notes: String = ""
)

data class CollectionItemRequest(
    val fragrance_id: Int,
    val ownership_status: String = "owned"
)

interface OlfactoryApiService {

    @GET("api/health")
    suspend fun checkHealth(): Response<HealthResponse>

    @GET("api/fragrances")
    suspend fun getFragrances(
        @Query("search") search: String? = null,
        @Query("family") family: String? = null
    ): Response<List<Fragrance>>

    @GET("api/fragrances/{id}")
    suspend fun getFragranceById(@Path("id") id: Int): Response<Fragrance>

    @POST("api/layer")
    suspend fun calculateLayering(@Body request: LayerRequest): Response<LayerApiResponse>

    @GET("api/collection")
    suspend fun getCollection(): Response<List<Map<String, Any>>>

    @POST("api/collection")
    suspend fun addToCollection(@Body request: CollectionItemRequest): Response<Map<String, Any>>

    @DELETE("api/collection/{id}")
    suspend fun removeFromCollection(@Path("id") fragranceId: Int): Response<Map<String, Any>>

    @GET("api/saved-combinations")
    suspend fun getSavedCombinations(): Response<List<Map<String, Any>>>

    @POST("api/saved-combinations")
    suspend fun saveCombination(@Body request: SaveCombinationRequest): Response<Map<String, Any>>

    @DELETE("api/saved-combinations/{id}")
    suspend fun deleteSavedCombination(@Path("id") id: Int): Response<Map<String, Any>>
}

object ApiClient {
    // Default to standard Android emulator loopback for localhost:3000
    // Users can also update the backend endpoint dynamically in Settings / Profile
    const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/"

    private var currentBaseUrl = DEFAULT_BASE_URL
    private var cachedService: OlfactoryApiService? = null

    fun getService(baseUrl: String = currentBaseUrl): OlfactoryApiService {
        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (cachedService != null && currentBaseUrl == normalized) {
            return cachedService!!
        }

        currentBaseUrl = normalized
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(normalized)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val service = retrofit.create(OlfactoryApiService::class.java)
        cachedService = service
        return service
    }

    fun updateBaseUrl(newUrl: String) {
        currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        cachedService = null
    }

    fun getCurrentBaseUrl(): String = currentBaseUrl
}
