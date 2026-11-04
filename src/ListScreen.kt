package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahListScreen(repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit, onOpenSurah: (Int) -> Unit, onResume: (Int, Int, Int) -> Unit, onOpenRecorder: () -> Unit, onOpenCheck: () -> Unit, onOpenTajweed: () -> Unit, onOpenStories: () -> Unit, onOpenReciters: () -> Unit) {
  var query by remember { mutableStateOf("") }
  val last = remember { prefs.lastPosition() }
  val q = normalizeAr(query.trim())
  val filtered = remember(q) { (1..repo.surahCount()).filter { n -> q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q) } }
  val colors = MaterialTheme.colorScheme
  LazyColumn(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(), contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 32.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
    item {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(colors.primary, colors.secondary))), contentAlignment = Alignment.Center) { Text("م", color = colors.onPrimary, fontSize = 29.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) { Text("مُتقن", fontSize = 27.sp, fontWeight = FontWeight.Black, color = colors.primary); Text("رفيق رحلتك مع القرآن", fontSize = 11.sp, color = colors.onSurfaceVariant) }
        Column(horizontalAlignment = Alignment.End) {
          Text("رحلتك اليوم", fontSize = 10.sp, color = colors.onSurfaceVariant)
          Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { onDark(!dark) }, modifier = Modifier.size(38.dp)) { Text(if (dark) "☀" else "☾", fontSize = 21.sp, color = colors.primary) }; Text(if (dark) "ليلي" else "نهاري", fontSize = 10.sp, color = colors.onSurfaceVariant) }
        }
      }
    }
    item {
      Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = colors.primary), modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(colors.primary, colors.primary.copy(alpha = .78f), colors.secondary.copy(alpha = .85f)))).padding(20.dp)) {
          Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("﴿ وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا ﴾", color = colors.onPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("لا تستعجل الإنجاز؛ اجعل لك مع القرآن موعدًا ثابتًا.", color = colors.onPrimary.copy(alpha = .9f), fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Surface(color = colors.onPrimary.copy(alpha = .16f), shape = CircleShape) { Text("  ١١٤ سورة  ", color = colors.onPrimary, fontSize = 10.sp, modifier = Modifier.padding(vertical = 7.dp)) }
              Surface(color = colors.onPrimary.copy(alpha = .16f), shape = CircleShape) { Text("  قراءة • حفظ • استماع  ", color = colors.onPrimary, fontSize = 10.sp, modifier = Modifier.padding(vertical = 7.dp)) }
            }
            if (last != null) {
              Button(onClick = { onResume(last.first, last.second, last.third) }, colors = ButtonDefaults.buttonColors(containerColor = colors.surface, contentColor = colors.primary), shape = RoundedCornerShape(14.dp)) { Text("استكمل رحلتك  ←", fontWeight = FontWeight.Bold) }
              Text("آخر موضع: ${SURAH_NAMES[last.first - 1]} • الآية ${toArabic(last.second)}", color = colors.onPrimary, fontSize = 11.sp)
            } else {
              Button(onClick = { onOpenSurah(1) }, colors = ButtonDefaults.buttonColors(containerColor = colors.surface, contentColor = colors.primary), shape = RoundedCornerShape(14.dp)) { Text("ابدأ القراءة الآن  ←", fontWeight = FontWeight.Bold) }
            }
          }
        }
      }
    }
    item {
      Text("مسارات مُتقن", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground)
      Text("اختر ما تحتاجه الآن", fontSize = 11.sp, color = colors.onSurfaceVariant)
      Spacer(Modifier.height(9.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { FeatureCard("🎙", "جلسة التسميع", "تدريب صوتي", Modifier.weight(1f), onOpenCheck); FeatureCard("📖", "المصحف", "قراءة ومراجعة", Modifier.weight(1f)) { onOpenSurah(1) } }
      Spacer(Modifier.height(9.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { FeatureCard("✨", "تعلم التجويد", "دروس واختبارات", Modifier.weight(1f), onOpenTajweed); FeatureCard("📚", "مكتبة القصص", "قصص وعبر", Modifier.weight(1f), onOpenStories) }
      Spacer(Modifier.height(9.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { FeatureCard("🎧", "القرّاء", "استمع للتلاوات", Modifier.weight(1f), onOpenReciters); FeatureCard("🎚", "استديو صوتي", "سجّل واستمع", Modifier.weight(1f), onOpenRecorder) }
    }
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text("المصحف الشريف", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground); Text("ابحث عن سورة أو افتحها مباشرة", fontSize = 11.sp, color = colors.onSurfaceVariant) }
        Text("١١٤", color = colors.primary, fontWeight = FontWeight.Bold)
      }
      Spacer(Modifier.height(8.dp))
      OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("اسم السورة أو رقمها") }, leadingIcon = { Text("⌕", fontSize = 22.sp) }, singleLine = true, shape = RoundedCornerShape(17.dp), modifier = Modifier.fillMaxWidth())
    }
    if (filtered.isEmpty()) item { Text("لا توجد سورة مطابقة لبحثك", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
    items(filtered) { n ->
      Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) }) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(colors.primary.copy(alpha = .10f)), contentAlignment = Alignment.Center) { Text(toArabic(n), color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp) }
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) { Text(SURAH_NAMES[n - 1], fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface); Text("عدد الآيات: ${toArabic(repo.ayahCount(n))}", fontSize = 10.sp, color = colors.onSurfaceVariant) }
          Text("فتح  ›", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
    item { Text("تنبيه: جلسة الصوت الحالية للتدريب والاستماع الذاتي، ولا تدّعي تشخيص أحكام التجويد آليًا.", fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) }
  }
}

@Composable
private fun FeatureCard(icon: String, title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Card(onClick = onClick, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = modifier) {
    Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
      Box(Modifier.size(39.dp).clip(RoundedCornerShape(13.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = .11f)), contentAlignment = Alignment.Center) { Text(icon, fontSize = 21.sp) }
      Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
      Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
