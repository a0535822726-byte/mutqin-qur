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
  repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean)->Unit,
  onOpenSurah: (Int)->Unit, onResume: (Int,Int,Int)->Unit
) {
  var query by remember { mutableStateOf("") }
  val last=remember { prefs.lastPosition() }
  val q=normalizeAr(query.trim())
  val searchText=normalizeQuranSearch(query)
  val ayahMatches=remember(repo,searchText) {
    if(searchText.length<2) emptyList() else buildList {
      for(s in 1..repo.surahCount()) { for(a in 1..repo.ayahCount(s)) {
        if(normalizeQuranSearch(repo.ayah(s,a)).contains(searchText)) { add(s to a); if(size>=40) break }
      }; if(size>=40) break }
    }
  }
  val filtered=remember(q) { (1..repo.surahCount()).filter { n -> q.isEmpty() || n.toString()==q || normalizeAr(SURAH_NAMES[n-1]).contains(q) } }
  val c=MaterialTheme.colorScheme
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=17.dp),verticalAlignment=Alignment.CenterVertically) {
      Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) { Text("مُتْقِن  •  الْمُصْحَف",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary); Text("المصحف الشريف",fontSize=26.sp,fontWeight=FontWeight.Black,color=c.onBackground) }
      Switch(checked=dark,onCheckedChange=onDark)
    }
    Card(Modifier.fillMaxWidth().padding(horizontal=16.dp),shape=androidx.compose.foundation.shape.RoundedCornerShape(25.dp),colors=CardDefaults.cardColors(containerColor=c.primary),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.55f))) {
      Column(Modifier.fillMaxWidth().padding(horizontal=22.dp,vertical=23.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) { Text("۞",fontSize=34.sp,color=c.secondary); Spacer(Modifier.weight(1f)); Text("الْقُرْآنُ الْكَرِيم",fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.secondary) }
        Text("اقرأ بقلب حاضر",fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
        Text("فهرس السور • البحث في الآيات • حفظ موضع القراءة",fontSize=11.sp,lineHeight=20.sp,color=c.onPrimary.copy(alpha=.8f))
        if(last!=null) Surface(Modifier.fillMaxWidth().clickable { onResume(last.first,last.second,last.third) },shape=androidx.compose.foundation.shape.RoundedCornerShape(18.dp),color=c.onPrimary.copy(alpha=.09f)) { Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically) { Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) { Text("متابعة التلاوة",fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.secondary); Text("سورة ${SURAH_NAMES[last.first-1]} • الآية ${toArabic(last.second)}",fontSize=12.sp,color=c.onPrimary) }; Text("←",fontSize=23.sp,color=c.secondary) } }
      }
    }
    OutlinedTextField(value=query,onValueChange={query=it},placeholder={Text("ابحث عن سورة أو كلمة من آية",fontSize=12.sp)},leadingIcon={Text("⌕",fontSize=25.sp,color=c.secondary)},singleLine=true,shape=androidx.compose.foundation.shape.RoundedCornerShape(20.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=c.secondary,unfocusedBorderColor=c.outlineVariant,focusedContainerColor=c.surface,unfocusedContainerColor=c.surface),modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=12.dp))
    LazyColumn(contentPadding=PaddingValues(start=16.dp,end=16.dp,top=3.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
      if(searchText.length>=2) { item { Text("نتائج الآيات  •  ${toArabic(ayahMatches.size)}",fontSize=13.sp,fontWeight=FontWeight.Bold,color=c.onBackground,modifier=Modifier.padding(vertical=5.dp)) }; if(ayahMatches.isEmpty()) item { Text("لم نعثر على آية تطابق البحث.",fontSize=12.sp,color=c.onSurfaceVariant) }; items(ayahMatches) { match -> val (s,a)=match; Card(Modifier.fillMaxWidth().clickable { onResume(s,a,a) },shape=androidx.compose.foundation.shape.RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant)) { Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("سورة ${SURAH_NAMES[s-1]}  •  الآية ${toArabic(a)}",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary); Text(repo.ayah(s,a),fontSize=19.sp,lineHeight=34.sp,color=c.onSurface,textAlign=TextAlign.Start) } } } }
      else { item { Text("فهرس السور",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=c.onBackground,modifier=Modifier.padding(vertical=5.dp)) }; if(filtered.isEmpty()) item { Text("لا توجد سورة بهذا الاسم أو الرقم",fontSize=12.sp,color=c.onSurfaceVariant) }; items(filtered) { n -> Card(Modifier.fillMaxWidth().clickable { onOpenSurah(n) },shape=androidx.compose.foundation.shape.RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant.copy(alpha=.55f))) { Row(Modifier.fillMaxWidth().padding(horizontal=15.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) { Box(Modifier.size(44.dp).background(c.primary,androidx.compose.foundation.shape.RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Text(toArabic(n),fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary)}; Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) { Text(SURAH_NAMES[n-1],fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.onSurface); Text("${toArabic(repo.ayahCount(n))} آية",fontSize=10.sp,color=c.onSurfaceVariant) }; Text("‹",fontSize=27.sp,color=c.secondary,fontWeight=FontWeight.Bold) } } } }
      item { Text("نص القرآن بالرسم العثماني • مصدر النص موضح داخل التطبيق",fontSize=9.sp,color=c.onSurfaceVariant,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth().padding(top=12.dp)) }
    }
  }
}