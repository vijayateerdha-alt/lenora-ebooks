package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.BookEntity
import com.example.ui.components.BookCoverCard
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
  fun greeting_screenshot() {
    val sampleBook = BookEntity(
      id = "test_1",
      title = "Pride and Prejudice",
      author = "Jane Austen",
      authors = "Jane Austen",
      description = "A classic romance following Elizabeth Bennet and Mr. Darcy.",
      coverImageUrl = "https://www.gutenberg.org/cache/epub/1342/pg1342.cover.medium.jpg",
      thumbnailUrl = "https://www.gutenberg.org/cache/epub/1342/pg1342.cover.small.jpg",
      genres = "Classics, Romance",
      subjects = "Courtship, Sisters, Social classes",
      source = "Project Gutenberg",
      sourceId = "1342",
      sourceUrl = "https://www.gutenberg.org/ebooks/1342",
      hasAudiobook = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        BookCoverCard(book = sampleBook, onClick = {})
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

