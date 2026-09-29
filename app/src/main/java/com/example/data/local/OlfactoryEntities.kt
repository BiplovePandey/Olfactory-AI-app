package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Fragrance
import com.example.data.model.SavedCombination

@Entity(tableName = "fragrances")
data class FragranceEntity(
    @PrimaryKey val id: Int,
    val brand_id: Int?,
    val brand_name: String,
    val name: String,
    val format: String,
    val fragrance_type: String?,
    val concentration: String?,
    val gender: String,
    val category: String?,
    val description: String?,
    val origin_style: String,
    val price_inr: Double?,
    val volume_ml: Double?,
    val is_oil_based: Boolean,
    val fragrance_family: String,
    val top_notes: String,
    val middle_notes: String,
    val base_notes: String,
    val season: String,
    val occasion: String,
    val intensity: Int,
    val sweetness: Int,
    val freshness: Int,
    val longevity: String,
    val brand_country: String?,
    val is_in_wardrobe: Boolean = false
) {
    fun toDomain(): Fragrance {
        return Fragrance(
            id = id,
            brand_id = brand_id,
            brand_name = brand_name,
            name = name,
            format = format,
            fragrance_type = fragrance_type,
            concentration = concentration,
            gender = gender,
            category = category,
            description = description,
            origin_style = origin_style,
            price_inr = price_inr,
            volume_ml = volume_ml,
            is_oil_based = is_oil_based,
            fragrance_family = fragrance_family,
            top_notes = if (top_notes.isBlank()) emptyList() else top_notes.split("|"),
            middle_notes = if (middle_notes.isBlank()) emptyList() else middle_notes.split("|"),
            base_notes = if (base_notes.isBlank()) emptyList() else base_notes.split("|"),
            season = if (season.isBlank()) emptyList() else season.split("|"),
            occasion = if (occasion.isBlank()) emptyList() else occasion.split("|"),
            intensity = intensity,
            sweetness = sweetness,
            freshness = freshness,
            longevity = longevity,
            brand_country = brand_country
        )
    }

    companion object {
        fun fromDomain(f: Fragrance, inWardrobe: Boolean = false): FragranceEntity {
            return FragranceEntity(
                id = f.id,
                brand_id = f.brand_id,
                brand_name = f.brand_name,
                name = f.name,
                format = f.format,
                fragrance_type = f.fragrance_type,
                concentration = f.concentration,
                gender = f.gender,
                category = f.category,
                description = f.description,
                origin_style = f.origin_style,
                price_inr = f.price_inr,
                volume_ml = f.volume_ml,
                is_oil_based = f.is_oil_based,
                fragrance_family = f.fragrance_family,
                top_notes = f.top_notes.joinToString("|"),
                middle_notes = f.middle_notes.joinToString("|"),
                base_notes = f.base_notes.joinToString("|"),
                season = f.season.joinToString("|"),
                occasion = f.occasion.joinToString("|"),
                intensity = f.intensity,
                sweetness = f.sweetness,
                freshness = f.freshness,
                longevity = f.longevity,
                brand_country = f.brand_country,
                is_in_wardrobe = inWardrobe
            )
        }
    }
}

@Entity(tableName = "saved_combinations")
data class SavedCombinationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fragrance_a_id: Int,
    val fragrance_b_id: Int,
    val fragrance_a_name: String,
    val fragrance_b_name: String,
    val fragrance_a_brand: String,
    val fragrance_b_brand: String,
    val compatibility_score: Int,
    val chord_title: String,
    val explanation: String,
    val layering_method: String,
    val created_at: Long = System.currentTimeMillis()
) {
    fun toDomain(): SavedCombination {
        return SavedCombination(
            id = id,
            fragrance_a_id = fragrance_a_id,
            fragrance_b_id = fragrance_b_id,
            fragrance_a_name = fragrance_a_name,
            fragrance_b_name = fragrance_b_name,
            fragrance_a_brand = fragrance_a_brand,
            fragrance_b_brand = fragrance_b_brand,
            compatibility_score = compatibility_score,
            chord_title = chord_title,
            explanation = explanation,
            layering_method = layering_method,
            created_at = created_at
        )
    }

    companion object {
        fun fromDomain(c: SavedCombination): SavedCombinationEntity {
            return SavedCombinationEntity(
                id = c.id,
                fragrance_a_id = c.fragrance_a_id,
                fragrance_b_id = c.fragrance_b_id,
                fragrance_a_name = c.fragrance_a_name,
                fragrance_b_name = c.fragrance_b_name,
                fragrance_a_brand = c.fragrance_a_brand,
                fragrance_b_brand = c.fragrance_b_brand,
                compatibility_score = c.compatibility_score,
                chord_title = c.chord_title,
                explanation = c.explanation,
                layering_method = c.layering_method,
                created_at = c.created_at
            )
        }
    }
}
