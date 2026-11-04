package com.mutqin.app

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private data class AudioMoshaf(val id: Int, val title: String, val server: String, val surahs: Set<Int>)
private data class AudioReciter(val id: Int, val name: String, val moshaf: List<AudioMoshaf>)

private suspend fun loadReciters(): List<AudioReciter> = withContext(Dispatchers.IO) {
  val connection = URL("https://www.mp3quran.net/api/v3/reciters?language=ar").openConnection() as HttpURLConnection
  connection.connectTimeout = 15000
  connection.readTimeout = 20000
  try {
    val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
    val array = json.optJSONArray("reciters") ?: return@withContext emptyList()
    buildList {
      for (i in 0 until array.length()) {
        val item = array.optJSONObject(i) ?: continue
        val reads = item.optJSONArray("moshaf") ?: continue
        val moshaf = buildList {
          for (j in 0 until reads.length()) {
            val read = reads.optJSONObject(j) ?: continue
            val server = read.optString("server").trim()
            if (server.isBlank()) continue
            val ids = read.optString("surah_list").split(",").mapNotNull { it.trim().toIntOrNull() }.filter { it in 1..114 }.toSet()
            add(AudioMoshaf(read.optInt("id"), read.optString("name", "رواية غير محددة"), server, ids))
          }
        }
        if (moshaf.isNotEmpty()) add(AudioReciter(item.optInt("id"), item.optString("name", "قارئ"), moshaf))
      }
    }
  } finally { connection.disconnect() }
}

