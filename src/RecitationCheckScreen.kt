package com.mutqin.app

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
  var endAyahInput by remember { mutableStateOf("1") }
  var sessionHistory by remember { mutableStateOf(AppPrefs(context).recitationSessions()) }
  val prefs = remember { AppPrefs(context) }
  val surah = (surahInput.toIntOrNull() ?: 1).coerceIn(1, 114)
  val count = repo.ayahCount(surah)
  val ayah = (ayahInput.toIntOrNull() ?: 1).coerceIn(1, count)
  val endAyah = (endAyahInput.toIntOrNull() ?: ayah).coerceIn(ayah, count)
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
      savedFile?.let { prefs.addRecitationSession(surah, ayah, endAyah, elapsed, it.name) }
      sessionHistory = prefs.recitationSessions()
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

  fun playSavedRecording(file: File) {
    if (!file.exists()) {
      status = "ملف هذا التسجيل غير موجود على الجهاز."
      return
    }
    try {
      player?.release()
      player = MediaPlayer().apply {
        setDataSource(file.absolutePath)
        setOnCompletionListener { completed ->
          completed.release()
          if (player === completed) player = null
          status = "انتهى تشغيل التسجيل."
        }
        prepare()
        start()
      }
      status = "تشغيل تسجيل جلسة المراجعة."
    } catch (_: Exception) {
      try { player?.release() } catch (_: Exception) {}
      player = null
      status = "تعذر تشغيل هذا التسجيل."
    }
  }

  val colors = MaterialTheme.colorScheme
  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding().verticalScroll(rememberScrollState())) {
    Column(Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Color(0xFF092E24)).padding(horizontal=20.dp,vertical=20.dp)) {
      Row(verticalAlignment=Alignment.CenterVertically) {
        Surface(onClick=onBack,shape=CircleShape,color=colors.onPrimary.copy(alpha=.12f)) { Text("‹",Modifier.padding(horizontal=15.dp,vertical=7.dp),fontSize=24.sp,color=colors.onPrimary) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
          Text("مُتْقِن  /  القرآن الكريم",fontSize=10.sp,color=colors.secondary,fontWeight=FontWeight.Bold)
          Text("التسميع والمراجعة",fontSize=21.sp,color=colors.onPrimary,fontWeight=FontWeight.ExtraBold)
        }
        Text("◉",fontSize=30.sp,color=colors.secondary)
      }
      Spacer(Modifier.height(22.dp))
      Text("DEENI  •  RECITATION",fontSize=10.sp,color=androidx.compose.ui.graphics.Color(0xFFD6B56D),fontWeight=FontWeight.Bold,letterSpacing=2.sp)
      Text("أنصت إلى تلاوتك.",fontSize=29.sp,color=androidx.compose.ui.graphics.Color(0xFFF6F0E1),fontWeight=FontWeight.Black)
      Text("حدّد مقطعك، سجّل قراءتك، ثم استمع إلى مراجعتك.",fontSize=12.sp,color=colors.onPrimary.copy(alpha=.78f))
      Spacer(Modifier.height(13.dp))
      Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Surface(shape=RoundedCornerShape(13.dp),color=colors.onPrimary.copy(alpha=.12f)) { Text("تسجيل محلي",Modifier.padding(horizontal=12.dp,vertical=8.dp),fontSize=10.sp,color=colors.onPrimary) }
        Surface(shape=RoundedCornerShape(13.dp),color=colors.onPrimary.copy(alpha=.12f)) { Text("حفظ الجلسات",Modifier.padding(horizontal=12.dp,vertical=8.dp),fontSize=10.sp,color=colors.onPrimary) }
      }
    }
    Column(Modifier.padding(horizontal=16.dp).offset(y=(-12).dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
    Card(shape = RoundedCornerShape(25.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), border=androidx.compose.foundation.BorderStroke(1.dp,colors.outlineVariant), elevation=CardDefaults.cardElevation(defaultElevation=0.dp)) {
      Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
          Text("موضع المراجعة", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,color=colors.primary)
          Spacer(Modifier.weight(1f))
          Text("١",fontSize=11.sp,fontWeight=FontWeight.Bold,color=colors.secondary)
        }
        OutlinedTextField(value = surahInput, onValueChange = { surahInput = it.filter(Char::isDigit).take(3); ayahInput = "1"; endAyahInput = "1" }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape=RoundedCornerShape(16.dp), label = { Text("رقم السورة (١–١١٤)") })
        Text("السورة: ${SURAH_NAMES[surah - 1]} • ${toArabic(count)} آية", color = colors.primary, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(value = ayahInput, onValueChange = { ayahInput = it.filter(Char::isDigit).take(4) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape=RoundedCornerShape(16.dp), label = { Text("رقم الآية") })
        OutlinedTextField(value = endAyahInput, onValueChange = { endAyahInput = it.filter(Char::isDigit).take(4) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape=RoundedCornerShape(16.dp), label = { Text("إلى الآية (نهاية المقطع)") })
        Text("المقطع المحدد: ${SURAH_NAMES[surah - 1]} — من ${toArabic(ayah)} إلى ${toArabic(endAyah)}", fontSize = 12.sp, color = colors.onSurfaceVariant)
        Text("عدد الآيات: ${toArabic(endAyah - ayah + 1)}", fontSize = 12.sp, color = colors.primary)
        Surface(shape = RoundedCornerShape(16.dp), color = colors.primary.copy(alpha=.08f)) {
          Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("آيات المقطع الذي ستسمّعه",fontSize=11.sp,fontWeight=FontWeight.Bold,color=colors.primary)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { (ayah..endAyah).forEach { number -> Text("${toArabic(number)}. ${repo.ayah(surah,number)}",fontSize=19.sp,lineHeight=34.sp,color=colors.onSurface) } }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
              OutlinedButton(onClick={ayahInput=(ayah-1).coerceAtLeast(1).toString()},enabled=ayah>1,modifier=Modifier.weight(1f)){Text("الآية السابقة")}
              OutlinedButton(onClick={ayahInput=(ayah+1).coerceAtMost(count).toString()},enabled=ayah<count,modifier=Modifier.weight(1f)){Text("الآية التالية")}
            }
          }
        }
      }
    }
    Card(shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), border=androidx.compose.foundation.BorderStroke(1.dp,androidx.compose.ui.graphics.Color(0xFFB8954B).copy(alpha=.55f)), elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
      Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Box(Modifier.size(104.dp).clip(CircleShape).background(if (recording) colors.error.copy(alpha = .13f) else colors.primary.copy(alpha = .10f)).then(Modifier.padding(5.dp)).border(1.dp,if(recording) colors.error.copy(alpha=.55f) else colors.secondary.copy(alpha=.65f),CircleShape), contentAlignment = Alignment.Center) {
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
          savedFile?.let { playSavedRecording(it) }
        }, enabled = !recording && savedFile?.exists() == true, modifier = Modifier.fillMaxWidth()) { Text("▶ استمع إلى تلاوتك") }
      }
    }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), border=androidx.compose.foundation.BorderStroke(1.dp,colors.outline.copy(alpha=.28f)), elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
      Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("سجل جلسات التسميع", fontWeight = FontWeight.ExtraBold, fontSize=16.sp, color = colors.onSurface)
        if (sessionHistory.isEmpty()) Text("لا توجد جلسات محفوظة بعد.", fontSize = 12.sp, color = colors.onSurfaceVariant) else sessionHistory.take(8).forEach { entry ->
          val parts = entry.split(":")
          if (parts.size >= 5) {
            val s = (parts[0].toIntOrNull() ?: 1).coerceIn(1,114)
            val f = parts[1].toIntOrNull() ?: 1
            val t = parts[2].toIntOrNull() ?: f
            val sec = parts[3].toIntOrNull() ?: 0
            val recordingFile = File(File(context.filesDir, "mutqin_sessions"), parts.drop(4).joinToString(":"))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("${SURAH_NAMES[s-1]} • الآيات ${toArabic(f)}–${toArabic(t)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
                Text("المدة ${toArabic(sec/60)}:${toArabic(sec%60)}", fontSize = 10.sp, color = colors.onSurfaceVariant)
              }
              OutlinedButton(onClick = { playSavedRecording(recordingFile) }, enabled = !recording && recordingFile.exists()) {
                Text("▶ تشغيل")
              }
            }
          }
        }
        Text("ما الذي تفعله هذه الجلسة؟", fontWeight = FontWeight.Bold, color = colors.primary)
        Text("• تلتقط صوتك مباشرة من الميكروفون وتعرض مستوى الصوت أثناء القراءة.", fontSize = 12.sp)
        Text("• تحفظ التسجيل داخل ملفات التطبيق بدل الحفظ المؤقت، ويمكنك الاستماع إليه.", fontSize = 12.sp)
        Text("• تحفظ بيانات آخر 20 جلسة: السورة ونطاق الآيات ومدة التسجيل.", fontSize = 12.sp)
        Text("• لا تحوّل تلاوتك إلى نص ولا تصدر درجة تجويد وهمية. التصحيح الدقيق للمخارج والمدود والغنة يحتاج نموذجًا قرآنيًا متخصصًا لم يُدمج بعد.", fontSize = 12.sp, color = colors.error)
      }
    }
    }
  }
}
