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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahListScreen(
  repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit,
  onOpenSurah: (Int) -> Unit, onResume: (Int, Int, Int) -> Unit,
  onOpenRecorder: () -> Unit, onOpenCheck: () -> Unit, onOpenTajweed: () -> Unit, onOpenStories: () -> Unit, onOpenReciters: () -> Unit
) {
  var query by remember { mutableStateOf("") }
  val last = remember { prefs.lastPosition() }
  val q = normalizeAr(query.trim())
  val filtered = remember(q) { (1..repo.surahCount()).filter { n -> q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q) } }
  val colors = MaterialTheme.colorScheme

  LazyColumn(
    modifier = Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(),
    contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
          Text("مُتقن", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
          Text("رحلتك اليومية مع القرآن الكريم", fontSize = 13.sp, color = colors.onBackground.copy(alpha = .65f))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("الوضع الليلي", fontSize = 10.sp, color = colors.onBackground.copy(alpha = .7f))
          Switch(checked = dark, onCheckedChange = onDark)
        }
      }
    }
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primary),
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          Modifier.fillMaxWidth().background(
            Brush.linearGradient(listOf(colors.primary, colors.primary.copy(alpha = .78f), colors.secondary.copy(alpha = .75f)))
          ).padding(22.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("السَّلَامُ عَلَيْكَ يَا صَاحِبَ الْقُرْآنِ", color = colors.onPrimary.copy(alpha = .9f), fontSize = 13.sp)
            Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا", color = colors.onPrimary, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text("خطوة صغيرة كل يوم تصنع أثرًا عظيمًا.", color = colors.onPrimary.copy(alpha = .9f), fontSize = 13.sp)
            if (last != null) {
              Button(
                onClick = { onResume(last.first, last.second, last.third) },
                colors = ButtonDefaults.buttonColors(containerColor = colors.surface, contentColor = colors.primary),
                shape = RoundedCornerShape(14.dp)
              ) { Text("متابعة من آخر موضع  ←", fontWeight = FontWeight.Bold) }
              Text("سورة ${SURAH_NAMES[last.first - 1]} • الآية ${toArabic(last.second)}", color = colors.onPrimary, fontSize = 12.sp)
            }
          }
        }
      }
    }
    item {
      Text("ماذا تريد أن تفعل؟", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
      Spacer(Modifier.height(10.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FeatureCard("🎙", "التسميع", "مقارنة مبدئية", Modifier.weight(1f), onOpenCheck)
        FeatureCard("📖", "المصحف", "قراءة السور", Modifier.weight(1f)) { onOpenSurah(1) }
      }
      Spacer(Modifier.height(10.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FeatureCard("۞", "التجويد", "دروس واختبار", Modifier.weight(1f), onOpenTajweed)
        FeatureCard("🌿", "القصص", "قصص قرآنية", Modifier.weight(1f), onOpenStories)
      }
      Spacer(Modifier.height(10.dp))
      FeatureCard("🎼", "القرّاء", "تلاوات حقيقية", Modifier.fillMaxWidth(), onOpenReciters)
      FeatureCard("🎧", "التسجيل", "استمع لصوتك", Modifier.fillMaxWidth(), onOpenRecorder)
    }
    item {
      Text("سور القرآن الكريم", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
      Text("١١٤ سورة بين يديك", fontSize = 12.sp, color = colors.onBackground.copy(alpha = .6f))
      Spacer(Modifier.height(8.dp))
      OutlinedTextField(
        value = query, onValueChange = { query = it },
        label = { Text("ابحث باسم السورة أو رقمها") },
        singleLine = true, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
      )
    }
    if (filtered.isEmpty()) {
      item { Text("لا توجد سورة بهذا الاسم", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
    }
    items(filtered) { n ->
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) }
      ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
          Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(colors.primary.copy(alpha = .10f)),
            contentAlignment = Alignment.Center
          ) { Text(toArabic(n), color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
          Spacer(Modifier.width(13.dp))
          Column(Modifier.weight(1f)) {
            Text(SURAH_NAMES[n - 1], fontSize = 17.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text("عدد الآيات: ${toArabic(repo.ayahCount(n))}", fontSize = 11.sp, color = colors.onSurface.copy(alpha = .6f))
          }
          Text("‹", fontSize = 28.sp, color = colors.secondary)
        }
      }
    }
    item {
      Text(
        "ملاحظة: نتيجة التسميع تعتمد على التعرّف الصوتي في الهاتف، وقد تحتاج إلى مراجعة بشرية.",
        fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.onBackground.copy(alpha = .55f),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
      )
    }
  }
}

@Composable
private fun FeatureCard(
  icon: String, title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = modifier
  ) {
    Row(
      Modifier.fillMaxWidth().padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        Modifier.size(42.dp).clip(RoundedCornerShape(14.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = .10f)),
        contentAlignment = Alignment.Center
      ) { Text(icon, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary) }
      Column(Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .62f))
      }
    }
  }
}
