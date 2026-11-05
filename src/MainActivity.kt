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

private val Green=Color(0xFF0F5132)
private val Gold=Color(0xFFD4AF37)
private val LightColors=lightColorScheme(primary=Green,secondary=Gold,background=Color(0xFFFAF6EC),surface=Color(0xFFFAF6EC),onBackground=Color(0xFF1B1B1B),onSurface=Color(0xFF1B1B1B))
private val DarkColors=darkColorScheme(primary=Gold,secondary=Gold,background=Color(0xFF0B1F17),surface=Color(0xFF0B1F17),onBackground=Color(0xFFEDE8D8),onSurface=Color(0xFFEDE8D8))

sealed interface Screen {
  data object Home:Screen
  data object SurahList:Screen
  data object Recitation:Screen
  data object Adhkar:Screen
  data object Tasbih:Screen
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
  BackHandler(enabled=screen !is Screen.Home) {
    screen=when(val current=screen) {
      is Screen.Reader->Screen.Range(current.surah)
      is Screen.Range->Screen.SurahList
      else->Screen.Home
    }
  }
  when(val s=screen) {
    is Screen.Home->DeeniHomeScreen(dark,onDark,{screen=Screen.SurahList},{screen=Screen.Recitation},{screen=Screen.Adhkar},{screen=Screen.Tasbih})
    is Screen.SurahList->SurahListScreen(repo,prefs,dark,onDark,{screen=Screen.Range(it)},{surah,from,to->screen=Screen.Reader(surah,from,to)})
    is Screen.Range->RangeScreen(repo,s.surah,{screen=Screen.SurahList},{from,to->prefs.setLastPosition(s.surah,from,to);screen=Screen.Reader(s.surah,from,to)})
    is Screen.Reader->ReaderScreen(repo,prefs,s.surah,s.from,s.to,{screen=Screen.Range(s.surah)})
    is Screen.Recitation->RecitationCheckScreen(repo,{screen=Screen.Home})
    is Screen.Adhkar->AdhkarScreen({screen=Screen.Home})
    is Screen.Tasbih->TasbihScreen(prefs,{screen=Screen.Home})
  }
}
