package com.mutqin.app

import androidx.compose.foundation.background
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
  val searchText = normalizeQuranSearch(query)
  val ayahMatches = remember(repo, searchText) {
    if (searchText.length < 2) emptyList()
    else buildList {
      for (surahNumber in 1..repo.surahCount()) {
        for (ayahNumber in 1..repo.ayahCount(surahNumber)) {
          if (normalizeQuranSearch(repo.ayah(surahNumber, ayahNumber)).contains(searchText)) {
            add(surahNumber to ayahNumber)
            if (size >= 40) break
          }
        }
        if (size >= 40) break
      }
    }
  }
  val filtered = remember(q) {
    (1..repo.surahCount()).filter { n ->
      q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q)
    }
  }

  Column(modifier = Modifier.fillMaxSize().safeDrawingPadding().background(MaterialTheme.colorScheme.background).padding(top=4.dp)) {
    val c = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=7.dp),verticalAlignment=Alignment.CenterVertically) {
      Column(verticalArrangement=Arrangement.spacedBy(1.dp)) {
        Text("مُتْقِن",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
        Text("رحلتك مع القرآن",fontSize=9.sp,color=c.onSurfaceVariant)
      }
      Spacer(Modifier.weight(1f))
      TextButton(onClick={onResume(last?.first ?: 1,last?.second ?: 1,last?.third ?: 7)}) {
        Text("متابعة القراءة  ›",fontSize=11.sp,color=c.secondary,fontWeight=FontWeight.Bold)
      }
      Switch(checked=dark,onCheckedChange=onDark)
    }
    Card(Modifier.fillMaxWidth().padding(horizontal=14.dp),shape=androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
      colors=CardDefaults.cardColors(containerColor=c.primary),
      border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.8f)),
      elevation=CardDefaults.cardElevation(defaultElevation=5.dp)) {
      Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
          Text("الْمُصْحَفُ الشَّرِيف",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
          Spacer(Modifier.weight(1f))
          Text("۞",fontSize=30.sp,color=c.secondary)
        }
        Text("افتح قلبك لآيات الله",fontSize=23.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
        Text("تصفّح السور، وابحث عن آية، وأكمل من حيث توقفت.",fontSize=11.sp,lineHeight=18.sp,color=c.onPrimary.copy(alpha=.82f))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalAlignment=Alignment.CenterVertically) {
          Surface(shape=androidx.compose.foundation.shape.RoundedCornerShape(50),color=c.onPrimary.copy(alpha=.10f)) {
            Text("١١٤ سورة",modifier=Modifier.padding(horizontal=12.dp,vertical=7.dp),fontSize=10.sp,color=c.onPrimary,fontWeight=FontWeight.SemiBold)
          }
          Surface(shape=androidx.compose.foundation.shape.RoundedCornerShape(50),color=c.secondary.copy(alpha=.20f)) {
            Text("بحث في القرآن",modifier=Modifier.padding(horizontal=12.dp,vertical=7.dp),fontSize=10.sp,color=c.onPrimary,fontWeight=FontWeight.SemiBold)
          }
        }
      }
    }
    OutlinedTextField(
      value=query,onValueChange={query=it},
      label={Text("ابحث باسم السورة أو بكلمات من آية")},
      leadingIcon={Text("⌕",fontSize=23.sp,color=c.secondary)},
      singleLine=true,
      shape=androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
      colors=OutlinedTextFieldDefaults.colors(
        focusedBorderColor=c.secondary,
        unfocusedBorderColor=c.outline.copy(alpha=.45f),
        focusedContainerColor=c.surface,
        unfocusedContainerColor=c.surface
      ),
      modifier=Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=12.dp)
    )
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      if (searchText.length >= 2) {
        item {
          Text(
            "نتائج البحث في نص الآيات: ${toArabic(ayahMatches.size)} — الحد الأقصى 40 نتيجة",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
        if (ayahMatches.isEmpty()) {
          item { Text("لم نعثر على آية تطابق عبارة البحث.") }
        }
        items(ayahMatches) { match ->
          val (surahNumber, ayahNumber) = match
          Card(
            modifier = Modifier.fillMaxWidth().clickable { onResume(surahNumber, ayahNumber, ayahNumber) },shape=androidx.compose.foundation.shape.RoundedCornerShape(23.dp),
            colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
            border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.outline.copy(alpha=.25f)),
            elevation=CardDefaults.cardElevation(defaultElevation=1.dp)
          ) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                "سورة ${SURAH_NAMES[surahNumber - 1]} — الآية ${toArabic(ayahNumber)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                repo.ayah(surahNumber, ayahNumber),
                fontSize = 19.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Start
              )
              Text("اضغط لفتح الآية في المصحف", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
            }
          }
        }
      }
      if (last != null && q.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth().clickable { onResume(last.first, last.second, last.third) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape=androidx.compose.foundation.shape.RoundedCornerShape(23.dp),
            border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.secondary.copy(alpha=.55f)),
            elevation=CardDefaults.cardElevation(defaultElevation=3.dp)
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
      if (filtered.isEmpty() && searchText.length < 2) {
        item { Text("لا توجد سورة بهذا الاسم أو الرقم") }
      }
      if (searchText.length < 2) items(filtered) { n ->
        Card(modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) },shape=androidx.compose.foundation.shape.RoundedCornerShape(21.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.secondary.copy(alpha=.20f)),elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier=Modifier.size(47.dp).background(MaterialTheme.colorScheme.primary.copy(alpha=.09f),androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
              contentAlignment=Alignment.Center
            ) {
              Text(toArabic(n),color=MaterialTheme.colorScheme.secondary,fontWeight=FontWeight.ExtraBold,fontSize=15.sp)
            }
            Column(Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(3.dp)) {
              Text(SURAH_NAMES[n - 1], fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,color=MaterialTheme.colorScheme.onSurface)
              Text("سُورَةٌ مِنَ الْقُرْآنِ الْكَرِيم",fontSize=9.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End,verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("${toArabic(repo.ayahCount(n))} آية", fontSize = 11.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)
              Text("اقرأ الآن  ›",fontSize=9.sp,color=MaterialTheme.colorScheme.secondary,fontWeight=FontWeight.Bold)
            }
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
