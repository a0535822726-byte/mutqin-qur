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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RedesignedHomeScreen(
 repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit,
 onOpenSurah: (Int) -> Unit, onResume: (Int, Int, Int) -> Unit,
 onOpenRecorder: () -> Unit, onOpenCheck: () -> Unit, onOpenTajweed: () -> Unit,
 onOpenStories: () -> Unit, onOpenCircle: () -> Unit, onOpenReciters: () -> Unit,
 onOpenDailyTools: () -> Unit, onOpenTool: (String) -> Unit
) {
 var query by remember { mutableStateOf("") }
 val colors = MaterialTheme.colorScheme
 val last = prefs.lastPosition()
 val matches = remember(query) {
  (1..repo.surahCount()).filter { n -> query.isBlank() || n.toString() == query.trim() || normalizeAr(SURAH_NAMES[n-1]).contains(normalizeAr(query.trim())) }
 }
 LazyColumn(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
  item {
   Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Column(Modifier.weight(1f)) {
     Text("مُتقن", fontSize = 30.sp, fontWeight = FontWeight.Black, color = colors.primary)
     Text("رحلتك القرآنية في مساحة واحدة", fontSize = 11.sp, color = colors.onSurfaceVariant)
    }
    FilledTonalButton(onClick = { onDark(!dark) }, shape = RoundedCornerShape(14.dp)) { Text(if (dark) "☀" else "☾") }
   }
  }
  item {
   Card(shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = colors.primary)) {
    Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF082F27), Color(0xFF0D5C48), Color(0xFF8B713B)))).padding(22.dp)) {
     Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text("مساحة القرآن الكريم", color = Color(0xFFEBD59F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
      Text("تلاوة • حفظ • مراجعة • تدبّر", color = Color.White.copy(alpha=.88f), fontSize = 12.sp)
      Button(onClick = { if (last != null) onResume(last.first,last.second,last.third) else onOpenSurah(1) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE7C477), contentColor = Color(0xFF123B2F)), shape = RoundedCornerShape(14.dp)) {
       Text(if (last != null) "تابع من حيث وصلت" else "ابدأ رحلتك الآن", fontWeight = FontWeight.Black)
      }
      if (last != null) Text("آخر قراءة: " + SURAH_NAMES[last.first-1] + " • الآية " + toArabic(last.second), color = Color.White, fontSize = 11.sp)
     }
    }
   }
  }
  item {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    HomeMetric("١١٤", "سورة", Modifier.weight(1f))
    HomeMetric("٣٠", "جزءًا", Modifier.weight(1f))
    HomeMetric("٤", "مسارات رئيسية", Modifier.weight(1f))
   }
  }
  item { HomeDailyRoutine(prefs) }
  item {
   HomeHeading("المسارات الرئيسية", "كل مسار له وظيفة واضحة")
   Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
    HomeLargeAction("◉", "التسميع الذكي المباشر", "استمع إلى قراءتك وأظهر مواضع الاختلاف", Color(0xFFDCEBE3), Color(0xFF104D3C), onOpenCheck)
    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
     HomeSmallAction("▤", "المصحف", "قراءة وتدبّر", Modifier.weight(1f)) { onOpenSurah(1) }
     HomeSmallAction("✧", "أكاديمية التجويد", "دروس واختبارات", Modifier.weight(1f), onOpenTajweed)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
     HomeSmallAction("◈", "الحفظ والمراجعة", "خطط ومتابعة", Modifier.weight(1f)) { onOpenTool("unique_memorization") }
     HomeSmallAction("♫", "الاستماع", "مكتبة القرّاء", Modifier.weight(1f), onOpenReciters)
    }
   }
  }
  item {
   HomeHeading("مكتبة ومجتمع", "تعلّم واستمع وشارك")
   Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    HomeWideAction("◉", "استديو التسجيل", "سجّل صوتك واستمع إليه", onOpenRecorder)
    HomeWideAction("◇", "قصص قرآنية", "قصص وعبر", onOpenStories)
    HomeWideAction("⌂", "حلقة القرآن", "متابعة الحلقة", onOpenCircle)
    HomeWideAction("▦", "منظّم الورد اليومي", "خطط الحفظ والمراجعة والقراءة", onOpenDailyTools)
   }
  }
  item {
   HomeHeading("مراكز متخصصة", "أدوات أعمق للبحث والتعلّم")
   Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    HomeWideAction("⌘", "مركز الإتقان المتقدم", "إحصاءات وخطط ومتابعة") { onOpenTool("unique_master_tools") }
    HomeWideAction("⌕", "مختبر الدراسة القرآنية", "متشابهات وقاموس وبطاقات") { onOpenTool("unique_study_lab") }
    HomeWideAction("↻", "المراجعة المتباعدة", "تثبيت الحفظ على مراحل") { onOpenTool("unique_spaced_review") }
    HomeWideAction("◎", "تحدي الحفظ", "اختبر استدعاء محفوظك") { onOpenTool("unique_memorization") }
    HomeWideAction("↗", "باحث ألفاظ القرآن", "ابحث في نص المصحف") { onOpenTool("unique_word_search") }
   }
  }
  item {
   HomeHeading("فهرس المصحف", "ابحث باسم السورة أو رقمها")
   OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(15.dp), label = { Text("اسم السورة أو رقمها") })
  }
  items(matches) { n ->
   Card(onClick = { onOpenSurah(n) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
    Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
     Surface(shape = RoundedCornerShape(11.dp), color = colors.primaryContainer) { Text(toArabic(n), Modifier.padding(horizontal=13.dp, vertical=9.dp), color=colors.primary, fontWeight=FontWeight.Bold) }
     Spacer(Modifier.width(12.dp))
     Column(Modifier.weight(1f)) { Text(SURAH_NAMES[n-1], fontWeight=FontWeight.Bold); Text("عدد الآيات: " + toArabic(repo.ayahCount(n)), fontSize=10.sp, color=colors.onSurfaceVariant) }
     Text("فتح ←", color=colors.primary, fontWeight=FontWeight.Bold)
    }
   }
  }
  item { Text("تنبيه: نتائج التسميع تقديرية، ولا تغني عن المعلّم المتقن.", Modifier.fillMaxWidth(), textAlign=TextAlign.Center, fontSize=10.sp, color=colors.onSurfaceVariant) }
 }
}

