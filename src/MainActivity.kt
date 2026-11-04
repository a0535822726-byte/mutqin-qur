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

private val Green = Color(0xFF08745A)
private val Gold = Color(0xFFE2B85B)
private val LightColors = lightColorScheme(primary = Green, secondary = Color(0xFFB17B24), background = Color(0xFFF5F7F4), surface = Color.White, onBackground = Color(0xFF17231F), onSurface = Color(0xFF17231F), surfaceVariant = Color(0xFFE8F0EB))
private val DarkColors = darkColorScheme(primary = Color(0xFF65C6A4), secondary = Gold, background = Color(0xFF101A17), surface = Color(0xFF192520), onBackground = Color(0xFFEAF2ED), onSurface = Color(0xFFEAF2ED), surfaceVariant = Color(0xFF24362E))

sealed interface Screen {
  data object SurahList : Screen
  data class Range(val surah: Int) : Screen
  data class Reader(val surah: Int, val from: Int, val to: Int) : Screen
  data object Recorder : Screen
  data object Check : Screen
  data object Tajweed : Screen
  data object Stories : Screen
  data object Reciters : Screen
  data object DailyTools : Screen
  data class Tool(val id: String) : Screen
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
          Surface(Modifier.fillMaxSize()) {
            AppRoot(repo, prefs, dark) { value -> dark = value; prefs.setDark(value) }
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
    screen = when (val current = screen) {
      is Screen.Reader -> Screen.Range(current.surah)
      is Screen.Range -> Screen.SurahList
      Screen.Recorder, Screen.Check, Screen.Tajweed, Screen.Stories, Screen.Reciters -> Screen.SurahList
      Screen.DailyTools, is Screen.Tool -> Screen.SurahList
      Screen.SurahList -> Screen.SurahList
    }
  }
  when (val s = screen) {
    is Screen.SurahList -> SurahListScreen(repo, prefs, dark, onDark,
      onOpenSurah = { screen = Screen.Range(it) },
      onResume = { a, b, c -> screen = Screen.Reader(a, b, c) },
      onOpenRecorder = { screen = Screen.Recorder },
      onOpenCheck = { screen = Screen.Check },
      onOpenTajweed = { screen = Screen.Tajweed },
      onOpenStories = { screen = Screen.Stories },
      onOpenReciters = { screen = Screen.Reciters },
      onOpenDailyTools = { screen = Screen.DailyTools },
      onOpenTool = { screen = Screen.Tool(it) })
    is Screen.Range -> RangeScreen(repo, s.surah, onBack = { screen = Screen.SurahList },
      onStart = { a, b -> prefs.setLastPosition(s.surah, a, b); screen = Screen.Reader(s.surah, a, b) })
    is Screen.Reader -> ReaderScreen(repo, prefs, s.surah, s.from, s.to, onBack = { screen = Screen.Range(s.surah) })
    Screen.Recorder -> RecorderScreen(onBack = { screen = Screen.SurahList })
    Screen.Check -> RecitationCheckScreen(repo, onBack = { screen = Screen.SurahList })
    Screen.Tajweed -> TajweedScreen(onBack = { screen = Screen.SurahList })
    Screen.Stories -> StoriesScreen(onBack = { screen = Screen.SurahList })
    Screen.Reciters -> RecitersScreen(onBack = { screen = Screen.SurahList })
    Screen.DailyTools -> DailyToolsScreen(prefs, onBack = { screen = Screen.SurahList })
    is Screen.Tool -> DailyToolDetailScreen(prefs, s.id, onBack = { screen = Screen.SurahList })
  }
}
