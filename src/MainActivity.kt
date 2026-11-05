package com.mutqin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val Green = Color(0xFF0B5D49)
private val Gold = Color(0xFFD8AD55)
private val LightColors = lightColorScheme(primary = Green, onPrimary = Color.White, primaryContainer = Color(0xFFE0EEE7), onPrimaryContainer = Color(0xFF123D30), secondary = Color(0xFF9A6B24), onSecondary = Color.White, background = Color(0xFFF4F6F1), surface = Color(0xFFFFFEFA), onBackground = Color(0xFF17251F), onSurface = Color(0xFF17251F), surfaceVariant = Color(0xFFE7EDE7), onSurfaceVariant = Color(0xFF63736A), outline = Color(0xFFD5DED6))
private val DarkColors = darkColorScheme(primary = Color(0xFF83CDB0), onPrimary = Color(0xFF073B2D), primaryContainer = Color(0xFF1D493A), onPrimaryContainer = Color(0xFFD5F0E2), secondary = Gold, background = Color(0xFF0D1713), surface = Color(0xFF15221C), onBackground = Color(0xFFEAF2EC), onSurface = Color(0xFFEAF2EC), surfaceVariant = Color(0xFF24362D), onSurfaceVariant = Color(0xFFB5C6BA), outline = Color(0xFF3A5045))
private val MutqinShapes = Shapes(extraSmall = RoundedCornerShape(10.dp), small = RoundedCornerShape(15.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(26.dp), extraLarge = RoundedCornerShape(32.dp))
private val MutqinTypography = Typography(bodyLarge = TextStyle(fontSize = 16.sp), bodyMedium = TextStyle(fontSize = 14.sp), bodySmall = TextStyle(fontSize = 12.sp), titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold), headlineSmall = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold))

sealed interface Screen {
  data object SurahList : Screen
  data class Range(val surah: Int) : Screen
  data class Reader(val surah: Int, val from: Int, val to: Int) : Screen
  data object Recorder : Screen
  data object Check : Screen
  data object Tajweed : Screen
  data object Stories : Screen
  data object Circle : Screen
  data object Reciters : Screen
  data object DailyTools : Screen
  data object Quiz : Screen
  data object RandomAyah : Screen
  data object MemorizationChallenge : Screen
  data object KhatmahPlanner : Screen
  data object WordSearch : Screen
  data object AyahContext : Screen
  data object SurahStatistics : Screen
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
        MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, shapes = MutqinShapes, typography = MutqinTypography) {
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
      Screen.Recorder, Screen.Check, Screen.Tajweed, Screen.Stories, Screen.Reciters, Screen.Circle -> Screen.SurahList
      Screen.DailyTools, Screen.Quiz, Screen.RandomAyah, Screen.MemorizationChallenge, Screen.KhatmahPlanner, Screen.WordSearch, Screen.AyahContext, Screen.SurahStatistics, is Screen.Tool -> Screen.SurahList
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
      onOpenCircle = { screen = Screen.Circle },
      onOpenReciters = { screen = Screen.Reciters },
      onOpenDailyTools = { screen = Screen.DailyTools },
      onOpenTool = { screen = Screen.Tool(it) })
    is Screen.Range -> RangeScreen(repo, s.surah, onBack = { screen = Screen.SurahList },
      onStart = { a, b -> prefs.setLastPosition(s.surah, a, b); screen = Screen.Reader(s.surah, a, b) })
    is Screen.Reader -> ReaderScreen(repo, prefs, s.surah, s.from, s.to, onBack = { screen = Screen.Range(s.surah) })
    Screen.Recorder -> RecorderScreen(prefs, onBack = { screen = Screen.SurahList })
    Screen.Check -> RecitationCheckScreen(repo, onBack = { screen = Screen.SurahList })
    Screen.Tajweed -> TajweedScreen(onBack = { screen = Screen.SurahList })
    Screen.Stories -> StoriesScreen(prefs, onBack = { screen = Screen.SurahList })
    Screen.Circle -> QuranCircleScreen(prefs, onBack = { screen = Screen.SurahList })
    Screen.Reciters -> RecitersScreen(onBack = { screen = Screen.SurahList })
    Screen.DailyTools -> DailyToolsScreen(prefs, onBack = { screen = Screen.SurahList })
    Screen.Quiz -> QuranQuizScreen(onBack = { screen = Screen.SurahList })
    Screen.RandomAyah -> RandomAyahScreen(repo, onBack = { screen = Screen.SurahList })
    Screen.MemorizationChallenge -> MemorizationChallengeScreen(repo, prefs, onBack = { screen = Screen.SurahList })
    Screen.KhatmahPlanner -> KhatmahPlannerScreen(prefs, onBack = { screen = Screen.SurahList })
    Screen.WordSearch -> QuranWordSearchScreen(repo, onBack = { screen = Screen.SurahList })
    Screen.AyahContext -> AyahContextScreen(repo, onBack = { screen = Screen.SurahList })
    Screen.SurahStatistics -> SurahStatisticsScreen(repo, onBack = { screen = Screen.SurahList })
    is Screen.Tool -> when (s.id) {
      "unique_quiz" -> QuranQuizScreen(onBack = { screen = Screen.SurahList })
      "unique_random_ayah" -> RandomAyahScreen(repo, onBack = { screen = Screen.SurahList })
      "unique_memorization" -> MemorizationChallengeScreen(repo, prefs, onBack = { screen = Screen.SurahList })
      "unique_khatmah" -> KhatmahPlannerScreen(prefs, onBack = { screen = Screen.SurahList })
      else -> DailyToolDetailScreen(prefs, s.id, onBack = { screen = Screen.SurahList })
    }
  }
}
