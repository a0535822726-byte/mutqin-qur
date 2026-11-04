package com.mutqin.app

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
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
  var recognizedText by remember { mutableStateOf("") }
  var matchPercent by remember { mutableIntStateOf(-1) }
  var savedFile by remember { mutableStateOf<File?>(null) }
  var player by remember { mutableStateOf<MediaPlayer?>(null) }
  var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
  val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    hasPermission = granted
    status = if (granted) "تم السماح بالميكروفون. ابدأ التلاوة." else "يلزم السماح بالميكروفون لاستخدام جلسة الصوت."
  }

  val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val heard = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
      recognizedText = heard
      matchPercent = approximateWordMatch(repo.ayah(surah, ayah), heard)
      status = if (heard.isBlank()) "لم ألتقط كلمات واضحة. حاول في مكان هادئ." else "انتهى فحص الكلمات التقريبي. راجع النتيجة بنفسك."
    } else {
      status = "لم يكتمل فحص الكلمات. يمكنك المحاولة مرة أخرى."
    }
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
    var ticks = 0
    while (recording) {
      delay(120)
      val value = try { recorder?.maxAmplitude ?: 0 } catch (_: Exception) { 0 }
      amplitude = (value / 32767f).coerceIn(0f, 1f)
      ticks++
      if (ticks % 8 == 0) elapsed += 1
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
        Surface(shape = RoundedCornerShape(16.dp), color = colors.primary.copy(alpha=.08f)) {
          Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("الآية التي ستسمّعها",fontSize=11.sp,fontWeight=FontWeight.Bold,color=colors.primary)
            Text(repo.ayah(surah,ayah),fontSize=21.sp,lineHeight=38.sp,color=colors.onSurface)
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
              OutlinedButton(onClick={ayahInput=(ayah-1).coerceAtLeast(1).toString()},enabled=ayah>1,modifier=Modifier.weight(1f)){Text("الآية السابقة")}
              OutlinedButton(onClick={ayahInput=(ayah+1).coerceAtMost(count).toString()},enabled=ayah<count,modifier=Modifier.weight(1f)){Text("الآية التالية")}
            }
          }
        }
      }
    }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)) {
      Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Box(Modifier.size(94.dp).clip(CircleShape).background(if (recording) colors.error.copy(alpha = .13f) else colors.primary.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
          Text(if (recording) "●" else "🎙", color = if (recording) colors.error else colors.primary, fontSize = 35.sp)
        }
        Text(if (recording) "جارٍ الاستماع لصوتك" else "جاهز لبدء التلاوة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("%02d:%02d".format(elapsed / 60, elapsed % 60), fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
          Text("مستوى إشارة الميكروفون",fontSize=11.sp,color=colors.onSurfaceVariant)
          Text("${(amplitude*100).toInt()}%",fontSize=11.sp,fontWeight=FontWeight.Bold,color=colors.primary)
        }
        LinearProgressIndicator(progress={amplitude},modifier=Modifier.fillMaxWidth().height(7.dp).clip(CircleShape))
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
          val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "أعد تلاوة الآية المحددة بوضوح")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
          }
          try { speechLauncher.launch(intent) } catch (_: Exception) {
            status = "ميزة التعرف الصوتي غير متاحة على هذا الجهاز."
          }
        }, enabled = !recording, modifier = Modifier.fillMaxWidth()) {
          Text("اختبار كلمات الآية بالميكروفون")
        }
        if (recognizedText.isNotBlank()) {
          Card(shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
            Column(Modifier.fillMaxWidth().padding(13.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
              Text("النص الذي التقطه التعرف الصوتي",fontWeight=FontWeight.Bold,color=colors.primary,fontSize=13.sp)
              Text(recognizedText,fontSize=15.sp,lineHeight=25.sp)
              Text("تطابق الكلمات التقريبي: $matchPercent٪",fontWeight=FontWeight.Bold,color=colors.primary)
              Text("هذه مقارنة آلية تقريبية للكلمات فقط؛ قد يخطئ التعرف في ألفاظ القرآن، ولا تقيس المخارج أو الغنة أو المدود أو أحكام التجويد.",fontSize=11.sp,color=colors.error)
            }
          }
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
        Text("• فحص الكلمات يستخدم خدمة التعرف الصوتي العربية وقد يحتاج اتصالًا بالإنترنت. النتيجة تقريبية وليست تقييمًا للتجويد؛ المخارج والغنة والمدود تحتاج نموذجًا قرآنيًا متخصصًا.", fontSize = 12.sp, color = colors.error)
      }
    }
  }
}

private fun normalizeRecitationWords(value: String): List<String> {
  val withoutMarks = value.replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"), "")
  return withoutMarks.replace(Regex("[^\\u0621-\\u064A\\s]"), " ")
    .replace('أ','ا').replace('إ','ا').replace('آ','ا').replace('ى','ي')
    .trim().split(Regex("\\s+")).filter { it.isNotBlank() }
}

private fun approximateWordMatch(reference: String, heard: String): Int {
  val a = normalizeRecitationWords(reference)
  val b = normalizeRecitationWords(heard)
  if (a.isEmpty()) return 0
  val previous = IntArray(b.size + 1)
  val current = IntArray(b.size + 1)
  for (i in 1..a.size) {
    for (j in 1..b.size) {
      current[j] = if (a[i - 1] == b[j - 1]) previous[j - 1] + 1
        else maxOf(previous[j], current[j - 1])
    }
    for (j in previous.indices) previous[j] = current[j]
    current.fill(0)
  }
  return ((previous[b.size] * 100f) / a.size).toInt().coerceIn(0,100)
}