@Composable
fun RecitersScreen(onBack: () -> Unit) {
  var reciters by remember { mutableStateOf<List<AudioReciter>>(emptyList()) }
  var loading by remember { mutableStateOf(true) }
  var error by remember { mutableStateOf<String?>(null) }
  var query by remember { mutableStateOf("") }
  var selected by remember { mutableStateOf<AudioReciter?>(null) }
  var selectedRead by remember { mutableStateOf<AudioMoshaf?>(null) }
  var surah by remember { mutableIntStateOf(1) }
  var playing by remember { mutableStateOf(false) }
  var playerMessage by remember { mutableStateOf("اختر قارئًا وسورة ثم اضغط تشغيل") }
  var player by remember { mutableStateOf<MediaPlayer?>(null) }

  DisposableEffect(Unit) { onDispose { player?.release(); player = null } }

  LaunchedEffect(Unit) {
    try {
      reciters = loadReciters()
      if (reciters.isEmpty()) error = "لم تصل قائمة القراء. تحقق من اتصال الإنترنت."
    } catch (_: Exception) {
      error = "تعذر تحميل القراء. تحقق من الإنترنت ثم أعد فتح الصفحة."
    } finally { loading = false }
  }

  val filtered = remember(reciters, query) { reciters.filter { query.isBlank() || it.name.contains(query.trim(), ignoreCase = true) } }
  val availableSurahs = selectedRead?.surahs ?: emptySet()
  val colors = MaterialTheme.colorScheme

  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Spacer(Modifier.width(8.dp))
      Column(Modifier.weight(1f)) {
        Text("مكتبة القرّاء", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
        Text("تلاوات حقيقية من مصادر صوتية متاحة", fontSize = 11.sp, color = colors.onSurface.copy(alpha = .65f))
      }
    }

    if (selected != null) {
      Card(colors = CardDefaults.cardColors(containerColor = colors.surface), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
              Text(selected!!.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
              Text("اختر المصحف والرواية المتاحة لهذا القارئ", fontSize = 11.sp, color = colors.onSurface.copy(alpha = .65f))
            }
            TextButton(onClick = { selected = null; selectedRead = null; player?.release(); player = null; playing = false }) { Text("تغيير") }
          }
          selected!!.moshaf.forEach { read ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (selectedRead?.id == read.id) colors.primary.copy(alpha = .12f) else colors.surfaceVariant).clickable {
              selectedRead = read
              if (surah !in read.surahs) surah = read.surahs.minOrNull() ?: 1
            }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
              Column(Modifier.weight(1f)) {
                Text(read.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("${read.surahs.size} سورة متاحة", fontSize = 10.sp, color = colors.onSurface.copy(alpha = .65f))
              }
              RadioButton(selected = selectedRead?.id == read.id, onClick = {
                selectedRead = read
                if (surah !in read.surahs) surah = read.surahs.minOrNull() ?: 1
              })
            }
          }
          if (selectedRead != null) {
            var expanded by remember { mutableStateOf(false) }
            Box {
              OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("السورة: ${SURAH_NAMES[surah - 1]}") }
              DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.heightIn(max = 360.dp)) {
                (1..114).filter { it in availableSurahs }.forEach { n ->
                  DropdownMenuItem(text = { Text(SURAH_NAMES[n - 1]) }, onClick = { surah = n; expanded = false })
                }
              }
            }
            Button(onClick = {
              val read = selectedRead ?: return@Button
              val url = read.server.trimEnd('/') + "/" + surah.toString().padStart(3, '0') + ".mp3"
              try {
                player?.release()
                player = MediaPlayer().also { mp ->
                  playing = false
                  playerMessage = "جارٍ تحميل التلاوة..."
                  mp.setOnPreparedListener { it.start(); playing = true; playerMessage = "يعمل الآن: ${SURAH_NAMES[surah - 1]}" }
                  mp.setOnCompletionListener { playing = false; playerMessage = "انتهت السورة" }
                  mp.setOnErrorListener { _, _, _ -> playing = false; playerMessage = "تعذر تشغيل هذا التسجيل"; true }
                  mp.setDataSource(url)
                  mp.prepareAsync()
                }
              } catch (_: Exception) { playerMessage = "تعذر بدء التشغيل. جرّب تسجيلًا آخر." }
            }, modifier = Modifier.fillMaxWidth()) { Text("▶  تشغيل السورة") }
            OutlinedButton(onClick = {
              try {
                val mp = player
                if (mp != null && mp.isPlaying) { mp.pause(); playing = false; playerMessage = "متوقف مؤقتًا" }
                else if (mp != null) { mp.start(); playing = true; playerMessage = "يعمل الآن" }
              } catch (_: Exception) { playerMessage = "أعد تشغيل التلاوة" }
            }, enabled = player != null, modifier = Modifier.fillMaxWidth()) { Text(if (playing) "إيقاف مؤقت" else "استكمال") }
            Text(playerMessage, fontSize = 12.sp, color = colors.primary)
          }
        }
      }
    } else {
      OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("ابحث عن اسم القارئ") }, shape = RoundedCornerShape(14.dp))
      when {
        loading -> Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        error != null -> Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
          Text(error!!, color = colors.error)
          Button(onClick = { loading = true; error = null }) { Text("إعادة المحاولة") }
          LaunchedEffect(loading) {
            if (loading) {
              try { reciters = loadReciters(); if (reciters.isEmpty()) error = "قائمة القراء غير متاحة حاليًا." }
              catch (_: Exception) { error = "تعذر الاتصال بمصدر القراء." }
              finally { loading = false }
            }
          }
        }
        else -> {
          Text("عدد القراء المتاحين: ${reciters.size}", fontSize = 12.sp, color = colors.onSurface.copy(alpha = .65f))
          LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            items(filtered) { reciter ->
              Card(modifier = Modifier.fillMaxWidth().clickable {
                selected = reciter
                selectedRead = reciter.moshaf.firstOrNull()
                surah = selectedRead?.surahs?.minOrNull() ?: 1
                player?.release(); player = null; playing = false
              }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                  Box(Modifier.size(38.dp).background(colors.primary.copy(alpha = .1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text("🎙", fontSize = 18.sp) }
                  Spacer(Modifier.width(12.dp))
                  Column(Modifier.weight(1f)) {
                    Text(reciter.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${reciter.moshaf.size} مصحف/رواية متاحة", fontSize = 10.sp, color = colors.onSurface.copy(alpha = .6f))
                  }
                  Text("‹", fontSize = 25.sp, color = colors.primary)
                }
              }
            }
          }
        }
      }
    }
    Text("التشغيل يحتاج اتصالًا بالإنترنت، وتظهر فقط المصاحف والسور التي يوفرها المصدر.", fontSize = 10.sp, color = colors.onSurface.copy(alpha = .55f), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
  }
}
