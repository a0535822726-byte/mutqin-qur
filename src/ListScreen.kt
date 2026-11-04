package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahListScreen(repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit,
  onOpenSurah: (Int) -> Unit, onResume: (Int, Int, Int) -> Unit,
  onOpenRecorder: () -> Unit, onOpenCheck: () -> Unit, onOpenTajweed: () -> Unit, onOpenStories: () -> Unit) {
  var query by remember { mutableStateOf("") }
  val last = remember { prefs.lastPosition() }
  val q = normalizeAr(query.trim())
  val filtered = remember(q) { (1..repo.surahCount()).filter { n -> q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q) } }
  Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
      item {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
          Column {
            Text("مُتقن", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text("رحلتك اليومية مع القرآن الكريم", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = .65f))
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("ليلي", fontSize = 12.sp)
            Switch(checked = dark, onCheckedChange = onDark)
          }
        }
      }
      item {
        Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
          Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            Text("اقرأ، راجع حفظك، وتعلّم أحكام التلاوة خطوة بخطوة.", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .88f))
            if (last != null) {
              Button(onClick = { onResume(last.first, last.second, last.third) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("متابعة القراءة ←", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
              }
              Text("آخر موضع: " + SURAH_NAMES[last.first - 1] + " • الآية " + toArabic(last.second), color = MaterialTheme.colorScheme.onPrimary)
            }
          }
        }
      }
      item {
        Text("ماذا تريد أن تفعل؟", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
          ActionTile("🎙", "اختبار التسميع", "مقارنة صوتك بالنص", Modifier.weight(1f), onOpenCheck)
          ActionTile("📖", "المصحف", "قراءة السور", Modifier.weight(1f)) { onOpenSurah(1) }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
          ActionTile("📚", "التجويد", "دروس واختبارات", Modifier.weight(1f), onOpenTajweed)
          ActionTile("🌿", "قصص قرآنية", "عبر من القرآن", Modifier.weight(1f), onOpenStories)
        }
        Spacer(Modifier.height(8.dp))
        ActionTile("🎧", "مسجل الصوت", "تسجيل واستماع فقط", Modifier.fillMaxWidth(), onOpenRecorder)
      }
      item {
        Text("سور القرآن الكريم", fontWeight = FontWeight.Bold, fontSize = 19.sp)
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("ابحث باسم السورة أو رقمها") },
          singleLine = true, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(top = 7.dp))
      }
      if (filtered.isEmpty()) item { Text("لا توجد سورة بهذا الاسم", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
      items(filtered) { n ->
        Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) }) {
          Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
              Text(toArabic(n), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
              Text(SURAH_NAMES[n - 1], fontSize = 18.sp, fontWeight = FontWeight.Bold)
              Text("عدد الآيات: " + toArabic(repo.ayahCount(n)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
            }
            Text("‹", fontSize = 27.sp, color = MaterialTheme.colorScheme.secondary)
          }
        }
      }
      item {
        Text("تنبيه: التسميع يقارن الكلمات التي يتعرف عليها الهاتف بالنص، ولا يصحح التجويد أو مخارج الحروف.", fontSize = 11.sp,
          textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f), modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

@Composable
private fun ActionTile(icon: String, title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Card(onClick = onClick, shape = RoundedCornerShape(19.dp), modifier = modifier) {
    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
      Text(icon, fontSize = 25.sp)
      Spacer(Modifier.width(9.dp))
      Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
      }
    }
  }
}
