package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.TodoDatabase
import com.example.data.TaskRepository
import com.example.ui.TaskViewModel
import com.example.ui.TodoScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Before
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

  private lateinit var database: TodoDatabase

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      TodoDatabase::class.java
    ).allowMainThreadQueries().build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun greeting_screenshot() {
    val repository = TaskRepository(database.taskDao())
    val viewModel = TaskViewModel(repository)

    composeTestRule.setContent {
      MyApplicationTheme {
        TodoScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
