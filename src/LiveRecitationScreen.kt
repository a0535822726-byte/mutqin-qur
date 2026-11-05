package com.mutqin.app

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun LiveRecitationScreen(repo: QuranRepository, onBack: () -> Unit) {
    val context = LocalContext.current
    var surahInput by remember { mutableStateOf("1") }
    var ayahInput by remember { mutableStateOf("1") }
    val surah = (surahInput.toIntOrNull() ?: 1).coerceIn(1, 114)
    val count = repo.ayahCount(surah)
    val ayah = (ayahInput.toIntOrNull() ?: 1).coerceIn(1, count)
    val verse = repo.ayah(surah, ayah)
    var allowed by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
    var listening by remember { mutableStateOf(false) }
    var heard by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("اختر الآية ثم ابدأ التسميع المباشر.") }
    var lastAlertCount by remember { mutableIntStateOf(0) }
    var recognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    val tone = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        allowed = it
        status = if (it) "تم تفعيل الميكروفون. اضغط ابدأ." else "يلزم السماح بالميكروفون."
    }
    val comparison = remember(verse, heard) { if (heard.isBlank()) null else compareRecitation(verse, heard) }
    val errorIndices = comparison?.unmatchedReferenceIndices?.filter { it < (comparison.heardWords.size) }?.toSet().orEmpty()
    val displayWords = verse.trim().split(Regex("\\s+"))

    DisposableEffect(Unit) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            status = "خدمة التعرف الصوتي العربي غير متاحة على هذا الجهاز."
        }
        onDispose {
            try { recognizer?.destroy() } catch (_: Exception) {}
            tone.release()
        }
    }

    fun beginListening() {
        if (!allowed) { permission.launch(Manifest.permission.RECORD_AUDIO); return }
        val sr = recognizer ?: run {
            status = "خدمة التعرف الصوتي غير متاحة."
            return
        }
        heard = ""
        lastAlertCount = 0
        listening = true
        status = "أقرأ الآن… ستظهر الكلمات التي تحتاج مراجعة بالأحمر."
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }
        sr.setRecognitionListener(object : RecognitionListener {
            private fun update(bundle: Bundle?) {
                val value = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                if (value.isBlank()) return
                heard = value
                val result = compareRecitation(verse, value)
                val confirmedErrors = result.unmatchedReferenceIndices.count { it < result.heardWords.size }
                if (confirmedErrors > lastAlertCount) {
                    tone.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
                    lastAlertCount = confirmedErrors
                    status = "انتبه: هناك كلمة تحتاج مراجعة. التنبيه مبني على التعرّف الصوتي."
                } else {
                    status = "أتابع قراءتك مباشرة…"
                }
            }
            override fun onReadyForSpeech(params: Bundle?) { status = "الميكروفون جاهز. ابدأ القراءة." }
            override fun onBeginningOfSpeech() { status = "أسمع تلاوتك الآن…" }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { status = "أحلّل المقطع الصوتي…" }
            override fun onError(error: Int) {
                if (listening) status = "توقف التعرف مؤقتًا. اضغط متابعة لإكمال التسميع."
            }
            override fun onResults(results: Bundle?) { update(results) }
            override fun onPartialResults(partialResults: Bundle?) { update(partialResults) }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        try { sr.startListening(intent) } catch (_: Exception) {
            listening = false
            status = "تعذر بدء التعرف الصوتي. حاول مرة أخرى."
        }
    }

    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { try { recognizer?.cancel() } catch (_: Exception) {}; listening = false; onBack() }) { Text("رجوع") }
            Spacer(Modifier.weight(1f))
            Text("مُتقن  /  التسميع المباشر", color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
        Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = colors.primary)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("اقرأ… وتابع موضعك لحظة بلحظة", color = colors.onPrimary, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("تمييز الكلمات المختلف عليها وتنبيه صوتي أثناء القراءة", color = colors.onPrimary.copy(alpha = .82f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = colors.onPrimary.copy(alpha = .14f), shape = CircleShape) { Text("● مباشر", color = colors.onPrimary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 11.sp) }
                    Surface(color = colors.onPrimary.copy(alpha = .14f), shape = CircleShape) { Text("عربي", color = colors.onPrimary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 11.sp) }
                }
            }
        }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("موضع التسميع", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(surahInput, { surahInput = it.filter(Char::isDigit).take(3); ayahInput = "1"; heard = "" }, Modifier.weight(1f), label = { Text("رقم السورة") }, singleLine = true)
                    OutlinedTextField(ayahInput, { ayahInput = it.filter(Char::isDigit).take(4); heard = "" }, Modifier.weight(1f), label = { Text("رقم الآية") }, singleLine = true)
                }
                Text("سورة ${SURAH_NAMES[surah - 1]} • الآية ${toArabic(ayah)}", color = colors.primary, fontWeight = FontWeight.Bold)
                Text(buildAnnotatedString {
                    displayWords.forEachIndexed { index, word ->
                        if (index > 0) append(" ")
                        if (index in errorIndices) withStyle(SpanStyle(color = Color(0xFFD62828), fontWeight = FontWeight.ExtraBold)) { append(word) }
                        else withStyle(SpanStyle(color = colors.onSurface, fontWeight = FontWeight.Medium)) { append(word) }
                    }
                }, fontSize = 25.sp, lineHeight = 48.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { ayahInput = (ayah - 1).coerceAtLeast(1).toString(); heard = "" }, enabled = ayah > 1, modifier = Modifier.weight(1f)) { Text("السابقة") }
                    OutlinedButton(onClick = { ayahInput = (ayah + 1).coerceAtMost(count).toString(); heard = "" }, enabled = ayah < count, modifier = Modifier.weight(1f)) { Text("التالية") }
                }
            }
        }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(76.dp).background(if (listening) Color(0xFFFFE1E1) else colors.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                    Text(if (listening) "●" else "🎙", color = if (listening) Color(0xFFD62828) else colors.primary, fontSize = 31.sp)
                }
                Text(if (listening) "التسميع يعمل" else "جاهز للتسميع", fontSize = 19.sp, fontWeight = FontWeight.Black)
                Text(status, color = colors.onSurfaceVariant, fontSize = 12.sp)
                if (listening) {
                    Button(onClick = { try { recognizer?.stopListening() } catch (_: Exception) {}; listening = false; status = "تم إيقاف التسميع." }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = colors.error)) { Text("إيقاف التسميع") }
                } else {
                    Button(onClick = { beginListening() }, modifier = Modifier.fillMaxWidth()) { Text(if (heard.isBlank()) "ابدأ التسميع المباشر" else "متابعة التسميع") }
                }
                OutlinedButton(onClick = { heard = ""; lastAlertCount = 0; status = "تم مسح نتيجة الجلسة." }, modifier = Modifier.fillMaxWidth()) { Text("مسح النتيجة وإعادة المحاولة") }
            }
        }
        if (heard.isNotBlank()) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
                Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ما التقطه الميكروفون", fontWeight = FontWeight.Bold, color = colors.primary)
                    Text(heard, fontSize = 17.sp, lineHeight = 29.sp)
                    Text("الكلمات المعلّمة بالأحمر هي اختلافات محتملة في النص الملتقط.", color = Color(0xFFD62828), fontSize = 12.sp)
                }
            }
        }
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Text("تنبيه الدقة: يعتمد هذا المؤشر على خدمة تحويل الكلام إلى نص في الجهاز، وقد تخطئ في سماع ألفاظ القرآن أو الحركات. لذلك هو مساعد أولي لموضع الاختلاف، وليس حكمًا معتمدًا على صحة التلاوة أو المخارج أو الغنة أو المدود. قد يحتاج اتصالًا بالإنترنت حسب خدمة جهازك.", Modifier.padding(15.dp), fontSize = 11.sp, lineHeight = 19.sp, color = colors.onSurfaceVariant)
        }
    }
}
