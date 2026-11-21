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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

  val c = MaterialTheme.colorScheme
  Column(
    modifier = Modifier.fillMaxSize().background(c.background).safeDrawingPadding().padding(horizontal = 18.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
      Modifier.fillMaxWidth().background(c.surface, RoundedCornerShape(18.dp))
        .border(1.dp, c.outline.copy(alpha = .22f), RoundedCornerShape(18.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(onClick = onBack, shape = RoundedCornerShape(12.dp), color = c.primary.copy(alpha = .09f)) {
        Text("‹", modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp), color = c.primary, fontSize = 23.sp, fontWeight = FontWeight.Bold)
      }
      Spacer(Modifier.weight(1f))
      Column(horizontalAlignment = Alignment.End) {
        Text("مُتْقِن  •  المصحف", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = c.secondary)
        Text("إعداد القراءة", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = c.onSurface)
      }
    }
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = c.primary),
      border = androidx.compose.foundation.BorderStroke(1.dp, c.secondary.copy(alpha=.65f))
    ) {
      Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
          Text("۞", fontSize = 19.sp, color = c.secondary)
          Text("وَرْدُكَ الْقُرْآنِيّ", fontSize = 11.sp, color = c.secondary, fontWeight = FontWeight.Bold)
          Spacer(Modifier.weight(1f))
          Surface(shape = RoundedCornerShape(30.dp), color = c.onPrimary.copy(alpha = .12f)) {
            Text("سورة ${toArabic(surah)}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), fontSize = 10.sp, color = c.onPrimary)
          }
        }
        Text(SURAH_NAMES[surah - 1], fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = c.onPrimary)
        Text("${toArabic(count)} آية  •  اختر موضع البداية والنهاية", fontSize = 12.sp, color = c.onPrimary.copy(alpha=.82f))
        Divider(color = c.secondary.copy(alpha=.55f), thickness = 1.dp)
        Text("اجعل قراءتك على مهل، واختر المقطع المناسب لجلسة اليوم", fontSize = 12.sp, color = c.onPrimary.copy(alpha=.92f))
      }
    }
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = c.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, c.outline.copy(alpha = .25f))
    ) {
      Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("حدّد مقطعك", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = c.onSurface)
            Text("من آية إلى آية", fontSize = 11.sp, color = c.secondary)
          }
          Surface(shape = RoundedCornerShape(12.dp), color = c.secondary.copy(alpha = .13f)) {
            Text("﴿ ﴾", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = c.secondary, fontSize = 17.sp)
          }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = fromText, onValueChange = { fromText = it.filter(Char::isDigit).take(4) },
            label = { Text("من الآية") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = toText, onValueChange = { toText = it.filter(Char::isDigit).take(4) },
            label = { Text("إلى الآية") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(onClick = { fromText = "1"; toText = count.toString() }, modifier = Modifier.weight(1f)) { Text("السورة كاملة") }
          OutlinedButton(onClick = { fromText = "1"; toText = count.coerceAtMost(10).toString() }, modifier = Modifier.weight(1f)) { Text("أول 10 آيات") }
        }
        if (!valid) {
          Text("تأكد من إدخال أرقام بين ١ و ${toArabic(count)}، وأن البداية لا تتجاوز النهاية.", color = c.error, fontSize = 12.sp)
        } else {
          Surface(shape = RoundedCornerShape(13.dp), color = c.primary.copy(alpha=.08f)) {
            Text("تم تحديد ${toArabic(b!! - a!! + 1)} آية للقراءة", Modifier.fillMaxWidth().padding(12.dp), fontSize = 12.sp, color = c.primary, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
    Spacer(Modifier.weight(1f))
    Button(
      onClick = { if (a != null && b != null && valid) onStart(a, b) },
      enabled = valid, modifier = Modifier.fillMaxWidth().height(58.dp),
      shape = RoundedCornerShape(18.dp),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
    ) {
      Text("ابدأ القراءة", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
      Spacer(Modifier.width(9.dp))
      Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
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
  var selectedAyah by remember { mutableIntStateOf(0) }
  var bookmarkVersion by remember { mutableIntStateOf(0) }
  val bookmarks = remember(bookmarkVersion) { prefs.bookmarks() }
  val listState = rememberLazyListState()
  val mushafPages = remember(surah, from, to, size, hide) {
    val groups = mutableListOf<List<Int>>()
    var current = mutableListOf<Int>()
    var chars = 0
    // Dynamic page grouping tuned to the reader font size, with a conservative
    // estimate to avoid overflowing the reading surface.
    val pageCapacity = (520f * (30f / size.coerceAtLeast(20f))).toInt().coerceIn(260, 720)
    for (n in from..to) {
      val length = repo.ayah(surah, n).length
      if (current.isNotEmpty() && chars + length > pageCapacity) {
        groups.add(current.toList())
        current = mutableListOf()
        chars = 0
      }
      current.add(n)
      chars += length
    }
    if (current.isNotEmpty()) groups.add(current.toList())
    groups
  }
  var pageIndex by remember(surah, from, to) { mutableIntStateOf(0) }
  LaunchedEffect(pageIndex, mushafPages) {
    val currentPage = mushafPages.getOrNull(pageIndex)
    if (!currentPage.isNullOrEmpty()) {
      prefs.setLastPosition(surah, currentPage.first(), currentPage.last())
    }
  }
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
  // Align the recognized phrase to the best matching position within the selected passage.
  val alignment = remember(spokenWords, targetWords) {
    var bestStart = 0
    var bestCount = 0
    if (spokenWords.isNotEmpty()) {
      targetWords.indices.forEach { start ->
        var count = 0
        while (count < spokenWords.size && start + count < targetWords.size &&
          spokenWords[count] == targetWords[start + count]) count++
        if (count > bestCount) { bestStart = start; bestCount = count }
      }
    }
    bestStart to bestCount
  }
  val matchStart = alignment.first
  val matchedWords = alignment.second
  val expectedWord = targetWords.getOrNull(matchStart + matchedWords)
  val hasMismatch = spokenWords.isNotEmpty() && matchedWords < spokenWords.size

  Column(
    modifier = Modifier.fillMaxSize().safeDrawingPadding().background(MaterialTheme.colorScheme.background)
  ) {
    Column(Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Color(0xFF062A20)).padding(horizontal=20.dp,vertical=22.dp)) {
      Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)) {
        Surface(onClick=onBack,shape=RoundedCornerShape(16.dp),color=MaterialTheme.colorScheme.onPrimary.copy(alpha=.12f)) {
          Text("‹",Modifier.padding(horizontal=14.dp,vertical=6.dp),fontSize=23.sp,color=MaterialTheme.colorScheme.onPrimary)
        }
        Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) {
          Text("مُتْقِن  /  الْمُصْحَف",fontSize=9.sp,color=MaterialTheme.colorScheme.secondary,fontWeight=FontWeight.Bold,letterSpacing=1.sp)
          Text(SURAH_NAMES[surah-1],fontSize=22.sp,color=MaterialTheme.colorScheme.onPrimary,fontWeight=FontWeight.ExtraBold)
        }
        Surface(shape=RoundedCornerShape(13.dp),color=MaterialTheme.colorScheme.onPrimary.copy(alpha=.12f),onClick={
          if(isListening){recognizer?.cancel();isListening=false;speechError="تم إيقاف التسميع."}
          else if(ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) beginRecognition()
          else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }) { Text(if(isListening) "■" else "🎙",Modifier.padding(horizontal=13.dp,vertical=10.dp),fontSize=16.sp,color=if(isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary) }
        Surface(shape=RoundedCornerShape(13.dp),color=MaterialTheme.colorScheme.onPrimary.copy(alpha=.12f),onClick={
          val current=mushafPages.getOrNull(pageIndex)
          if(!current.isNullOrEmpty()){prefs.toggleBookmark(surah,current.first());bookmarkVersion++;android.widget.Toast.makeText(context,"تم حفظ موضع الصفحة",android.widget.Toast.LENGTH_SHORT).show()}
        }) { Text("⚑",Modifier.padding(horizontal=13.dp,vertical=9.dp),fontSize=18.sp,color=MaterialTheme.colorScheme.secondary) }
      }
      Spacer(Modifier.height(9.dp))
      Text("تِلَاوَةٌ • تَدَبُّرٌ • مُرَاجَعَة",Modifier.align(Alignment.CenterHorizontally),fontSize=9.sp,color=MaterialTheme.colorScheme.onPrimary.copy(alpha=.7f),letterSpacing=1.sp)
    }

    // Reading modes are explicit and remain accessible without opening a separate screen.
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Surface(
        modifier = Modifier.weight(1f).clickable { showTools = !showTools },
        shape = RoundedCornerShape(16.dp),
        color = if (showTools) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.45f))
      ) { Text("Aa  إعدادات القراءة", modifier = Modifier.padding(vertical = 12.dp), textAlign = TextAlign.Center, color = if (showTools) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
      Surface(
        modifier = Modifier.weight(1f).clickable { showTools = true; hide = !hide; revealed.clear() },
        shape = RoundedCornerShape(14.dp),
        color = if (hide) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
      ) { Text(if (hide) "إظهار الحفظ" else "اختبار الحفظ", modifier = Modifier.padding(vertical = 11.dp), textAlign = TextAlign.Center, color = if (hide) androidx.compose.ui.graphics.Color(0xFF17251D) else androidx.compose.ui.graphics.Color(0xFFF4E8C9), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
      Surface(
        modifier = Modifier.weight(1f).clickable { showBookmarks = true },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha=.55f))
      ) { Text("☆  المحفوظات", modifier = Modifier.padding(vertical = 12.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
    }

    if (showTools) {
      Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF5EEDC)),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFB8954B))
      ) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
              Text("وضع المراجعة", color = androidx.compose.ui.graphics.Color(0xFF173A2E), fontWeight = FontWeight.Bold)
              Text("أخفِ نص الآيات ثم اكشفها عند الحاجة", color = androidx.compose.ui.graphics.Color(0xFF75684F), fontSize = 10.sp)
            }
            Switch(checked = hide, onCheckedChange = { hide = it; revealed.clear() })
          }
          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("حجم الخط", color = androidx.compose.ui.graphics.Color(0xFF173A2E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
              OutlinedButton(onClick = { size = (size - 2f).coerceAtLeast(20f); prefs.setFontSize(size) }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) { Text("−") }
              Text("${size.toInt()}", color = androidx.compose.ui.graphics.Color(0xFF173A2E), modifier = Modifier.align(Alignment.CenterVertically))
              OutlinedButton(onClick = { size = (size + 2f).coerceAtMost(44f); prefs.setFontSize(size) }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) { Text("+") }
            }
          }
          if (hide) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              TextButton(onClick = { revealed.clear(); revealed.addAll(from..to) }) { Text("كشف الكل") }
              TextButton(onClick = { revealed.clear() }) { Text("إخفاء الكل") }
            }
          }
          if (transcript.isNotBlank()) {
            Text("النص الذي التقطه الجهاز: $transcript", color = androidx.compose.ui.graphics.Color(0xFF243B32), fontSize = 12.sp)
            Text("تطابق نصي تقريبي: ${toArabic(matchedWords)} / ${toArabic(targetWords.size)}", color = androidx.compose.ui.graphics.Color(0xFF17664E), fontSize = 11.sp)
            LinearProgressIndicator(progress = { if (targetWords.isEmpty()) 0f else (matchedWords.toFloat() / targetWords.size).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth(), color = androidx.compose.ui.graphics.Color(0xFF17664E), trackColor = androidx.compose.ui.graphics.Color(0xFFD7E5DC))
            if (hasMismatch) {
              Text("موضع اختلاف محتمل — راجع التلاوة يدويًا", color = androidx.compose.ui.graphics.Color(0xFFB3261E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                  Text("سمعه الجهاز", color = androidx.compose.ui.graphics.Color(0xFF75684F), fontSize = 10.sp)
                  Text(spokenWords.getOrNull(matchedWords).orEmpty(), color = androidx.compose.ui.graphics.Color(0xFFB3261E), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                  Text("الكلمة في المصحف", color = androidx.compose.ui.graphics.Color(0xFF75684F), fontSize = 10.sp)
                  Text(expectedWord.orEmpty(), color = androidx.compose.ui.graphics.Color(0xFF17664E), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
              }
            } else if (matchedWords == targetWords.size && targetWords.isNotEmpty()) {
              Text("اكتملت المطابقة النصية للمقطع.", color = androidx.compose.ui.graphics.Color(0xFF17664E), fontSize = 12.sp)
            }
          }
          if (speechError != null) Text(speechError!!, color = androidx.compose.ui.graphics.Color(0xFFB3261E), fontSize = 11.sp)
          Text("تنبيه: هذه مطابقة صوت إلى نص بشكل أولي، وليست حكمًا على صحة التجويد أو المخارج.", color = androidx.compose.ui.graphics.Color(0xFF75684F), fontSize = 10.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { transcript = ""; speechError = null }, modifier = Modifier.weight(1f)) { Text("مسح النتيجة") }
            Button(onClick = {
              transcript = ""; speechError = null
              if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) beginRecognition()
              else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF174B3B))) { Text("بدء التسميع") }
          }
        }
      }
    }

    Surface(
      modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 7.dp, vertical = 3.dp)
        .pointerInput(pageIndex, mushafPages.size) {
          var drag = 0f
          detectHorizontalDragGestures(
            onHorizontalDrag = { _, amount -> drag += amount },
            onDragEnd = {
              if (drag < -55f) pageIndex = (pageIndex + 1).coerceAtMost(mushafPages.lastIndex)
              else if (drag > 55f) pageIndex = (pageIndex - 1).coerceAtLeast(0)
              drag = 0f
            }
          )
        },
      color = androidx.compose.ui.graphics.Color(0xFFFFFCF4),
      shape = RoundedCornerShape(4.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, androidx.compose.ui.graphics.Color(0xFFB8954B)),
      shadowElevation = 2.dp
    ) {
      Column(Modifier.fillMaxSize().padding(5.dp).background(androidx.compose.ui.graphics.Color(0xFFFFFCF4)).padding(4.dp)) {
        // Printed-page treatment: square paper edges and a fine double-rule frame.
        Column(Modifier.fillMaxSize().border(1.dp, androidx.compose.ui.graphics.Color(0xFFB8954B), RoundedCornerShape(2.dp)).padding(2.dp).border(0.7.dp, androidx.compose.ui.graphics.Color(0xFFD6BD83), RoundedCornerShape(1.dp)).padding(horizontal = 13.dp, vertical = 10.dp)) {
          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("الْجُزْءُ", color = androidx.compose.ui.graphics.Color(0xFF98783B), fontSize = 9.sp)
            Spacer(Modifier.weight(1f))
            Text("الْقُرْآنُ الْكَرِيمُ", color = androidx.compose.ui.graphics.Color(0xFF174B3B), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.weight(1f))
            Text("حِفْظٌ وَتِلَاوَة", color = androidx.compose.ui.graphics.Color(0xFF98783B), fontSize = 9.sp)
          }
          androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().padding(top = 7.dp).height(2.dp)) {
            drawLine(color = androidx.compose.ui.graphics.Color(0xFFB8954B), start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(this.size.width, 0f), strokeWidth = 1.dp.toPx())
          }
          Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("سورة ${SURAH_NAMES[surah - 1]}", color = androidx.compose.ui.graphics.Color(0xFF174B3B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("صفحة ${toArabic(pageIndex + 1)} من ${toArabic(mushafPages.size)}", color = androidx.compose.ui.graphics.Color(0xFF92733B), fontSize = 10.sp)
          }
          androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(color = androidx.compose.ui.graphics.Color(0xFFD6BD83), start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(this.size.width, 0f), strokeWidth = 1.dp.toPx())
          }
          Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 7.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("۞", color = androidx.compose.ui.graphics.Color(0xFFB28A3B), fontSize = 26.sp)
            Text("سُورَةُ ${SURAH_NAMES[surah - 1]}", color = androidx.compose.ui.graphics.Color(0xFF174B3B), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (from == 1 && surah != 1 && surah != 9) {
              Text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", modifier = Modifier.padding(top = 10.dp, bottom = 8.dp), color = androidx.compose.ui.graphics.Color(0xFF30291F), fontSize = (size * 0.92f).sp, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(8.dp))
            mushafPages.getOrNull(pageIndex).orEmpty().forEach { n ->
              val verse = repo.ayah(surah, n)
              val shown = !hide || n in revealed
              val body = if (shown) verse else verse.substringBefore(' ') + " …"
              Text(
                body + " ۞" + toArabic(n) + "۞",
                modifier = Modifier.fillMaxWidth().clickable {
                  if (hide) { if (n in revealed) revealed.remove(n) else revealed.add(n) }
                  else selectedAyah = n
                }.padding(vertical = 1.dp),
                fontSize = size.sp,
                lineHeight = (size * 1.95f).sp,
                textAlign = TextAlign.Justify,
                color = androidx.compose.ui.graphics.Color(0xFF29271F)
              )
            }
            Spacer(Modifier.height(15.dp))
            Text("۞  ❁  ۞", color = androidx.compose.ui.graphics.Color(0xFFB28A3B), fontSize = 17.sp)
          }
          androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(color = androidx.compose.ui.graphics.Color(0xFFD6BD83), start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(this.size.width, 0f), strokeWidth = 1.dp.toPx())
          }
          Row(Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("مُتْقِن", color = androidx.compose.ui.graphics.Color(0xFF174B3B), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("تصفّح صفحات المصحف", color = androidx.compose.ui.graphics.Color(0xFF98783B), fontSize = 9.sp)
            Text(toArabic(pageIndex + 1), color = androidx.compose.ui.graphics.Color(0xFF174B3B), fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }
        }
      }
    }

    // Fixed bottom page navigator with progress and direct previous/next actions.
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 14.dp, vertical = 8.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = { pageIndex = (pageIndex - 1).coerceAtLeast(0) }, enabled = pageIndex > 0) { Text("السابق", color = MaterialTheme.colorScheme.secondary) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("${toArabic(pageIndex + 1)} / ${toArabic(mushafPages.size)}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Text("${toArabic(((pageIndex + 1f) / mushafPages.size.coerceAtLeast(1) * 100).toInt())}% من المقطع", color = MaterialTheme.colorScheme.secondary, fontSize = 9.sp)
        }
        TextButton(onClick = { pageIndex = (pageIndex + 1).coerceAtMost(mushafPages.lastIndex) }, enabled = pageIndex < mushafPages.lastIndex) { Text("التالي", color = MaterialTheme.colorScheme.secondary) }
      }
      Slider(value = pageIndex.toFloat(), onValueChange = { pageIndex = it.toInt().coerceIn(0, mushafPages.lastIndex) }, valueRange = 0f..mushafPages.lastIndex.coerceAtLeast(1).toFloat(), steps = (mushafPages.size - 2).coerceAtLeast(0), modifier = Modifier.fillMaxWidth())
    }
  }
  if (selectedAyah > 0) {
    val selectedText = repo.ayah(surah, selectedAyah)
    var focusedWord by remember(selectedAyah) { mutableStateOf<String?>(null) }
    AlertDialog(
      onDismissRequest = { selectedAyah = 0 },
      title = { Text("سورة ${SURAH_NAMES[surah - 1]} — الآية ${toArabic(selectedAyah)}") },
      text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(selectedText, fontSize = 20.sp, lineHeight = 34.sp, textAlign = TextAlign.Center); Text("اختر كلمة للتأمل فيها أو نسخها", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary); FlowRowCompat(selectedText.split(Regex("\\s+")), focusedWord) { focusedWord = it }; if (focusedWord != null) { Text("الكلمة المحددة: $focusedWord", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold); TextButton(onClick = { val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager; clipboard.setPrimaryClip(android.content.ClipData.newPlainText("كلمة قرآنية", focusedWord)); android.widget.Toast.makeText(context, "تم نسخ الكلمة", android.widget.Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) { Text("نسخ الكلمة المحددة") } } } },
      confirmButton = {
        TextButton(onClick = {
          val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
          clipboard.setPrimaryClip(android.content.ClipData.newPlainText("آية من القرآن", selectedText))
          selectedAyah = 0
          android.widget.Toast.makeText(context, "تم نسخ الآية", android.widget.Toast.LENGTH_SHORT).show()
        }) { Text("نسخ الآية") }
      },
      dismissButton = {
        Row {
          TextButton(onClick = {
            val share = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, "$selectedText\n(سورة ${SURAH_NAMES[surah - 1]}، الآية ${toArabic(selectedAyah)})")
            }
            context.startActivity(Intent.createChooser(share, "مشاركة الآية"))
            selectedAyah = 0
          }) { Text("مشاركة") }
          TextButton(onClick = {
            prefs.toggleBookmark(surah, selectedAyah)
            bookmarkVersion++
            selectedAyah = 0
          }) { Text("حفظ") }
          TextButton(onClick = { selectedAyah = 0 }) { Text("إغلاق") }
        }
      }
    )
  }
  if (showBookmarks) {
    AlertDialog(
      onDismissRequest = { showBookmarks = false },
      title = { Text("الآيات المحفوظة") },
      text = {
        if (bookmarks.isEmpty()) {
          Text("لم تحفظ أي آية بعد. اضغط على الآية ثم اختر «حفظ» لإضافتها هنا.")
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

@Composable
private fun FlowRowCompat(words: List<String>, selected: String?, onSelect: (String) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    words.chunked(4).forEach { chunk ->
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        chunk.forEach { word ->
          TextButton(onClick = { onSelect(word) }) {
            Text(word, color = if (word == selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary, fontSize = 15.sp)
          }
        }
      }
    }
  }
}
