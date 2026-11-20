package com.mutqin.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
  repo: QuranRepository,
  prefs: AppPrefs,
  surah: Int,
  from: Int,
  to: Int,
  onBack: () -> Unit,
  onOpenBookmark: (Int, Int) -> Unit
) {
  var hide by remember { mutableStateOf(false) }
  var size by remember { mutableStateOf(prefs.fontSize()) }
  val revealed = remember { mutableStateListOf<Int>() }
  var showBookmarks by remember { mutableStateOf(false) }
  var showTools by remember { mutableStateOf(false) }
  var bookmarkVersion by remember { mutableIntStateOf(0) }
  val bookmarks = remember(bookmarkVersion) { prefs.bookmarks() }
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()
  val context = androidx.compose.ui.platform.LocalContext.current
  var transcript by remember { mutableStateOf("") }
  var speechError by remember { mutableStateOf<String?>(null) }
  var isListening by remember { mutableStateOf(false) }
  val recognizer = remember(context) {
    if (SpeechRecognizer.isRecognitionAvailable(context)) SpeechRecognizer.createSpeechRecognizer(context) else null
  }
  DisposableEffect(recognizer) {
    if (recognizer != null) {
      recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { speechError = null }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
          isListening = false
          speechError = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "لم أتعرف على الكلمات بوضوح؛ حاول القراءة بهدوء."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "لم أسمع قراءة؛ اضغط وابدأ من جديد."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "يلزم السماح باستخدام الميكروفون."
            else -> "تعذر إكمال التعرف الصوتي. تحقق من اتصال الإنترنت وإعدادات خدمة الصوت."
          }
        }
        override fun onResults(results: Bundle?) {
          transcript = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
          isListening = false
        }
        override fun onPartialResults(partialResults: Bundle?) {
          val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
          if (!partial.isNullOrBlank()) transcript = partial
        }
        override fun onEvent(eventType: Int, params: Bundle?) {}
      })
    }
    onDispose { recognizer?.destroy() }
  }
  fun beginRecognition() {
    if (recognizer == null) {
      speechError = "التعرف الصوتي غير متاح على هذا الجهاز."
      return
    }
    try {
      transcript = ""
      speechError = null
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-SA")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
      }
      recognizer.startListening(intent)
      isListening = true
    } catch (e: Exception) {
      isListening = false
      speechError = "تعذر تشغيل الميكروفون؛ تحقق من أذونات الجهاز."
    }
  }
  val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    if (granted) beginRecognition() else speechError = "لم يتم منح إذن الميكروفون."
  }
  fun normalizeSpeech(value: String): String = value
    .replace(Regex("[\\u064B-\\u065F\\u0670\\u0640\\u06D6-\\u06ED]"), "")
    .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
    .replace("ى", "ي").replace("ؤ", "و").replace("ئ", "ي")
    .replace(Regex("[^\\p{L}\\p{N} ]"), "")
    .trim()
  val targetWords = remember(surah, from, to) {
    (from..to).flatMap { repo.ayah(surah, it).split(Regex("\\s+")) }
      .map { normalizeSpeech(it) }.filter { it.isNotBlank() }
  }
  val spokenWords = remember(transcript) { normalizeSpeech(transcript).split(Regex("\\s+")).filter { it.isNotBlank() } }
  val matchedWords = spokenWords.zip(targetWords).takeWhile { (spoken, target) -> spoken == target }.size
  val hasMismatch = spokenWords.size > matchedWords && matchedWords < targetWords.size

  Column(
    modifier = Modifier.fillMaxSize().safeDrawingPadding().background(androidx.compose.ui.graphics.Color(0xFFECE3CF))
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Color(0xFF123D32)).padding(horizontal = 8.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextButton(onClick = onBack) { Text("رجوع", color = androidx.compose.ui.graphics.Color.White) }
      Text(
        "سورة \${SURAH_NAMES[surah - 1]}",
        modifier = Modifier.weight(1f),
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = androidx.compose.ui.graphics.Color(0xFFFFE6A3)
      )
      TextButton(onClick = {
        if (isListening) {
          recognizer?.stopListening()
          isListening = false
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
          beginRecognition()
        } else {
          permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
      }) { Text(if (isListening) "إيقاف" else "🎙", color = androidx.compose.ui.graphics.Color.White) }
      TextButton(onClick = { showTools = !showTools }) {
        Text(if (showTools) "إخفاء" else "Aa", color = androidx.compose.ui.graphics.Color.White)
      }
    }
    if (showTools) {
      Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF8F1E2)),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("إخفاء الآيات لاختبار الحفظ", color = androidx.compose.ui.graphics.Color(0xFF243B32))
            Switch(checked = hide, onCheckedChange = { hide = it; revealed.clear() })
          }
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { showBookmarks = true }) { Text("المحفوظات (\${toArabic(bookmarks.size)})") }
            Text("حجم الخط", color = androidx.compose.ui.graphics.Color(0xFF243B32), fontSize = 12.sp)
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { size = (size + 2f).coerceAtMost(44f); prefs.setFontSize(size) }) { Text("تكبير +") }
            TextButton(onClick = { size = (size - 2f).coerceAtLeast(20f); prefs.setFontSize(size) }) { Text("تصغير −") }
            if (hide) {
              TextButton(onClick = { revealed.clear(); revealed.addAll(from..to) }) { Text("إظهار الكل") }
              TextButton(onClick = { revealed.clear() }) { Text("إخفاء الكل") }
            }
          }
          if (transcript.isNotBlank()) {
            Text("ما التقطه الجهاز: $transcript", color = androidx.compose.ui.graphics.Color(0xFF243B32), fontSize = 13.sp)
            Text("التطابق النصي: \${toArabic(matchedWords)} من \${toArabic(targetWords.size)} كلمة", fontSize = 12.sp, color = androidx.compose.ui.graphics.Color(0xFF17664E))
            if (hasMismatch) Text("راجع الكلمة التالية: \${targetWords.getOrNull(matchedWords).orEmpty()}", color = androidx.compose.ui.graphics.Color(0xFFB3261E), fontSize = 13.sp)
            else if (matchedWords == targetWords.size && targetWords.isNotEmpty()) Text("اكتملت المطابقة النصية للمقطع.", color = androidx.compose.ui.graphics.Color(0xFF17664E), fontSize = 13.sp)
          }
          if (speechError != null) Text(speechError!!, color = androidx.compose.ui.graphics.Color(0xFFB3261E), fontSize = 12.sp)
          Text("التسميع هنا تحويل صوت إلى نص ومطابقة أولية فقط، ولا يصحح التجويد أو المخارج.", color = androidx.compose.ui.graphics.Color(0xFF6C6253), fontSize = 10.sp)
        }
      }
    }
    Surface(
      modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 7.dp),
      color = androidx.compose.ui.graphics.Color(0xFFFAF5E8),
      shape = RoundedCornerShape(8.dp),
      shadowElevation = 2.dp
    ) {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        state = listState
      ) {
        item {
          Column(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("۞", color = androidx.compose.ui.graphics.Color(0xFFB28A3B), fontSize = 20.sp)
            Text("سُورَةُ \${SURAH_NAMES[surah - 1]}", color = androidx.compose.ui.graphics.Color(0xFF174B3B), fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("ــــــــــــــــــــــــــــــــــــــــــــــــــــــــ", color = androidx.compose.ui.graphics.Color(0xFFB28A3B), fontSize = 12.sp)
          }
        }
        items((from..to).toList()) { n ->
          val verse = repo.ayah(surah, n)
          val shown = !hide || n in revealed
          val body = if (shown) verse else verse.substringBefore(' ') + " …"
          Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Text(
              body + " ۞ " + toArabic(n),
              fontSize = size.sp,
              lineHeight = (size * 2.0f).sp,
              textAlign = TextAlign.Justify,
              color = androidx.compose.ui.graphics.Color(0xFF30291F),
              modifier = Modifier
                .weight(1f)
                .clickable(enabled = hide) {
                  if (n in revealed) revealed.remove(n) else revealed.add(n)
                }
                .padding(vertical = 5.dp)
            )
            TextButton(
              onClick = {
                prefs.toggleBookmark(surah, n)
                bookmarkVersion++
              },
              contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
              Text(
                if (prefs.isBookmarked(surah, n)) "★" else "☆",
                fontSize = 17.sp,
                color = androidx.compose.ui.graphics.Color(0xFFB28A3B)
              )
            }
          }
        }
      }
    }
  }
  if (showBookmarks) {
    AlertDialog(
      onDismissRequest = { showBookmarks = false },
      title = { Text("الآيات المحفوظة") },
      text = {
        if (bookmarks.isEmpty()) {
          Text("لم تحفظ أي آية بعد. اضغط ☆ بجانب الآية لإضافتها هنا.")
        } else {
          LazyColumn(modifier = Modifier.heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(bookmarks) { item ->
              val (savedSurah, savedAyah) = item
              TextButton(
                onClick = {
                  showBookmarks = false
                  onOpenBookmark(savedSurah, savedAyah)
                },
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("سورة ${SURAH_NAMES[savedSurah - 1]} — الآية ${toArabic(savedAyah)}", textAlign = TextAlign.Start)
              }
            }
          }
        }
      },
      confirmButton = { TextButton(onClick = { showBookmarks = false }) { Text("إغلاق") } }
    )
  }
}
