package com.example

import com.example.data.model.Fragrance
import com.example.olfactory.audio.AmbientSoundscapeEngine
import com.example.olfactory.weather.WeatherEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherAndAtelierTest {

    @Test
    fun testDefaultEnvironmentCreation() {
        val weather = WeatherEngine.getDefaultEnvironment()
        assertNotNull(weather)
        assertEquals(28, weather.temperatureC)
        assertEquals(72, weather.humidityPct)
        assertEquals("Monsoon", weather.season)
        assertTrue(weather.intensityFactor > 1.0f)
    }

    @Test
    fun testWeatherPresets() {
        assertTrue(WeatherEngine.PRESETS.isNotEmpty())
        val monsoon = WeatherEngine.PRESETS.first { it.id == "monsoon_rain" }
        assertEquals(28, monsoon.temp)
        assertEquals(75, monsoon.humidity)
    }

    @Test
    fun testWeatherAlignmentCalculation() {
        val mittiAttar = Fragrance(
            id = 1,
            name = "Kannauj Mitti Attar",
            format = "Attar",
            fragrance_family = "Earthy",
            top_notes = listOf("Wet Mud", "Petrichor"),
            middle_notes = listOf("Baked Alluvial Clay"),
            base_notes = listOf("Indian Sandalwood Oil"),
            season = listOf("Monsoon", "Summer"),
            is_oil_based = true
        )

        val monsoonWeather = WeatherEngine.createWeather(
            temp = 28,
            humidity = 75,
            conditionId = "monsoon_rain"
        )

        val alignment = WeatherEngine.calculateWeatherAlignmentScore(mittiAttar, monsoonWeather)
        assertTrue("Mitti Attar should have high alignment in monsoon rain", alignment.score >= 90)
        assertTrue(alignment.advisory.isNotBlank())
    }

    @Test
    fun testSoundscapeEnginePresets() {
        assertTrue(AmbientSoundscapeEngine.PRESETS.size >= 4)
        val soundscapes = AmbientSoundscapeEngine.PRESETS.map { it.id }
        assertTrue(soundscapes.contains("none"))
        assertTrue(soundscapes.contains("monsoon_deg"))
        assertTrue(soundscapes.contains("temple_breeze"))
        assertTrue(soundscapes.contains("amber_hearth"))
    }
}
