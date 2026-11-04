package com.mutqin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val Green = Color(0xFF0F5132)
private val Gold = Color(0xFFD4AF37)

private val LightColors = lightColorScheme(
  primary = Green, secondary = Gold,
  background = Color(0xFFFAF6EC), surface = Color(0xFFFAF6EC),
  onBackground = Color(0xFF1B1B1B), onSurface = Color(0xFF1B1B1B)
)

private val DarkColors = darkColorScheme(
  primary = Gold, secondary = Gold,
  background = Color(0xFF0B1F17), surface = Color(0xFF0B1F17),
  onBackground = Color(0xFFEDE8D8), onSurface = Color(0xFFEDE8D8)
)

sealed interface Screen {
  data object SurahList : Screen
  data class Range(val surah: Int) : Screen
  data class Reader(val surah: Int, val from: Int, val to: Int) : Screen
  data object Recorder : Screen
  data object Tajweed : Screen
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val prefs = AppPrefs(applicationContext)
    val repo = QuranRepository.load(applicationContext)
    setContent {
      val systemDark = isSystemInDarkTheme()
      var dark by remember { mutableStateOf(prefs.darkOrNull() ?: systemDark) }
      DisposableEffect(dark) {
        val clear = android.graphics.Color.TRANSPARENT
        val style = if (dark) SystemBarStyle.dark(clear) else SystemBarStyle.light(clear, clear)
        this@MainActivity.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
        onDispose { }
      }
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = if (dark) DarkColors else LightColors) {
          Surface(modifier = Modifier.fillMaxSize()) {
            AppRoot(repo, prefs, dark) { value ->
              dark = value
              prefs.setDark(value)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AppRoot(repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit) {
  var screen by remember { mutableStateOf<Screen>(Screen.SurahList) }

  BackHandler(enabled = screen !is Screen.SurahList) {
    val current = screen
    screen = when (current) { is Screen.Reader -> Screen.Range(current.surah); is Screen.Range -> Screen.SurahList; Screen.Recorder -> Screen.SurahList; Screen.Tajweed -> Screen.SurahList; Screen.SurahList -> Screen.SurahList }
  }

  when (val s = screen) {
    is Screen.SurahList -> SurahListScreen(
      repo, prefs, dark, onDark,
      onOpenSurah = { screen = Screen.Range(it) },
      onResume = { surah, from, to -> screen = Screen.Reader(surah, from, to) },
      onOpenRecorder = { screen = Screen.Recorder },
      onOpenTajweed = { screen = Screen.Tajweed }
    )
    is Screen.Range -> RangeScreen(
      repo, s.surah,
      onBack = { screen = Screen.SurahList },
      onStart = { from, to ->
        prefs.setLastPosition(s.surah, from, to)
        screen = Screen.Reader(s.surah, from, to)
      }
    )
    is Screen.Reader -> ReaderScreen(
      repo, prefs, s.surah, s.from, s.to,
      onBack = { screen = Screen.Range(s.surah) }
    )
    Screen.Recorder -> RecorderScreen(onBack = { screen = Screen.SurahList })
    Screen.Tajweed -> TajweedScreen(onBack = { screen = Screen.SurahList })
  }
}
