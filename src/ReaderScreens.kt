package com.mutqin.app

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
  var pageStart by remember { mutableIntStateOf(from) }
  val pageSize = 10
  val pageEnd = minOf(to, pageStart + pageSize - 1)
  val bookmarked = prefs.bookmark(surah)
  val visibleAyahs = (pageStart..pageEnd).filter { n ->
    search.isBlank() || normalizeAr(repo.ayah(surah, n)).contains(normalizeAr(search.trim()))
  }
  val pageText = visibleAyahs.joinToString("  ") { n ->
    val verse = if (reviewMode && !revealAll) "ــــــــــــ" else repo.ayah(surah, n)
    "$verse ۞${toArabic(n)}"
  }

  LaunchedEffect(surah, pageStart, pageEnd) { prefs.setLastPosition(surah, pageStart, to) }

  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()) {
    if (!focusMode) {
      Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onBack) { Text("رجوع") }
        Column(Modifier.weight(1f)) {
          Text("مُتقن • المصحف", fontWeight = FontWeight.ExtraBold, color = colors.primary, fontSize = 18.sp)
          Text("سورة ${SURAH_NAMES[surah - 1]}  •  الآيات ${toArabic(pageStart)}–${toArabic(pageEnd)}", fontSize = 10.sp, color = colors.onSurfaceVariant)
        }
        TextButton(onClick = { prefs.setBookmark(surah, pageStart); }) { Text(if (bookmarked == pageStart) "★" else "☆", fontSize = 22.sp, color = colors.secondary) }
        TextButton(onClick = { showSearch = !showSearch }) { Text("⌕", fontSize = 23.sp) }
        TextButton(onClick = { showJump = true }) { Text("انتقال") }
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
          pageText,
          Modifier.fillMaxWidth().weight(1f),
          color = colors.onSurface,
          fontSize = fontSize.sp,
          lineHeight = (fontSize * 2.05f).sp,
          textAlign = TextAlign.Justify
        )
      }
      Spacer(Modifier.height(10.dp))
      Text("مُتقن  •  ${SURAH_NAMES[surah - 1]}  •  ${toArabic(pageStart)}–${toArabic(pageEnd)}", Modifier.fillMaxWidth(), color = colors.secondary, fontSize = 10.sp, textAlign = TextAlign.Center)
    }

    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      OutlinedButton(onClick = { pageStart = maxOf(from, pageStart - pageSize) }, enabled = pageStart > from, modifier = Modifier.weight(1f)) { Text("السابق") }
      OutlinedButton(onClick = {
        val clip = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clip.setPrimaryClip(android.content.ClipData.newPlainText("مُتقن", pageText))
      }, modifier = Modifier.weight(1f)) { Text("نسخ الصفحة") }
      Button(onClick = { pageStart = minOf(to, pageStart + pageSize) }, enabled = pageEnd < to, modifier = Modifier.weight(1f)) { Text("التالي") }
    }
    if (bookmarked != null) {
      Text("علامتك المرجعية: الآية ${toArabic(bookmarked)}", Modifier.fillMaxWidth().padding(bottom = 5.dp), color = colors.secondary, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
  }
}
