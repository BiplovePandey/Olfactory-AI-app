package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OlfactoryDao {

    @Query("SELECT * FROM fragrances ORDER BY name ASC")
    fun getAllFragrancesFlow(): Flow<List<FragranceEntity>>

    @Query("SELECT * FROM fragrances ORDER BY name ASC")
    suspend fun getAllFragrances(): List<FragranceEntity>

    @Query("SELECT * FROM fragrances WHERE id = :id LIMIT 1")
    suspend fun getFragranceById(id: Int): FragranceEntity?

    @Query("SELECT * FROM fragrances WHERE is_in_wardrobe = 1 ORDER BY name ASC")
    fun getWardrobeFlow(): Flow<List<FragranceEntity>>

    @Query("SELECT * FROM fragrances WHERE is_in_wardrobe = 1")
    suspend fun getWardrobe(): List<FragranceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFragrances(fragrances: List<FragranceEntity>)

    @Query("UPDATE fragrances SET is_in_wardrobe = :isInWardrobe WHERE id = :id")
    suspend fun updateWardrobeStatus(id: Int, isInWardrobe: Boolean)

    @Query("SELECT * FROM saved_combinations ORDER BY created_at DESC")
    fun getSavedCombinationsFlow(): Flow<List<SavedCombinationEntity>>

    @Query("SELECT * FROM saved_combinations ORDER BY created_at DESC")
    suspend fun getSavedCombinations(): List<SavedCombinationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCombination(combination: SavedCombinationEntity): Long

    @Query("DELETE FROM saved_combinations WHERE id = :id")
    suspend fun deleteSavedCombination(id: Int)

    @Query("DELETE FROM saved_combinations WHERE fragrance_a_id = :aId AND fragrance_b_id = :bId")
    suspend fun deleteCombinationByFragranceIds(aId: Int, bId: Int)

    @Query("SELECT COUNT(*) FROM fragrances")
    suspend fun getFragranceCount(): Int
}
