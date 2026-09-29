package com.example.data.repository

import android.content.Context
import com.example.data.local.AssetDataLoader
import com.example.data.local.FragranceEntity
import com.example.data.local.OlfactoryDatabase
import com.example.data.local.SavedCombinationEntity
import com.example.data.model.Brand
import com.example.data.model.Fragrance
import com.example.data.model.LayeringResult
import com.example.data.model.SavedCombination
import com.example.data.model.UserPreferences
import com.example.data.remote.ApiClient
import com.example.data.remote.CollectionItemRequest
import com.example.data.remote.SaveCombinationRequest
import com.example.olfactory.ml.OlfactoryEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FragranceRepository(private val context: Context) {

    private val db = OlfactoryDatabase.getDatabase(context)
    private val dao = db.olfactoryDao()

    // Flow of all fragrances from local Room database
    val allFragrancesFlow: Flow<List<Fragrance>> = dao.getAllFragrancesFlow().map { list ->
        list.map { it.toDomain() }
    }

    // Flow of user's perfume wardrobe
    val wardrobeFlow: Flow<List<Fragrance>> = dao.getWardrobeFlow().map { list ->
        list.map { it.toDomain() }
    }

    // Flow of saved layering combinations
    val savedCombinationsFlow: Flow<List<SavedCombination>> = dao.getSavedCombinationsFlow().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val count = dao.getFragranceCount()
        if (count == 0) {
            val initial = AssetDataLoader.loadInitialFragrances(context)
            if (initial.isNotEmpty()) {
                // Pre-seed a few iconic wardrobe fragrances for instant exploration:
                // Raw by SKINN (Titan), Bombay Perfumery Chai Musk, Forest Essentials Gulab Jal / Attar, Mitti Attar
                val defaultWardrobeIds = setOf(1, 2, 5, 8, 12, 19, 23)
                val entities = initial.map { f ->
                    FragranceEntity.fromDomain(f, inWardrobe = defaultWardrobeIds.contains(f.id))
                }
                dao.insertFragrances(entities)

                // Pre-seed an initial saved combination demonstrating the signature East-meets-West harmony
                val fragA = initial.firstOrNull { it.id == 1 } ?: initial[0]
                val fragB = initial.firstOrNull { it.id == 2 } ?: initial[1]
                val layering = OlfactoryEngine.scoreLayeringPair(fragA, fragB)
                if (layering != null) {
                    dao.insertSavedCombination(
                        SavedCombinationEntity(
                            fragrance_a_id = fragA.id,
                            fragrance_b_id = fragB.id,
                            fragrance_a_name = fragA.name,
                            fragrance_b_name = fragB.name,
                            fragrance_a_brand = fragA.brand_name,
                            fragrance_b_brand = fragB.brand_name,
                            compatibility_score = layering.compatibility_score,
                            chord_title = layering.chord_title,
                            explanation = layering.explanation,
                            layering_method = layering.layering_method,
                            created_at = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    suspend fun getAllFragrances(): List<Fragrance> = withContext(Dispatchers.IO) {
        dao.getAllFragrances().map { it.toDomain() }
    }

    suspend fun getFragranceById(id: Int): Fragrance? = withContext(Dispatchers.IO) {
        dao.getFragranceById(id)?.toDomain()
    }

    suspend fun loadBrands(): List<Brand> = withContext(Dispatchers.IO) {
        AssetDataLoader.loadBrands(context)
    }

    suspend fun toggleWardrobe(fragranceId: Int, inWardrobe: Boolean) = withContext(Dispatchers.IO) {
        dao.updateWardrobeStatus(fragranceId, inWardrobe)

        // Attempt non-blocking remote sync if backend is active
        try {
            val api = ApiClient.getService()
            if (inWardrobe) {
                api.addToCollection(CollectionItemRequest(fragrance_id = fragranceId))
            } else {
                api.removeFromCollection(fragranceId)
            }
        } catch (_: Exception) {
            // Offline-first: local database retains state
        }
    }

    suspend fun saveCombination(result: LayeringResult) = withContext(Dispatchers.IO) {
        val entity = SavedCombinationEntity(
            fragrance_a_id = result.fragrance_a.id,
            fragrance_b_id = result.fragrance_b.id,
            fragrance_a_name = result.fragrance_a.name,
            fragrance_b_name = result.fragrance_b.name,
            fragrance_a_brand = result.fragrance_a.brand_name,
            fragrance_b_brand = result.fragrance_b.brand_name,
            compatibility_score = result.compatibility_score,
            chord_title = result.chord_title,
            explanation = result.explanation,
            layering_method = result.layering_method,
            created_at = System.currentTimeMillis()
        )
        dao.insertSavedCombination(entity)

        // Attempt non-blocking remote sync
        try {
            val api = ApiClient.getService()
            api.saveCombination(
                SaveCombinationRequest(
                    fragrance1_id = result.fragrance_a.id,
                    fragrance2_id = result.fragrance_b.id,
                    score = result.compatibility_score,
                    notes = result.explanation
                )
            )
        } catch (_: Exception) {
            // Local state preserved
        }
    }

    suspend fun deleteSavedCombination(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteSavedCombination(id)
        try {
            val api = ApiClient.getService()
            api.deleteSavedCombination(id)
        } catch (_: Exception) {
            // Local state preserved
        }
    }

    fun calculateLayering(
        fragA: Fragrance,
        fragB: Fragrance,
        preferences: UserPreferences
    ): LayeringResult? {
        return OlfactoryEngine.scoreLayeringPair(fragA, fragB, preferences)
    }

    suspend fun syncWithBackend(): Boolean = withContext(Dispatchers.IO) {
        try {
            val api = ApiClient.getService()
            val health = api.checkHealth()
            if (health.isSuccessful) {
                val remoteFragrances = api.getFragrances()
                if (remoteFragrances.isSuccessful && !remoteFragrances.body().isNullOrEmpty()) {
                    val entities = remoteFragrances.body()!!.map { f ->
                        FragranceEntity.fromDomain(f)
                    }
                    dao.insertFragrances(entities)
                }
                return@withContext true
            }
        } catch (_: Exception) {
            // Backend offline or unreachable, local database remains authoritative
        }
        return@withContext false
    }
}
