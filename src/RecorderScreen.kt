package com.mutqin.app

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun RecorderScreen(prefs: AppPrefs, onBack: () -> Unit) {
  val context = LocalContext.current
  var hasPermission by remember {
    mutableStateOf(
      androidx.core.content.ContextCompat.checkSelfPermission(
        context, Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }
  var recording by remember { mutableStateOf(false) }
  var savedFile by remember { mutableStateOf(prefs.toolText("last_recorder_file").takeIf { it.isNotBlank() }?.let(::File)?.takeIf { it.exists() }) }
  var status by remember { mutableStateOf("اضغط السماح للميكروفون ثم ابدأ التسجيل") }
  var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
  var player by remember { mutableStateOf<MediaPlayer?>(null) }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasPermission = granted
    status = if (granted) "تم السماح بالميكروفون. يمكنك بدء التسجيل." else "لم يتم منح إذن الميكروفون."
  }

  fun startRecording() {
    try {
      val file = File(context.filesDir, "mutqin_recitation_${System.currentTimeMillis()}.m4a")
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
      prefs.setToolText("last_recorder_file", file.absolutePath)
      recording = true
      status = "جارٍ التسجيل… اقرأ بهدوء، ثم اضغط إيقاف."
    } catch (e: Exception) {
      recorder?.release()
      recorder = null
      recording = false
      status = "تعذر بدء التسجيل: تأكد أن الميكروفون غير مستخدم من تطبيق آخر."
    }
  }

  fun stopRecording() {
    try {
      recorder?.stop()
      status = "تم حفظ التسجيل مؤقتًا. يمكنك الاستماع إليه."
    } catch (e: Exception) {
      status = "انتهى التسجيل، لكن الملف لم يكتمل. أعد المحاولة."
      savedFile = null
    } finally {
      recorder?.release()
      recorder = null
      recording = false
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      try { recorder?.stop() } catch (_: Exception) {}
      recorder?.release()
      player?.release()
    }
  }

  Column(
    modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Text("استديو مُتقن", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
    Spacer(Modifier.height(12.dp))
    Card(modifier=Modifier.fillMaxWidth(),shape=androidx.compose.foundation.shape.RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) {
      Column(Modifier.fillMaxWidth().padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("🎙",fontSize=38.sp)
        Text("مساحة تسجيلك الصوتي",style=MaterialTheme.typography.titleLarge,fontWeight=androidx.compose.ui.text.font.FontWeight.Bold,color=MaterialTheme.colorScheme.onPrimaryContainer)
        Text("سجّل تلاوتك، أوقف التسجيل، ثم استمع إليه للمراجعة.",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onPrimaryContainer)
      }
    }
    Text(status, style = MaterialTheme.typography.bodyMedium)
    if (!hasPermission) {
      Button(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
        Text("السماح باستخدام الميكروفون")
      }
    } else if (!recording) {
      Button(onClick = { startRecording() }, modifier = Modifier.fillMaxWidth()) {
        Text("● بدء التسجيل")
      }
    } else {
      Button(onClick = { stopRecording() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
        Text("إيقاف التسجيل")
      }
    }
    OutlinedButton(
      onClick = {
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
            status = "يتم تشغيل تسجيلك الآن."
          } catch (_: Exception) {
            status = "تعذر تشغيل التسجيل."
          }
        }
      },
      enabled = !recording && savedFile?.exists() == true,
      modifier = Modifier.fillMaxWidth()
    ) { Text("▶ الاستماع إلى آخر تسجيل") }
    Text(
      "ملاحظة: هذه ميزة تسجيل واستماع فقط؛ لا تحكم آلي على صحة التجويد أو مخارج الحروف في هذه النسخة.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.secondary
    )
  }
}
