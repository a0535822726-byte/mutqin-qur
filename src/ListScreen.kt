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
    Row(Modifier.fillMaxWidth().padding(horizontal=19.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically) {
      Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)) {
        Text("المصحف",fontSize=25.sp,fontWeight=FontWeight.Black,color=c.onBackground)
        Text("فهرس السور والآيات",fontSize=10.sp,color=c.onSurfaceVariant)
      }
      Surface(shape=androidx.compose.foundation.shape.CircleShape,color=c.surface,border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant)) {
        Switch(checked=dark,onCheckedChange=onDark,modifier=Modifier.padding(horizontal=5.dp))
      }
    }
    Card(Modifier.fillMaxWidth().padding(horizontal=16.dp),shape=androidx.compose.foundation.shape.RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=c.primary)) {
      Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
          Text("الْقُرْآنُ الْكَرِيم",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=1.5.sp)
          Spacer(Modifier.weight(1f)); Text("۞",fontSize=32.sp,color=c.secondary)
        }
        Text("وَرْدُكَ يبدأ من هنا",fontSize=25.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
        Text("١١٤ سورة • اختر سورة أو ابحث عن آية لتصل إليها مباشرة.",fontSize=11.sp,lineHeight=19.sp,color=c.onPrimary.copy(alpha=.78f))
        if(last!=null) Surface(Modifier.fillMaxWidth().clickable { onResume(last.first,last.second,last.third) },shape=androidx.compose.foundation.shape.RoundedCornerShape(16.dp),color=c.onPrimary.copy(alpha=.10f)) {
          Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
              Text("تابع من حيث وصلت",fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.secondary)
              Text("سورة ${SURAH_NAMES[last.first-1]} • الآية ${toArabic(last.second)}",fontSize=11.sp,color=c.onPrimary)
            }
            Text("←",fontSize=22.sp,color=c.onPrimary)
          }
        }
      }
    }
    OutlinedTextField(value=query,onValueChange={query=it},placeholder={Text("اسم السورة أو كلمات من آية",fontSize=12.sp)},leadingIcon={Text("⌕",fontSize=25.sp,color=c.secondary)},singleLine=true,shape=androidx.compose.foundation.shape.RoundedCornerShape(17.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=c.secondary,unfocusedBorderColor=c.outlineVariant,focusedContainerColor=c.surface,unfocusedContainerColor=c.surface),modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=13.dp))
    LazyColumn(contentPadding=PaddingValues(start=16.dp,end=16.dp,top=2.dp,bottom=26.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
      if(searchText.length>=2) {
        item { Text("نتائج الآيات  •  ${toArabic(ayahMatches.size)}",fontSize=13.sp,fontWeight=FontWeight.Bold,color=c.onBackground,modifier=Modifier.padding(vertical=5.dp)) }
        if(ayahMatches.isEmpty()) item { Text("لم نعثر على آية تطابق البحث.",fontSize=12.sp,color=c.onSurfaceVariant) }
        items(ayahMatches) { match ->
          val (s,a)=match
          Card(Modifier.fillMaxWidth().clickable { onResume(s,a,a) },shape=androidx.compose.foundation.shape.RoundedCornerShape(19.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant)) {
            Column(Modifier.fillMaxWidth().padding(15.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
              Text("سورة ${SURAH_NAMES[s-1]}  •  الآية ${toArabic(a)}",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
              Text(repo.ayah(s,a),fontSize=19.sp,lineHeight=33.sp,color=c.onSurface,textAlign=TextAlign.Start)
            }
          }
        }
      } else {
        item { Text("السُّوَر",fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=c.onBackground,modifier=Modifier.padding(vertical=4.dp)) }
        if(filtered.isEmpty()) item { Text("لا توجد سورة بهذا الاسم أو الرقم",fontSize=12.sp,color=c.onSurfaceVariant) }
        items(filtered) { n ->
          Card(Modifier.fillMaxWidth().clickable { onOpenSurah(n) },shape=androidx.compose.foundation.shape.RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant.copy(alpha=.75f))) {
            Row(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) {
              Box(Modifier.size(43.dp).background(c.primary.copy(alpha=.09f),androidx.compose.foundation.shape.RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center) { Text(toArabic(n),fontSize=14.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary) }
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text(SURAH_NAMES[n-1],fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.onSurface)
                Text("${toArabic(repo.ayahCount(n))} آية",fontSize=10.sp,color=c.onSurfaceVariant)
              }
              Text("‹",fontSize=26.sp,color=c.secondary,fontWeight=FontWeight.Bold)
            }
          }
        }
      }
      item { Text("نص القرآن بالرسم العثماني • مصدر النص موضح داخل التطبيق",fontSize=9.sp,color=c.onSurfaceVariant,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth().padding(top=12.dp)) }
    }
  }
}
