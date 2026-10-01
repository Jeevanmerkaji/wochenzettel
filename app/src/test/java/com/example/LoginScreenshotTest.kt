package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.WochenzettelTheme
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
class LoginScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun login_screenshot() {
    composeTestRule.setContent {
      WochenzettelTheme {
        LoginScreen(
          isFirebaseConfigured = true,
          isLoading = false,
          errorMessage = null,
          onSignIn = { _, _ -> },
          onSignUp = { _, _ -> },
          onGoogleSignIn = {},
          onDismissError = {},
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/login.png")
  }
}
