package com.mutqin.app

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
  var bookmarkVersion by remember { mutableIntStateOf(0) }
  val bookmarks = remember(bookmarkVersion) { prefs.bookmarks() }
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()
  val showBasmala = surah != 1 && surah != 9 && from == 1

  Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Text(
        "سورة ${SURAH_NAMES[surah - 1]}",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("إخفاء الآيات (اختبار الحفظ)")
        Switch(checked = hide, onCheckedChange = { hide = it; revealed.clear() })
      }
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = { showBookmarks = true }) { Text("المحفوظات (${toArabic(bookmarks.size)})") }
        Text("اضغط على النجمة لحفظ الآية", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
      }
      if (hide) {
        Text("اضغط على أي آية لإظهارها أو إخفائها. يظهر لك أول كلمة فقط كتلميح.", fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          TextButton(onClick = { revealed.clear(); revealed.addAll(from..to) }) { Text("إظهار الكل") }
          TextButton(onClick = { revealed.clear() }) { Text("إخفاء الكل") }
        }
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("حجم الخط", fontSize = 13.sp)
        Spacer(Modifier.width(12.dp))
        Slider(
          value = size,
          onValueChange = { size = it },
          onValueChangeFinished = { prefs.setFontSize(size) },
          valueRange = 20f..44f,
          modifier = Modifier.weight(1f)
        )
      }
    }
    HorizontalDivider()
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
      state = listState
    ) {
      if (showBasmala) {
        item {
          Text(
            repo.ayah(1, 1),
            fontSize = size.sp,
            lineHeight = (size * 1.9f).sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          )
        }
      }
      items((from..to).toList()) { n ->
        val text = repo.ayah(surah, n)
        val shown = !hide || n in revealed
        val body = if (shown) text else text.substringBefore(' ') + " ..."
        Row(
          Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            body + "  (" + toArabic(n) + ")",
            fontSize = size.sp,
            lineHeight = (size * 1.9f).sp,
            modifier = Modifier
              .weight(1f)
              .clickable(enabled = hide) {
                if (n in revealed) revealed.remove(n) else revealed.add(n)
              }
              .padding(vertical = 8.dp)
          )
          TextButton(onClick = {
            prefs.toggleBookmark(surah, n)
            bookmarkVersion++
          }, contentPadding = PaddingValues(horizontal = 8.dp)) {
            Text(if (prefs.isBookmarked(surah, n)) "★" else "☆", fontSize = 23.sp,
              color = if (prefs.isBookmarked(surah, n)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        HorizontalDivider()
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
