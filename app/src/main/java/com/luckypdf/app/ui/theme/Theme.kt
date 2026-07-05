package com.luckypdf.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LuckyPdfDarkColorScheme =
  darkColorScheme(
    primary = ACCENT_PRIMARY,
    secondary = ACCENT_SECONDARY,
    tertiary = ACCENT_TERTIARY,
    background = BG_BASE,
    surface = BG_PANEL,
    surfaceVariant = BG_ELEVATED,
    error = SEMANTIC_ERROR,
    onPrimary = ACCENT_ON,
    onSecondary = ACCENT_ON,
    onTertiary = ACCENT_ON,
    onBackground = TEXT_PRIMARY,
    onSurface = TEXT_PRIMARY,
    onError = TEXT_ON_ACCENT,
    outline = STROKE,
    inverseOnSurface = BG_BASE,
  )

private val LuckyPdfLightColorScheme =
  lightColorScheme(
    primary = ACCENT_PRIMARY,
    secondary = ACCENT_SECONDARY,
    tertiary = ACCENT_TERTIARY,
    background = BG_BASE,
    surface = BG_PANEL,
    surfaceVariant = BG_ELEVATED,
    error = SEMANTIC_ERROR,
    onPrimary = ACCENT_ON,
    onSecondary = ACCENT_ON,
    onTertiary = ACCENT_ON,
    onBackground = TEXT_PRIMARY,
    onSurface = TEXT_PRIMARY,
    onError = TEXT_ON_ACCENT,
    outline = STROKE,
    inverseOnSurface = BG_BASE,
  )

@Composable
fun LuckyPdfTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme: ColorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> LuckyPdfDarkColorScheme
      else -> LuckyPdfLightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = LuckyPdfTypography, content = content)
}
