package com.mutqin.app

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun RecitationCheckScreen(repo: QuranRepository, onBack: () -> Unit) {
  val context = LocalContext.current
  var surahInput by remember { mutableStateOf("1") }
  var ayahInput by remember { mutableStateOf("1") }
  val surah = (surahInput.toIntOrNull() ?: 1).coerceIn(1, 114)
  val count = repo.ayahCount(surah)
  val ayah = (ayahInput.toIntOrNull() ?: 1).coerceIn(1, count)
  var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
  var recording by remember { mutableStateOf(false) }
  var elapsed by remember { mutableIntStateOf(0) }
  var amplitude by remember { mutableFloatStateOf(0f) }
  var status by remember { mutableStateOf("اختر موضع المراجعة، ثم ابدأ التلاوة.") }
  var savedFile by remember { mutableStateOf<File?>(null) }
  var player by remember { mutableStateOf<MediaPlayer?>(null) }
  var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
  val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    hasPermission = granted
    status = if (granted) "تم السماح بالميكروفون. ابدأ التلاوة." else "يلزم السماح بالميكروفون لاستخدام جلسة الصوت."
  }

  fun startCapture() {
    try {
      val folder = File(context.filesDir, "mutqin_sessions").apply { mkdirs() }
      val file = File(folder, "recitation_${System.currentTimeMillis()}.m4a")
      val r = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(context) else MediaRecorder()
      r.setAudioSource(MediaRecorder.AudioSource.MIC)
      r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
      r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
      r.setAudioEncodingBitRate(128000)
      r.setAudioSamplingRate(44100)
      r.setOutputFile(file.absolutePath)
      r.prepare()
      r.start()
      recorder = r
      savedFile = file
      elapsed = 0
      amplitude = 0f
      recording = true
      status = "الميكروفون يعمل الآن. اقرأ بهدوء ثم أوقف الجلسة."
    } catch (_: Exception) {
      try { recorder?.release() } catch (_: Exception) {}
      recorder = null
      recording = false
      status = "تعذر تشغيل الميكروفون. أغلق أي تطبيق يستخدمه ثم حاول مجددًا."
    }
  }

  fun stopCapture() {
    try {
      recorder?.stop()
      status = "انتهت الجلسة وحُفظ التسجيل على الجهاز."
    } catch (_: Exception) {
      savedFile = null
      status = "لم يكتمل التسجيل. أعد المحاولة."
    } finally {
      try { recorder?.release() } catch (_: Exception) {}
      recorder = null
      recording = false
      amplitude = 0f
    }
  }

  LaunchedEffect(recording) {
    while (recording) {
      delay(1000)
      elapsed += 1
      val value = try { recorder?.maxAmplitude ?: 0 } catch (_: Exception) { 0 }
      amplitude = (value / 32767f).coerceIn(0f, 1f)
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      try { recorder?.stop() } catch (_: Exception) {}
      try { recorder?.release() } catch (_: Exception) {}
      try { player?.release() } catch (_: Exception) {}
    }
  }

  val colors = MaterialTheme.colorScheme
  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Column(Modifier.weight(1f)) {
        Text("جلسة التسميع", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
        Text("تدريب صوتي خاص بك", fontSize = 11.sp, color = colors.onSurfaceVariant)
      }
      Text("🎙", fontSize = 27.sp)
    }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("موضع المراجعة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = surahInput, onValueChange = { surahInput = it.filter(Char::isDigit).take(3); ayahInput = "1" }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("رقم السورة (١–١١٤)") })
        Text("السورة: ${SURAH_NAMES[surah - 1]} • ${toArabic(count)} آية", color = colors.primary, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(value = ayahInput, onValueChange = { ayahInput = it.filter(Char::isDigit).take(4) }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("رقم الآية") })
        Text("الموضع المحدد: ${SURAH_NAMES[surah - 1]} — الآية ${toArabic(ayah)}", fontSize = 12.sp, color = colors.onSurfaceVariant)
      }
    }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)) {
      Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Box(Modifier.size(94.dp).clip(CircleShape).background(if (recording) colors.error.copy(alpha = .13f) else colors.primary.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
          Text(if (recording) "●" else "🎙", color = if (recording) colors.error else colors.primary, fontSize = 35.sp)
        }
        Text(if (recording) "جارٍ الاستماع لصوتك" else "جاهز لبدء التلاوة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("%02d:%02d".format(elapsed / 60, elapsed % 60), fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
        Row(Modifier.fillMaxWidth().height(54.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
          repeat(24) { i ->
            val wave = if (recording) ((amplitude * 18f + ((i * 7) % 9) + 3f) / 30f).coerceIn(.12f, 1f) else .12f
            Box(Modifier.weight(1f).fillMaxHeight(wave).clip(RoundedCornerShape(5.dp)).background(if (recording) colors.primary else colors.outlineVariant))
          }
        }
        Text(status, fontSize = 12.sp, color = colors.onSurfaceVariant)
        if (!hasPermission) {
          Button(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }, modifier = Modifier.fillMaxWidth()) { Text("السماح باستخدام الميكروفون") }
        } else if (!recording) {
          Button(onClick = { startCapture() }, modifier = Modifier.fillMaxWidth()) { Text("●  ابدأ جلسة التسميع") }
        } else {
          Button(onClick = { stopCapture() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = colors.error)) { Text("إيقاف وحفظ التسجيل") }
        }
        OutlinedButton(onClick = {
          val file = savedFile
          if (file != null && file.exists()) {
            try {
              player?.release()
              player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener { it.release(); player = null }
                prepare()
                start()
              }
              status = "تشغيل تسجيل جلسة المراجعة."
            } catch (_: Exception) { status = "تعذر تشغيل التسجيل." }
          }
        }, enabled = !recording && savedFile?.exists() == true, modifier = Modifier.fillMaxWidth()) { Text("▶ استمع إلى تلاوتك") }
      }
    }
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("ما الذي تفعله هذه الجلسة؟", fontWeight = FontWeight.Bold, color = colors.primary)
        Text("• تلتقط صوتك مباشرة من الميكروفون وتعرض مستوى الصوت أثناء القراءة.", fontSize = 12.sp)
        Text("• تحفظ التسجيل داخل ملفات التطبيق بدل الحفظ المؤقت، ويمكنك الاستماع إليه.", fontSize = 12.sp)
        Text("• لا تحوّل تلاوتك إلى نص ولا تصدر درجة تجويد وهمية. التصحيح الدقيق للمخارج والمدود والغنة يحتاج نموذجًا قرآنيًا متخصصًا لم يُدمج بعد.", fontSize = 12.sp, color = colors.error)
      }
    }
  }
}