@Composable private fun HomeHeading(title:String, subtitle:String) { Column(verticalArrangement=Arrangement.spacedBy(3.dp)) { Text(title,fontSize=19.sp,fontWeight=FontWeight.Black,color=MaterialTheme.colorScheme.onBackground); Text(subtitle,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun HomeMetric(value:String,label:String,modifier:Modifier) { Card(modifier,shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Column(Modifier.fillMaxWidth().padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally) { Text(value,fontSize=21.sp,fontWeight=FontWeight.Black,color=MaterialTheme.colorScheme.primary); Text(label,fontSize=9.sp,color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
@Composable private fun HomeLargeAction(icon:String,title:String,detail:String,bg:Color,fg:Color,onClick:()->Unit) { Card(onClick=onClick,shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=bg)) { Row(Modifier.fillMaxWidth().padding(17.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) { Text(icon,fontSize=29.sp,color=fg); Column(Modifier.weight(1f)) { Text(title,fontSize=15.sp,fontWeight=FontWeight.Black,color=fg); Text(detail,fontSize=10.sp,color=fg.copy(alpha=.8f)) }; Text("←",fontSize=20.sp,color=fg) } } }
@Composable private fun HomeSmallAction(icon:String,title:String,detail:String,modifier:Modifier=Modifier,onClick:()->Unit) { Card(onClick=onClick,modifier=modifier.heightIn(min=105.dp),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Column(Modifier.fillMaxWidth().padding(13.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) { Text(icon,fontSize=23.sp,color=MaterialTheme.colorScheme.secondary); Text(title,fontWeight=FontWeight.Bold,fontSize=12.sp); Text(detail,fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
@Composable private fun HomeWideAction(icon:String,title:String,detail:String,onClick:()->Unit) { Card(onClick=onClick,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Row(Modifier.fillMaxWidth().padding(13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)) { Surface(shape=RoundedCornerShape(12.dp),color=MaterialTheme.colorScheme.primaryContainer) { Text(icon,Modifier.padding(10.dp),fontSize=18.sp,color=MaterialTheme.colorScheme.primary) }; Column(Modifier.weight(1f)) { Text(title,fontWeight=FontWeight.Bold,fontSize=13.sp); Text(detail,fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant) }; Text("←",color=MaterialTheme.colorScheme.primary,fontSize=19.sp) } } }


@Composable
private fun HomeDailyRoutine(prefs: AppPrefs) {
 val day = java.time.LocalDate.now().toString()
 val tasks = listOf("قراءة الورد اليومي", "حفظ آيات جديدة", "مراجعة المحفوظ", "تعلّم حكم تجويدي")
 val completed = remember(day) {
  mutableStateListOf<Boolean>().apply {
   repeat(tasks.size) { index -> add(prefs.toolFlag("routine_${day}_${index}")) }
  }
 }
 val doneCount = completed.count { it }
 val colors = MaterialTheme.colorScheme
 Card(
  modifier = Modifier.fillMaxWidth(),
  shape = RoundedCornerShape(22.dp),
  colors = CardDefaults.cardColors(containerColor = colors.surface)
 ) {
  Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
   Row(verticalAlignment = Alignment.CenterVertically) {
    Column(Modifier.weight(1f)) {
     Text("وردك اليومي", fontSize = 18.sp, fontWeight = FontWeight.Black)
     Text("قائمة متابعة تتجدد تلقائيًا كل يوم", fontSize = 10.sp, color = colors.onSurfaceVariant)
    }
    Text("${toArabic(doneCount)}/٤", color = colors.primary, fontWeight = FontWeight.Black, fontSize = 16.sp)
   }
   LinearProgressIndicator(
    progress = { doneCount / 4f },
    modifier = Modifier.fillMaxWidth(),
    color = colors.primary,
    trackColor = colors.surfaceVariant
   )
   tasks.forEachIndexed { index, task ->
    Row(
     Modifier.fillMaxWidth(),
     verticalAlignment = Alignment.CenterVertically
    ) {
     Checkbox(
      checked = completed[index],
      onCheckedChange = { checked ->
       completed[index] = checked
       prefs.setToolFlag("routine_${day}_${index}", checked)
      }
     )
     Text(task, Modifier.weight(1f), fontSize = 13.sp, color = colors.onSurface)
     if (completed[index]) Text("تم", color = colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
   }
   if (doneCount == tasks.size) {
    Text("أحسنت! أكملت وردك المخطط لهذا اليوم.", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
   }
  }
 }
}
