package com.mutqin.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RangeScreen(
  repo: QuranRepository,
  surah: Int,
  onBack: () -> Unit,
  onStart: (Int, Int) -> Unit
) {
  val count = repo.ayahCount(surah)
  var fromText by remember { mutableStateOf("1") }
  var toText by remember { mutableStateOf(count.toString()) }
  val a = fromText.trim().toIntOrNull()
  val b = toText.trim().toIntOrNull()
  val valid = a != null && b != null && a >= 1 && b <= count && a <= b

  Column(
    modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    TextButton(onClick = onBack) { Text("رجوع") }
    Text(
      "سورة ${SURAH_NAMES[surah - 1]}",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )
    Text("عدد آيات السورة: ${toArabic(count)}")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      OutlinedTextField(
        value = fromText,
        onValueChange = { fromText = it },
        label = { Text("من الآية") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.weight(1f)
      )
      OutlinedTextField(
        value = toText,
        onValueChange = { toText = it },
        label = { Text("إلى الآية") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.weight(1f)
      )
    }
    OutlinedButton(
      onClick = {
        fromText = "1"
        toText = count.toString()
      },
      modifier = Modifier.fillMaxWidth()
    ) { Text("السورة كاملة") }
    if (!valid) {
      Text(
        "أدخل أرقامًا صحيحة بين ١ و ${toArabic(count)}، وتأكد أن بداية الآيات لا تتجاوز نهايتها",
        color = MaterialTheme.colorScheme.error,
        fontSize = 13.sp
      )
    }
    Button(
      onClick = { if (a != null && b != null && valid) onStart(a, b) },
      enabled = valid,
      modifier = Modifier.fillMaxWidth()
    ) { Text("ابدأ القراءة") }
  }
}

@Composable
fun ReaderScreen(
  repo: QuranRepository, prefs: AppPrefs, surah: Int, from: Int, to: Int, onBack: () -> Unit
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val colors = MaterialTheme.colorScheme
  var fontSize by remember { mutableStateOf(prefs.fontSize().coerceIn(20f, 42f)) }
  var reviewMode by remember { mutableStateOf(false) }
  var revealAll by remember { mutableStateOf(true) }
  var focusMode by remember { mutableStateOf(false) }
  var search by remember { mutableStateOf("") }
  var jumpText by remember { mutableStateOf("") }
  var showJump by remember { mutableStateOf(false) }
  var showSearch by remember { mutableStateOf(false) }
  var micListening by remember { mutableStateOf(false) }
  var micHeard by remember { mutableStateOf("") }
  var micStatus by remember { mutableStateOf("اضغط الميكروفون وابدأ القراءة") }
  var micErrors by remember { mutableIntStateOf(0) }
  var micAllowed by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
  var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
  val micTone = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75) }
  val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> micAllowed = granted; micStatus = if (granted) "تم السماح بالميكروفون، اضغط مرة أخرى للبدء" else "يلزم السماح بالميكروفون" }
  var pageStart by remember { mutableIntStateOf(from) }
  val pageSize = 10
  val pageEnd = minOf(to, pageStart + pageSize - 1)
  var bookmarked by remember { mutableStateOf(prefs.bookmark(surah)) }
  val visibleAyahs = (pageStart..pageEnd).filter { n ->
    search.isBlank() || normalizeAr(repo.ayah(surah, n)).contains(normalizeAr(search.trim()))
  }
  val pageText = visibleAyahs.joinToString("  ") { n ->
    val verse = if (reviewMode && !revealAll) "ــــــــــــ" else repo.ayah(surah, n)
    "$verse ۞${toArabic(n)}"
  }
  val recitationReference = visibleAyahs.joinToString(" ") { repo.ayah(surah, it) }
  val recitationComparison = remember(recitationReference, micHeard) { if (micHeard.isBlank()) null else compareRecitation(recitationReference, micHeard) }
  val recitationErrorIndices = recitationComparison?.suspectedErrorReferenceIndices?.toSet().orEmpty()
  val readerWords = remember(visibleAyahs, reviewMode, revealAll) { visibleAyahs.flatMap { n ->
    val verse = if (reviewMode && !revealAll) "ــــــــــــ" else repo.ayah(surah, n)
    verse.trim().split(Regex("\\s+"))
  } }
  DisposableEffect(Unit) {
    if (SpeechRecognizer.isRecognitionAvailable(context)) speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    onDispose { try { speechRecognizer?.destroy() } catch (_: Exception) {}; micTone.release() }
  }
  fun stopMushafMic() { micListening = false; try { speechRecognizer?.stopListening() } catch (_: Exception) {}; micStatus = "تم إيقاف التسميع داخل المصحف" }
  fun startMushafMic() {
    if (!micAllowed) { micPermission.launch(Manifest.permission.RECORD_AUDIO); return }
    val sr = speechRecognizer ?: run { micStatus = "خدمة التعرف الصوتي غير متاحة"; return }
    micHeard = ""; micErrors = 0; micListening = true; micStatus = "أقرأ الآن… أتابع الكلمات داخل المصحف"
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply { putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA"); putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName) }
    sr.setRecognitionListener(object : RecognitionListener {
      private fun accept(bundle: Bundle?) {
        val words = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
        if (words.isBlank()) return
        micHeard = words
        val errors = compareRecitation(recitationReference, words).suspectedErrorReferenceIndices.size
        if (errors > micErrors) { micTone.startTone(ToneGenerator.TONE_PROP_BEEP, 120); micErrors = errors; micStatus = "تنبيه: كلمة محتملة الخطأ مميزة بالأحمر" } else micStatus = "التسميع مباشر داخل المصحف"
      }
      override fun onReadyForSpeech(params: Bundle?) { micStatus = "الميكروفون جاهز، ابدأ التلاوة" }
      override fun onBeginningOfSpeech() { micStatus = "أستمع إلى تلاوتك…" }
      override fun onRmsChanged(rmsdB: Float) {}
      override fun onBufferReceived(buffer: ByteArray?) {}
      override fun onEndOfSpeech() { micStatus = "أحلّل ما التقطه الميكروفون…" }
      override fun onError(error: Int) { if (micListening) Handler(Looper.getMainLooper()).postDelayed({ if (micListening) try { sr.startListening(intent) } catch (_: Exception) { micListening = false; micStatus = "توقف الاستماع، اضغط للبدء مجددًا" } }, 400) }
      override fun onResults(results: Bundle?) { accept(results); if (micListening) Handler(Looper.getMainLooper()).postDelayed({ if (micListening) try { sr.startListening(intent) } catch (_: Exception) { micListening = false } }, 250) }
      override fun onPartialResults(partialResults: Bundle?) { accept(partialResults) }
      override fun onEvent(eventType: Int, params: Bundle?) {}
    })
    try { sr.startListening(intent) } catch (_: Exception) { micListening = false; micStatus = "تعذر تشغيل الميكروفون" }
  }
  LaunchedEffect(surah, pageStart, pageEnd) { prefs.setLastPosition(surah, pageStart, to); micHeard = ""; if (micListening) stopMushafMic() }

  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()) {
    if (!focusMode) {
      Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onBack) { Text("رجوع") }
        Column(Modifier.weight(1f)) {
          Text("مُتقن • المصحف", fontWeight = FontWeight.ExtraBold, color = colors.primary, fontSize = 18.sp)
          Text("سورة ${SURAH_NAMES[surah - 1]}  •  الآيات ${toArabic(pageStart)}–${toArabic(pageEnd)}", fontSize = 10.sp, color = colors.onSurfaceVariant)
        }
        TextButton(onClick = { prefs.setBookmark(surah, pageStart); bookmarked = pageStart }) { Text(if (bookmarked == pageStart) "★" else "☆", fontSize = 22.sp, color = colors.secondary) }
        TextButton(onClick = { showSearch = !showSearch }) { Text("⌕", fontSize = 23.sp) }
        TextButton(onClick = { showJump = true }) { Text("انتقال") }
        if (bookmarked != null) TextButton(onClick = { pageStart = bookmarked!!.coerceIn(from, to) }) { Text("علامتي") }
      }
      if (showSearch) {
        OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth().padding(horizontal = 12.dp), singleLine = true, label = { Text("ابحث داخل السورة") })
      }
      Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("المراجعة", Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Switch(checked = reviewMode, onCheckedChange = { reviewMode = it; revealAll = !it })
            Text("تركيز", fontSize = 11.sp)
            Switch(checked = focusMode, onCheckedChange = { focusMode = it })
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("حجم الخط", fontSize = 11.sp)
            TextButton(onClick = { fontSize = (fontSize - 2f).coerceAtLeast(20f); prefs.setFontSize(fontSize) }) { Text("أ−") }
            Text("${fontSize.toInt()}", fontSize = 11.sp)
            TextButton(onClick = { fontSize = (fontSize + 2f).coerceAtMost(42f); prefs.setFontSize(fontSize) }) { Text("أ+") }
            TextButton(onClick = { fontSize = 28f; prefs.setFontSize(fontSize) }) { Text("إعادة") }
          }
          if (reviewMode) {
            Row {
              TextButton(onClick = { revealAll = true }) { Text("إظهار الآيات") }
              TextButton(onClick = { revealAll = false }) { Text("إخفاء الآيات") }
            }
          }
        }
      }
    } else {
      Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = { focusMode = false }) { Text("إنهاء وضع التركيز") }
      }
    }

    if (showJump) {
      AlertDialog(
        onDismissRequest = { showJump = false },
        title = { Text("الانتقال إلى آية") },
        text = { OutlinedTextField(jumpText, { jumpText = it }, label = { Text("رقم الآية") }, singleLine = true) },
        confirmButton = {
          TextButton(onClick = {
            val n = jumpText.trim().toIntOrNull()
            if (n != null && n in from..to) pageStart = n
            showJump = false
          }) { Text("انتقال") }
        },
        dismissButton = { TextButton(onClick = { showJump = false }) { Text("إلغاء") } }
      )
    }

    Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
      .background(colors.surface, RoundedCornerShape(22.dp)).padding(horizontal = 17.dp, vertical = 18.dp)) {
      if (pageStart == from && from == 1) {
        Text("۞  بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ  ۞", Modifier.fillMaxWidth(), color = colors.primary, fontSize = (fontSize * .72f).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Text("سُورَةُ ${SURAH_NAMES[surah - 1]}", Modifier.fillMaxWidth(), color = colors.primary, fontSize = (fontSize * .78f).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
      }
      if (search.isNotBlank() && visibleAyahs.isEmpty()) {
        Text("لا توجد آيات مطابقة في هذا المدى.", color = colors.onSurfaceVariant)
      } else {
        Text(
          buildAnnotatedString { readerWords.forEachIndexed { index, word -> if (index > 0) append(" "); if (index in recitationErrorIndices) withStyle(SpanStyle(color = Color(0xFFD62828), fontWeight = FontWeight.ExtraBold)) { append(word) } else withStyle(SpanStyle(color = colors.onSurface, fontWeight = FontWeight.Medium)) { append(word) } } },
          Modifier.fillMaxWidth().weight(1f),
          fontSize = fontSize.sp,
          lineHeight = (fontSize * 2.05f).sp,
          textAlign = TextAlign.Justify
        )
      }
      Spacer(Modifier.height(10.dp))
      Text("مُتقن  •  ${SURAH_NAMES[surah - 1]}  •  ${toArabic(pageStart)}–${toArabic(pageEnd)}", Modifier.fillMaxWidth(), color = colors.secondary, fontSize = 10.sp, textAlign = TextAlign.Center)
    }

    if (micHeard.isNotBlank() || micListening) Text(micStatus, Modifier.fillMaxWidth().padding(horizontal = 16.dp), color = if (micListening) colors.primary else colors.onSurfaceVariant, fontSize = 11.sp, textAlign = TextAlign.Center)
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      FilledTonalButton(onClick = { if (micListening) stopMushafMic() else startMushafMic() }, modifier = Modifier.size(58.dp), shape = RoundedCornerShape(19.dp), colors = ButtonDefaults.filledTonalButtonColors(containerColor = if (micListening) colors.errorContainer else colors.primaryContainer)) { Text(if (micListening) "■" else "🎙", fontSize = 22.sp, color = if (micListening) colors.error else colors.primary) }
      OutlinedButton(onClick = { pageStart = maxOf(from, pageStart - pageSize) }, enabled = pageStart > from, modifier = Modifier.weight(1f)) { Text("السابق") }
      OutlinedButton(onClick = {
        val clip = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clip.setPrimaryClip(android.content.ClipData.newPlainText("مُتقن", pageText))
      }, modifier = Modifier.weight(1f)) { Text("نسخ الصفحة") }
      OutlinedButton(onClick = {
        val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(android.content.Intent.EXTRA_TEXT, pageText) }
        context.startActivity(android.content.Intent.createChooser(send, "مشاركة آيات من القرآن"))
      }, modifier = Modifier.weight(1f)) { Text("مشاركة") }
      Button(onClick = { pageStart = minOf(to, pageStart + pageSize) }, enabled = pageEnd < to, modifier = Modifier.weight(1f)) { Text("التالي") }
    }
    bookmarked?.let { mark ->
      Text("علامتك المرجعية: الآية ${toArabic(mark)}", Modifier.fillMaxWidth().padding(bottom = 5.dp), color = colors.secondary, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
  }
}
