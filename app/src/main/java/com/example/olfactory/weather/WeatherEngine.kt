package com.example.olfactory.weather

import com.example.data.model.Fragrance
import java.util.Calendar

data class WeatherCondition(
    val temperatureC: Int,
    val humidityPct: Int,
    val conditionId: String,
    val label: String,
    val season: String,
    val timeOfDay: String,
    val intensityFactor: Float = 1.0f,
    val freshnessFactor: Float = 1.0f,
    val projectionFactor: Float = 1.0f,
    val longevityHoursMod: Float = 0.0f
)

data class WeatherPreset(
    val id: String,
    val label: String,
    val temp: Int,
    val humidity: Int,
    val season: String,
    val icon: String
)

data class WeatherAlignment(
    val score: Int,
    val advisory: String
)

object WeatherEngine {

    val PRESETS = listOf(
        WeatherPreset(
            id = "monsoon_rain",
            label = "Tropical Monsoon (28°C • 75% Hum.)",
            temp = 28,
            humidity = 75,
            season = "Monsoon",
            icon = "🌧️"
        ),
        WeatherPreset(
            id = "sunny_warm",
            label = "Scorching Summer (36°C • 40% Hum.)",
            temp = 36,
            humidity = 40,
            season = "Summer",
            icon = "☀️"
        ),
        WeatherPreset(
            id = "crisp_autumn",
            label = "Crisp Autumn (21°C • 50% Hum.)",
            temp = 21,
            humidity = 50,
            season = "Fall",
            icon = "🍂"
        ),
        WeatherPreset(
            id = "chilly_winter",
            label = "Chilly Winter Evening (14°C • 55% Hum.)",
            temp = 14,
            humidity = 55,
            season = "Winter",
            icon = "❄️"
        ),
        WeatherPreset(
            id = "tropical_humid",
            label = "Coastal Sea Breeze (30°C • 85% Hum.)",
            temp = 30,
            humidity = 85,
            season = "Summer",
            icon = "🌊"
        ),
        WeatherPreset(
            id = "temperate",
            label = "Temperate Spring Day (24°C • 45% Hum.)",
            temp = 24,
            humidity = 45,
            season = "Spring",
            icon = "🌸"
        )
    )

    fun getDefaultEnvironment(): WeatherCondition {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeOfDay = when (hour) {
            in 5..11 -> "Morning"
            in 12..16 -> "Afternoon"
            in 17..21 -> "Evening"
            else -> "Night"
        }

        return WeatherCondition(
            temperatureC = 28,
            humidityPct = 72,
            conditionId = "monsoon_rain",
            label = "🌧️ Humid & Overcast • Post-Monsoon Breeze",
            season = "Monsoon",
            timeOfDay = timeOfDay,
            intensityFactor = 1.15f,
            freshnessFactor = 1.2f,
            projectionFactor = 1.1f,
            longevityHoursMod = -1.0f
        )
    }

    fun createWeather(
        temp: Int,
        humidity: Int,
        conditionId: String,
        timeOfDay: String = "Evening"
    ): WeatherCondition {
        val isHot = temp >= 30
        val isCold = temp <= 18
        val isHumid = humidity >= 65

        val season = when {
            temp <= 17 -> "Winter"
            conditionId == "monsoon_rain" -> "Monsoon"
            temp <= 23 -> "Fall"
            temp <= 27 -> "Spring"
            else -> "Summer"
        }

        val intensityFactor = if (isHot) (if (isHumid) 1.25f else 1.15f) else (if (isCold) 0.85f else 1.0f)
        val freshnessFactor = if (isHot) 1.3f else 1.0f
        val projectionFactor = if (isHumid) 1.2f else (if (isCold) 0.8f else 1.0f)
        val longevityMod = if (isHot) -2.0f else (if (isCold) 2.5f else 0.0f)

        val preset = PRESETS.firstOrNull { it.id == conditionId } ?: PRESETS[0]

        return WeatherCondition(
            temperatureC = temp,
            humidityPct = humidity,
            conditionId = conditionId,
            label = "${preset.icon} ${preset.label}",
            season = season,
            timeOfDay = timeOfDay,
            intensityFactor = intensityFactor,
            freshnessFactor = freshnessFactor,
            projectionFactor = projectionFactor,
            longevityHoursMod = longevityMod
        )
    }

    fun calculateWeatherAlignmentScore(
        fragrance: Fragrance?,
        weather: WeatherCondition
    ): WeatherAlignment {
        if (fragrance == null) {
            return WeatherAlignment(score = 75, advisory = "Balanced performance across ambient conditions.")
        }

        var score = 75
        val allNotes = (fragrance.top_notes + fragrance.middle_notes + fragrance.base_notes).joinToString(" ").lowercase()
        val family = fragrance.fragrance_family.lowercase()

        // Season alignment
        if (fragrance.season.any { it.equals(weather.season, ignoreCase = true) }) {
            score += 15
        }

        // Hot climate logic: penalize heavy gourmands, reward fresh, citrus, vetiver
        if (weather.temperatureC >= 30) {
            if (family.contains("gourmand") || allNotes.contains("vanilla") || allNotes.contains("caramel")) {
                score -= 20
            }
            if (family.contains("fresh") || allNotes.contains("citrus") || allNotes.contains("bergamot") ||
                allNotes.contains("vetiver") || allNotes.contains("khus")
            ) {
                score += 15
            }
        }

        // Cold climate logic: reward heavy woods, spices, oud, amber; penalize ultra-light colognes
        if (weather.temperatureC <= 18) {
            if (family.contains("woody") || family.contains("amber") || allNotes.contains("oud") ||
                allNotes.contains("cardamom") || allNotes.contains("sandalwood")
            ) {
                score += 15
            }
            if (family.contains("aquatic") || fragrance.intensity <= 4) {
                score -= 10
            }
        }

        // Monsoon & humid climate logic: petrichor, mitti attar, rose, and green grass bloom intensely
        if (weather.humidityPct >= 65 || weather.conditionId == "monsoon_rain") {
            if (allNotes.contains("mitti") || allNotes.contains("petrichor") || allNotes.contains("clay") ||
                allNotes.contains("rose") || allNotes.contains("khus") || fragrance.format == "Attar"
            ) {
                score += 20
            }
        }

        score = score.coerceIn(45, 99)

        val advisory = when {
            score >= 90 -> "Exceptional alignment with ${weather.temperatureC}°C ${weather.season} climate. Volatile top notes release gracefully into ambient air."
            score >= 75 -> "Harmonious equilibrium under ${weather.humidityPct}% humidity. Sillage and projection remain structured and elegant."
            else -> "Atmospheric friction: may diffuse densely in ${weather.temperatureC}°C warmth; consider anchoring pulse points with non-alcoholic sandalwood attar."
        }

        return WeatherAlignment(score = score, advisory = advisory)
    }
}
