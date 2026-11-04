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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private fun mutqinWords(s: String): List<String> = s
  .replace(Regex("[\\u064B-\\u065F\\u0670]"), "")
  .replace("ـ", "").replace(Regex("[،؛؟.!]"), " ")
  .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
  .replace('ى', 'ي').replace('ة', 'ه')
  .trim().split(Regex("\\s+")).filter { it.isNotBlank() }

@Composable
fun RecitationCheckScreen(repo: QuranRepository, onBack: () -> Unit) {
  val context = LocalContext.current
  var surahInput by remember { mutableStateOf("1") }
  var fromInput by remember { mutableStateOf("1") }
  var toInput by remember { mutableStateOf("7") }
  var transcript by remember { mutableStateOf("") }
  var status by remember { mutableStateOf("اختر السورة والآيات، ثم ابدأ القراءة بصوت واضح.") }
  var listening by remember { mutableStateOf(false) }
  var recognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
  val surah = surahInput.toIntOrNull()?.coerceIn(1, 114) ?: 1
  val count = repo.ayahCount(surah)
  val from = (fromInput.toIntOrNull() ?: 1).coerceIn(1, count)
  val to = (toInput.toIntOrNull() ?: from).coerceIn(from, count)
  val expected = (from..to).joinToString(" ") { repo.ayah(surah, it) }
  val expectedWords = remember(expected) { mutqinWords(expected) }
  val heardWords = remember(transcript) { mutqinWords(transcript) }
  val matching = expectedWords.indices.count { i -> i < heardWords.size && expectedWords[i] == heardWords[i] }
  val reviewCount = maxOf(expectedWords.size, heardWords.size) - matching
  val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    status = if (granted) "تم السماح بالميكروفون. اضغط بدء التسميع." else "لم يتم السماح بالميكروفون."
  }
  DisposableEffect(Unit) { onDispose { recognizer?.destroy(); recognizer = null } }

  Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(18.dp),
    verticalArrangement = Arrangement.spacedBy(13.dp)) {
    TextButton(onClick = onBack) { Text("رجوع") }
    Text("اختبار التسميع", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
    Text("اقرأ الآيات المحددة، وسيحوّل الهاتف صوتك إلى كلمات ثم يقارنها بالنص القرآني.")
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
      Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = surahInput, onValueChange = { surahInput = it.filter(Char::isDigit); fromInput = "1"; toInput = "7" },
          label = { Text("رقم السورة من 1 إلى 114") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Text("السورة المختارة: " + SURAH_NAMES[surah - 1] + " • " + toArabic(count) + " آية", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = fromInput, onValueChange = { fromInput = it.filter(Char::isDigit) }, label = { Text("من آية") }, singleLine = true, modifier = Modifier.weight(1f))
          OutlinedTextField(value = toInput, onValueChange = { toInput = it.filter(Char::isDigit) }, label = { Text("إلى آية") }, singleLine = true, modifier = Modifier.weight(1f))
        }
        Text("النطاق الفعلي: " + toArabic(from) + " إلى " + toArabic(to), fontSize = 12.sp)
      }
    }
    Card(shape = RoundedCornerShape(22.dp)) {
      Column(Modifier.padding(17.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (listening) "🎙 يستمع إليك الآن" else "🎧 جاهز للتسميع", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(status, fontSize = 13.sp)
        Button(onClick = {
          val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
          if (!granted) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
          else if (!SpeechRecognizer.isRecognitionAvailable(context)) status = "خدمة التعرف على الكلام غير متاحة على جهازك. تحقق من خدمة Google للتعرف على الكلام."
          else try {
            recognizer?.destroy()
            val sr = SpeechRecognizer.createSpeechRecognizer(context)
            recognizer = sr
            sr.setRecognitionListener(object : RecognitionListener {
              override fun onReadyForSpeech(params: Bundle?) { status = "ابدأ التلاوة الآن…" }
              override fun onBeginningOfSpeech() { listening = true; status = "جارٍ التقاط صوتك…" }
              override fun onRmsChanged(rmsdB: Float) {}
              override fun onBufferReceived(buffer: ByteArray?) {}
              override fun onEndOfSpeech() { listening = false; status = "انتهى الاستماع، جارٍ تجهيز المقارنة…" }
              override fun onError(error: Int) { listening = false; status = "تعذر فهم الصوت (رمز " + error + "). جرّب مكانًا هادئًا ثم أعد المحاولة." }
              override fun onResults(results: Bundle?) {
                listening = false
                transcript = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                status = if (transcript.isBlank()) "لم يتم التقاط كلمات واضحة. أعد المحاولة." else "تم تحويل الصوت إلى نص. راجع المقارنة."
              }
              override fun onPartialResults(partialResults: Bundle?) {}
              override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
              putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
              putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
              putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-SA")
              putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
              putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            }
            transcript = ""
            sr.startListening(intent)
          } catch (_: Exception) { listening = false; status = "تعذر تشغيل التعرف الصوتي على هذا الجهاز." }
        }, enabled = !listening, modifier = Modifier.fillMaxWidth()) {
          Text(if (listening) "يستمع…" else "🎙 ابدأ التسميع")
        }
        if (listening) OutlinedButton(onClick = { recognizer?.stopListening(); listening = false }, modifier = Modifier.fillMaxWidth()) { Text("إنهاء الاستماع") }
      }
    }
    if (transcript.isNotBlank()) {
      Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("نتيجة المقارنة الأولية", fontSize = 19.sp, fontWeight = FontWeight.Bold)
          Text("كلمات متطابقة في مواضعها: " + toArabic(matching) + " من " + toArabic(expectedWords.size))
          Text("النص الذي التقطه الهاتف:", fontWeight = FontWeight.Bold)
          Text(transcript)
          HorizontalDivider()
          Text("مراجعة الكلمات:", fontWeight = FontWeight.Bold)
          expectedWords.forEachIndexed { i, word ->
            val heard = heardWords.getOrNull(i)
            val good = heard == word
            Text((if (good) "✓ " else "• ") + word + (if (heard != null && !good) " — سُمِع: " + heard else if (heard == null) " — لم يلتقطها" else ""),
              color = if (good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontSize = 13.sp)
          }
          if (heardWords.size > expectedWords.size) Text("كلمات إضافية التقطها الهاتف: " + heardWords.drop(expectedWords.size).joinToString("، "), color = MaterialTheme.colorScheme.error)
          OutlinedButton(onClick = { transcript = ""; status = "جاهز لمحاولة جديدة." }, modifier = Modifier.fillMaxWidth()) { Text("إعادة التسميع") }
        }
      }
    }
    Text("تنبيه: هذه مقارنة مبدئية لتحويل الكلام إلى نص؛ قد يخطئ الهاتف في ألفاظ القرآن أو ترتيب الكلمات. لا تفحص التجويد أو الحركات أو مخارج الحروف، ولا تعتمد عليها حكمًا نهائيًا على التلاوة. راجع مواضع الاختلاف مع معلّم متقن.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
  }
}
