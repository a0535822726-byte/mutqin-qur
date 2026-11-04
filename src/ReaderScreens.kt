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
  var hide by remember { mutableStateOf(false) }
  var size by remember { mutableStateOf(prefs.fontSize().coerceIn(22f, 38f)) }
  val revealed = remember { mutableStateListOf<Int>() }
  val showBasmala = surah != 1 && surah != 9 && from == 1
  val listState = rememberLazyListState()
  val paper = androidx.compose.ui.graphics.Color(0xFFFFFCF2)
  val ink = androidx.compose.ui.graphics.Color(0xFF263D32)
  val gold = androidx.compose.ui.graphics.Color(0xFFB38A3E)

  LaunchedEffect(listState, surah, from, to, showBasmala) {
    snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
      val ayahNumber = from + index - if (showBasmala) 1 else 0
      if (ayahNumber in from..to) prefs.setLastPosition(surah, ayahNumber, to)
    }
  }

  Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Column(Modifier.weight(1f)) {
        Text("المصحف الشريف", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 17.sp)
        Text("سورة ${SURAH_NAMES[surah - 1]} • ${toArabic(from)}–${toArabic(to)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
      }
      Text("۞", fontSize = 25.sp, color = gold)
    }
    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(17.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
      Column(Modifier.padding(horizontal = 13.dp, vertical = 5.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          Text("وضع المراجعة", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
          Switch(checked = hide, onCheckedChange = { hide = it; revealed.clear() })
        }
        if (hide) {
          Text("اضغط على الآية لإظهارها. يمكنك كشف الكل أو إخفاؤه.", fontSize = 10.sp)
          Row {
            TextButton(onClick = { revealed.clear(); revealed.addAll(from..to) }) { Text("إظهار الكل") }
            TextButton(onClick = { revealed.clear() }) { Text("إخفاء الكل") }
          }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("حجم الخط", fontSize = 11.sp)
          Slider(value = size, onValueChange = { size = it }, onValueChangeFinished = { prefs.setFontSize(size) },
            valueRange = 22f..38f, modifier = Modifier.weight(1f))
        }
      }
    }
    Spacer(Modifier.height(8.dp))
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)) {
      item {
        Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)
          .border(1.dp, gold.copy(alpha = .8f), RoundedCornerShape(8.dp))
          .background(paper, RoundedCornerShape(8.dp)).padding(13.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Text("۞  بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ  ۞", color = ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
          Spacer(Modifier.height(5.dp))
          Text("سُورَةُ ${SURAH_NAMES[surah - 1]}", color = ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Text("❖ ─────────────── ❖", color = gold, fontSize = 13.sp)
        }
      }
      if (showBasmala) {
        item {
          Text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            fontSize = size.sp, lineHeight = (size * 1.9f).sp, textAlign = TextAlign.Center, color = ink)
        }
      }
      items((from..to).toList()) { n ->
        val text = repo.ayah(surah, n)
        val shown = !hide || n in revealed
        val body = if (shown) text else text.substringBefore(' ') + " …"
        Row(Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 1.dp).background(paper), verticalAlignment = Alignment.CenterVertically) {
          Text(body, modifier = Modifier.weight(1f).clickable(enabled = hide) {
            if (n in revealed) revealed.remove(n) else revealed.add(n)
          }.padding(vertical = 5.dp), fontSize = size.sp, lineHeight = (size * 1.85f).sp,
            textAlign = TextAlign.Center, color = ink)
          Text("۞${toArabic(n)}", color = gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
      item {
        Text("صَدَقَ اللَّهُ الْعَظِيمُ", color = ink, fontSize = 15.sp, textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth().background(paper).border(1.dp, gold, RoundedCornerShape(8.dp)).padding(12.dp))
      }
    }
  }
}
