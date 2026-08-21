package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.styleai.data.datasource.FashionDataSource
import com.example.styleai.ui.components.AccessoryCard
import com.example.styleai.ui.components.StyleScoreBadge
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun scoreBadge_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        StyleScoreBadge(score = 94)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/score_badge.png")
  }

  @Test
  fun accessoryCard_screenshot() {
    val sampleAccessory = FashionDataSource.sampleInspirations.first().sampleAnalysis.accessories.first()
    composeTestRule.setContent {
      MyApplicationTheme {
        AccessoryCard(item = sampleAccessory)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/accessory_card.png")
  }
}
