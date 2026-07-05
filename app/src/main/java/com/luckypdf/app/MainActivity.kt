package com.luckypdf.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.luckypdf.app.ui.LuckyPdfApp
import com.luckypdf.app.ui.theme.LuckyPdfTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      LuckyPdfTheme {
        LuckyPdfApp()
      }
    }
  }
}
