package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.styleai.data.datasource.FashionDataSource
import com.example.styleai.data.local.toEntity
import com.example.styleai.data.local.toModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    assertEquals("StyleAI", appName)
  }

  @Test
  fun `verify curated lookbooks and sample analyses`() {
    val samples = FashionDataSource.sampleInspirations
    assertTrue(samples.isNotEmpty())
    val firstLook = samples.first()
    assertNotNull(firstLook.title)
    assertTrue(firstLook.sampleAnalysis.score in 80..100)
    assertTrue(firstLook.sampleAnalysis.colorPalette.isNotEmpty())
    assertTrue(firstLook.sampleAnalysis.accessories.isNotEmpty())
  }

  @Test
  fun `verify OutfitEntity serialization roundtrip`() {
    val sample = FashionDataSource.sampleInspirations.first().sampleAnalysis
    val entity = sample.toEntity()
    val restored = entity.toModel()
    assertEquals(sample.title, restored.title)
    assertEquals(sample.score, restored.score)
    assertEquals(sample.colorPalette.size, restored.colorPalette.size)
    assertEquals(sample.accessories.size, restored.accessories.size)
  }
}
