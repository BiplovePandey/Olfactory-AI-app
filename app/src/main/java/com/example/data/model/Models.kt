package com.example.data.model

data class Fragrance(
    val id: Int,
    val brand_id: Int? = null,
    val brand_name: String = "",
    val name: String = "",
    val format: String = "Eau de Parfum",
    val fragrance_type: String? = null,
    val concentration: String? = null,
    val gender: String = "Unisex",
    val category: String? = null,
    val description: String? = null,
    val origin_style: String = "Contemporary",
    val price_inr: Double? = null,
    val volume_ml: Double? = null,
    val is_oil_based: Boolean = false,
    val fragrance_family: String = "Woody",
    val top_notes: List<String> = emptyList(),
    val middle_notes: List<String> = emptyList(),
    val base_notes: List<String> = emptyList(),
    val season: List<String> = emptyList(),
    val occasion: List<String> = emptyList(),
    val intensity: Int = 6,
    val sweetness: Int = 5,
    val freshness: Int = 6,
    val longevity: String = "8 hours",
    val status: String? = "verified",
    val brand_country: String? = null,
    val vector: List<Float>? = null
)

data class Brand(
    val id: Int,
    val name: String,
    val country: String,
    val brand_type: String = "Niche",
    val category: String = "Fine Fragrance",
    val origin_style: String = "Heritage",
    val description: String? = null,
    val founded_year: Int? = null,
    val city: String? = null
)

data class OlfactoryVector8D(
    val freshness: Int,
    val sweetness: Int,
    val intensity: Int,
    val woody: Int,
    val floral: Int,
    val warm_resinous_spices: Int,
    val earthy_clay: Int,
    val longevity_fixative: Int
)

data class CompatibilityBreakdown(
    val note_compatibility: Int,
    val user_preference_match: Int,
    val season_compatibility: Int,
    val occasion_compatibility: Int,
    val complementary_note_score: Int,
    val diversity_factor: Int
)

data class WhyItWorks(
    val opening_harmony: String,
    val drydown_depth: String,
    val application_tip: String
)

data class LayeringResult(
    val id: String,
    val fragrance_a: Fragrance,
    val fragrance_b: Fragrance,
    val compatibility_score: Int,
    val chord_title: String,
    val breakdown: CompatibilityBreakdown,
    val is_cross_origin: Boolean,
    val origin_pairing_type: String,
    val layering_method: String,
    val explanation: String,
    val why_it_works: WhyItWorks,
    val best_season: String,
    val best_occasion: String,
    val best_time_of_day: String
)

data class SavedCombination(
    val id: Int = 0,
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
)

data class UserPreferences(
    val sweetness: Int = 5,
    val freshness: Int = 6,
    val intensity: Int = 6,
    val favorite_family: List<String> = listOf("Woody", "Earthy", "Spicy"),
    val preferred_notes: List<String> = listOf("Mitti", "Sandalwood", "Rose", "Bergamot"),
    val season: String = "Summer",
    val occasion: String = "Signature",
    val time_of_day: String = "Any",
    val origin_filter: String = "all",
    val format_filter: String = "all"
)

data class UserProfile(
    val id: Int = 1,
    val name: String = "Bespoke Connoisseur",
    val email: String = "connoisseur@olfactory.ai",
    val persona: String = "Master Atelier Layerer",
    val memberSince: String = "2026",
    val collectionCount: Int = 5,
    val layeringCount: Int = 12
)
