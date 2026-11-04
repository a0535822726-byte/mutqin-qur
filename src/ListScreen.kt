package com.mutqin.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahListScreen(
  repo: QuranRepository,
  prefs: AppPrefs,
  dark: Boolean,
  onDark: (Boolean) -> Unit,
  onOpenSurah: (Int) -> Unit,
  onResume: (Int, Int, Int) -> Unit
) {
  var query by remember { mutableStateOf("") }
  val last = remember { prefs.lastPosition() }
  val q = normalizeAr(query.trim())
  val filtered = remember(q) {
    (1..repo.surahCount()).filter { n ->
      q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q)
    }
  }

  Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text("مُتقن", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("رفيق القرآن", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("الوضع الليلي", fontSize = 13.sp)
        Spacer(Modifier.width(8.dp))
        Switch(checked = dark, onCheckedChange = onDark)
      }
    }
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      label = { Text("ابحث عن سورة بالاسم أو الرقم") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      if (last != null && q.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth().clickable { onResume(last.first, last.second, last.third) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              val c = MaterialTheme.colorScheme.background
              Text("متابعة آخر قراءة", fontWeight = FontWeight.Bold, color = c)
              Text(
                "سورة ${SURAH_NAMES[last.first - 1]}، الآيات ${toArabic(last.second)} - ${toArabic(last.third)}",
                color = c
              )
            }
          }
        }
      }
      if (filtered.isEmpty()) {
        item { Text("لا توجد سورة بهذا الاسم") }
      }
      items(filtered) { n ->
        Card(modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) }) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                toArabic(n),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
              )
              Text(SURAH_NAMES[n - 1], fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
            Text("${toArabic(repo.ayahCount(n))} آية", fontSize = 13.sp)
          }
        }
      }
      item {
        Text(
          "نص القرآن الكريم (الرسم العثماني): مشروع تنزيل tanzil.net، تم الحصول عليه عبر alquran.cloud، دون أي تغيير في الآيات.",
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
      }
    }
  }
}
