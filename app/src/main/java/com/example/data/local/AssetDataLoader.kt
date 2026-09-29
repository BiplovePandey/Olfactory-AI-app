package com.example.data.local

import android.content.Context
import com.example.data.model.Brand
import com.example.data.model.Fragrance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object AssetDataLoader {

    suspend fun loadInitialFragrances(context: Context): List<Fragrance> = withContext(Dispatchers.IO) {
        val fragrances = mutableListOf<Fragrance>()
        try {
            val jsonString = context.assets.open("data/fragrances.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                fragrances.add(parseFragrance(obj))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fragrances
    }

    suspend fun loadBrands(context: Context): List<Brand> = withContext(Dispatchers.IO) {
        val brands = mutableListOf<Brand>()
        try {
            val jsonString = context.assets.open("data/brands.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                brands.add(
                    Brand(
                        id = obj.optInt("id", i + 1),
                        name = obj.optString("name", "Unknown"),
                        country = obj.optString("country", "India"),
                        brand_type = obj.optString("brand_type", "Niche"),
                        category = obj.optString("category", "Fine Fragrance"),
                        origin_style = obj.optString("origin_style", "Heritage"),
                        description = obj.optString("description", null),
                        founded_year = if (obj.has("founded_year")) obj.optInt("founded_year") else null,
                        city = obj.optString("city", null)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        brands
    }

    private fun parseFragrance(obj: JSONObject): Fragrance {
        fun jsonArrayToList(key: String): List<String> {
            val list = mutableListOf<String>()
            val arr = obj.optJSONArray(key) ?: return list
            for (i in 0 until arr.length()) {
                list.add(arr.optString(i))
            }
            return list
        }

        return Fragrance(
            id = obj.optInt("id", 0),
            brand_id = if (obj.has("brand_id")) obj.optInt("brand_id") else null,
            brand_name = obj.optString("brand_name", "Unknown"),
            name = obj.optString("name", "Fragrance"),
            format = obj.optString("format", "Eau de Parfum"),
            fragrance_type = obj.optString("fragrance_type", null),
            concentration = obj.optString("concentration", null),
            gender = obj.optString("gender", "Unisex"),
            category = obj.optString("category", null),
            description = obj.optString("description", null),
            origin_style = obj.optString("origin_style", "Contemporary"),
            price_inr = if (obj.has("price_inr")) obj.optDouble("price_inr") else null,
            volume_ml = if (obj.has("volume_ml")) obj.optDouble("volume_ml") else null,
            is_oil_based = obj.optBoolean("is_oil_based", false),
            fragrance_family = obj.optString("fragrance_family", "Woody"),
            top_notes = jsonArrayToList("top_notes"),
            middle_notes = jsonArrayToList("middle_notes"),
            base_notes = jsonArrayToList("base_notes"),
            season = jsonArrayToList("season"),
            occasion = jsonArrayToList("occasion"),
            intensity = obj.optInt("intensity", 6),
            sweetness = obj.optInt("sweetness", 5),
            freshness = obj.optInt("freshness", 6),
            longevity = obj.optString("longevity", "8 hours"),
            status = obj.optString("status", "verified"),
            brand_country = obj.optString("brand_country", null)
        )
    }
}
