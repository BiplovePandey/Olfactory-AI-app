package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Olfactory AI", appName)
  }

  @Test
  fun `test olfactory harmony computation`() {
    val fragA = com.example.data.model.Fragrance(
      id = 1,
      name = "Mitti Attar",
      brand_name = "Kannauj Heritage",
      fragrance_family = "Earthy",
      top_notes = listOf("Baked Earth"),
      middle_notes = listOf("Petrichor"),
      base_notes = listOf("Sandalwood Oil"),
      is_oil_based = true,
      format = "Attar"
    )
    val fragB = com.example.data.model.Fragrance(
      id = 2,
      name = "Bergamote 22",
      brand_name = "Le Labo",
      fragrance_family = "Citrus",
      top_notes = listOf("Bergamot", "Grapefruit"),
      middle_notes = listOf("Orange Blossom"),
      base_notes = listOf("Cedar", "Vetiver"),
      is_oil_based = false,
      format = "Eau de Parfum"
    )

    val result = com.example.olfactory.ml.OlfactoryEngine.scoreLayeringPair(fragA, fragB)
    org.junit.Assert.assertNotNull(result)
    org.junit.Assert.assertTrue(result!!.compatibility_score in 50..100)
    org.junit.Assert.assertTrue(result.is_cross_origin)
  }
}
