package com.mutqin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val Green = Color(0xFF174B3E)
private val Gold = Color(0xFFB18A4A)
private val LightColors = lightColorScheme(
  primary=Color(0xFF174B3E), onPrimary=Color(0xFFF8F6EF),
  secondary=Color(0xFFAD8545), onSecondary=Color(0xFF171D19),
  tertiary=Color(0xFF43866B), onTertiary=Color(0xFFFFFFFF),
  background=Color(0xFFF4F3EE), onBackground=Color(0xFF18251F),
  surface=Color(0xFFFFFEFA), onSurface=Color(0xFF18251F),
  surfaceVariant=Color(0xFFE8EEE8), onSurfaceVariant=Color(0xFF617168),
  outline=Color(0xFFB9C5BB)
)
private val DarkColors = darkColorScheme(
  primary=Color(0xFF153D32), onPrimary=Color(0xFFF5F1E7),
  secondary=Color(0xFFD2B477), onSecondary=Color(0xFF202016),
  tertiary=Color(0xFF78B99A), onTertiary=Color(0xFF10251D),
  background=Color(0xFF081511), onBackground=Color(0xFFF1EFE6),
  surface=Color(0xFF11231D), onSurface=Color(0xFFF1EFE6),
  surfaceVariant=Color(0xFF1A3028), onSurfaceVariant=Color(0xFFB4C4B9),
  outline=Color(0xFF3A5B4B)
)

sealed interface Screen {
  data object Home:Screen
  data object SurahList:Screen
  data object Recitation:Screen
  data object Adhkar:Screen
  data object Tasbih:Screen
  data object LearningHub:Screen
  data class SectionDetail(val section:String):Screen
  data class Range(val surah:Int):Screen
  data class Reader(val surah:Int,val from:Int,val to:Int):Screen
}

class MainActivity:ComponentActivity() {
  override fun onCreate(savedInstanceState:Bundle?) {
    super.onCreate(savedInstanceState)
    val prefs=AppPrefs(applicationContext)
    val repo=QuranRepository.load(applicationContext)
    setContent {
      val systemDark=isSystemInDarkTheme()
      var dark by remember { mutableStateOf(prefs.darkOrNull() ?: systemDark) }
      DisposableEffect(dark) {
        val clear=android.graphics.Color.TRANSPARENT
        val style=if(dark) SystemBarStyle.dark(clear) else SystemBarStyle.light(clear,clear)
        this@MainActivity.enableEdgeToEdge(statusBarStyle=style,navigationBarStyle=style)
        onDispose {}
      }
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme=if(dark) DarkColors else LightColors) {
          Surface(Modifier.fillMaxSize()) {
            AppRoot(repo,prefs,dark,{v->dark=v;prefs.setDark(v)})
          }
        }
      }
    }
  }
}

@Composable
fun AppRoot(repo:QuranRepository,prefs:AppPrefs,dark:Boolean,onDark:(Boolean)->Unit) {
  var screen by remember { mutableStateOf<Screen>(Screen.Home) }
  var showSplash by remember { mutableStateOf(true) }
  LaunchedEffect(Unit) {
    delay(1800)
    showSplash = false
  }
  if (showSplash) {
    DeeniSplashScreen()
    return
  }
  BackHandler(enabled=screen !is Screen.Home) {
    screen=when(val current=screen) {
      is Screen.Reader->Screen.Range(current.surah)
      is Screen.Range->Screen.SurahList
      is Screen.SectionDetail->Screen.LearningHub
      else->Screen.Home
    }
  }
  when(val s=screen) {
    is Screen.Home->DeeniHomeScreen(dark,onDark,{screen=Screen.SurahList},{screen=Screen.Recitation},{screen=Screen.Adhkar},{screen=Screen.Tasbih},{screen=Screen.LearningHub})
    is Screen.SurahList->SurahListScreen(repo,prefs,dark,onDark,{screen=Screen.Range(it)},{surah,from,to->screen=Screen.Reader(surah,from,to)})
    is Screen.Range->RangeScreen(repo,s.surah,{screen=Screen.SurahList},{from,to->prefs.setLastPosition(s.surah,from,to);screen=Screen.Reader(s.surah,from,to)})
    is Screen.Reader->ReaderScreen(repo,prefs,s.surah,s.from,s.to,{screen=Screen.Range(s.surah)},{savedSurah,savedAyah->screen=Screen.Reader(savedSurah,savedAyah,savedAyah)})
    is Screen.Recitation->RecitationCheckScreen(repo,{screen=Screen.Home})
    is Screen.Adhkar->AdhkarScreen({screen=Screen.Home})
    is Screen.Tasbih->TasbihScreen(prefs,{screen=Screen.Home})
    is Screen.LearningHub->LearningHubScreen({screen=Screen.Home},{screen=Screen.SectionDetail(it)})
    is Screen.SectionDetail->LearningSectionScreen(s.section,{screen=Screen.LearningHub})
  }
}


@Composable
private fun DeeniSplashScreen() {
  val ink = Color(0xFF173D32)
  val gold = Color(0xFFC5A45B)
  Box(
    modifier = Modifier.fillMaxSize().background(Color(0xFFFFFEFC)).safeDrawingPadding().padding(17.dp),
    contentAlignment = Alignment.Center
  ) {
    Box(
      Modifier.fillMaxSize()
        .border(1.dp,gold.copy(alpha=.42f),RoundedCornerShape(30.dp))
        .padding(7.dp)
        .border(1.dp,ink.copy(alpha=.10f),RoundedCornerShape(25.dp))
    )
    Column(
      Modifier.fillMaxWidth().padding(horizontal=22.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text("۞",color=gold,fontSize=35.sp,fontWeight=FontWeight.Light)
      Spacer(Modifier.height(19.dp))
      Text(
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        color = ink,
        fontSize = 25.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(28.dp))
      Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center) {
        Box(Modifier.width(38.dp).height(1.dp).background(gold.copy(alpha=.65f),RoundedCornerShape(2.dp)))
        Text("  ✦  ",color=gold,fontSize=14.sp)
        Box(Modifier.width(38.dp).height(1.dp).background(gold.copy(alpha=.65f),RoundedCornerShape(2.dp)))
      }
      Spacer(Modifier.height(25.dp))
      Box(
        Modifier.size(112.dp).border(1.dp,gold.copy(alpha=.65f),RoundedCornerShape(34.dp))
          .padding(6.dp).border(1.dp,ink.copy(alpha=.13f),RoundedCornerShape(29.dp)),
        contentAlignment=Alignment.Center
      ) {
        Text("مُتْقِن", color = ink, fontSize = 43.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
      }
      Spacer(Modifier.height(12.dp))
    }
  }
}
