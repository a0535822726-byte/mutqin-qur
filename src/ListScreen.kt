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
fun SurahListScreen(repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit, onOpenSurah: (Int) -> Unit, onResume: (Int, Int, Int) -> Unit, onOpenRecorder: () -> Unit, onOpenCheck: () -> Unit, onOpenTajweed: () -> Unit, onOpenStories: () -> Unit, onOpenReciters: () -> Unit, onOpenDailyTools: () -> Unit, onOpenTool: (String) -> Unit) {
 var query by remember { mutableStateOf("") }
 val last = remember { prefs.lastPosition() }
 val q = normalizeAr(query.trim())
 val filtered = remember(q) { (1..repo.surahCount()).filter { n -> q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q) } }
 val colors = MaterialTheme.colorScheme
 LazyColumn(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(), contentPadding=PaddingValues(horizontal=17.dp,vertical=14.dp), verticalArrangement=Arrangement.spacedBy(16.dp)) {
  item {
   Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=colors.surface),modifier=Modifier.fillMaxWidth()) {
    Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically) {
     Box(Modifier.size(58.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(colors.primary,colors.secondary))),contentAlignment=Alignment.Center){Text("مُ",color=colors.onPrimary,fontSize=29.sp,fontWeight=FontWeight.Black)}
     Spacer(Modifier.width(13.dp))
     Column(Modifier.weight(1f)){Text("مُتقن",fontSize=27.sp,fontWeight=FontWeight.Black,color=colors.onBackground);Text("رفيق رحلتك مع القرآن الكريم",fontSize=11.sp,color=colors.onSurfaceVariant)}
     FilledTonalIconButton(onClick={onDark(!dark)}){Text(if(dark)"☀" else "☾",fontSize=21.sp)}
    }
   }
  }
  item {
   Card(shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=colors.primary)) {
    Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(colors.primary,colors.secondary.copy(alpha=.88f),colors.primary.copy(alpha=.76f)))).padding(20.dp)) {
     Column(verticalArrangement=Arrangement.spacedBy(11.dp)) {
      Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",fontSize=24.sp,fontWeight=FontWeight.Black,color=colors.onPrimary)
      Text("اجعل لكل يوم وردًا، ولكل ورد أثرًا.",fontSize=12.sp,color=colors.onPrimary.copy(alpha=.9f))
      Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){HeroPill("١١٤ سورة",colors.onPrimary);HeroPill("قراءة • حفظ • مراجعة",colors.onPrimary)}
      if(last!=null){
       Button(onClick={onResume(last.first,last.second,last.third)},colors=ButtonDefaults.buttonColors(containerColor=colors.surface,contentColor=colors.primary),shape=RoundedCornerShape(15.dp)){Text("أكمل من حيث وصلت  ←",fontWeight=FontWeight.Bold)}
       Text("آخر موضع: ${SURAH_NAMES[last.first-1]} • الآية ${toArabic(last.second)}",fontSize=11.sp,color=colors.onPrimary)
      } else Button(onClick={onOpenSurah(1)},colors=ButtonDefaults.buttonColors(containerColor=colors.surface,contentColor=colors.primary),shape=RoundedCornerShape(15.dp)){Text("ابدأ رحلتك  ←",fontWeight=FontWeight.Bold)}
     }
    }
   }
  }
  item {
   HomeSectionHeading("مركز القرآن","أدوات القراءة والاستماع والتعلّم")
   Spacer(Modifier.height(9.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){
    FeatureCard("📖","المصحف","اقرأ وتدبّر",Modifier.weight(1f),colors){onOpenSurah(1)}
    FeatureCard("🎙","التسميع","سجّل تلاوتك",Modifier.weight(1f),colors,onOpenCheck)
   }
   Spacer(Modifier.height(9.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){
    FeatureCard("✨","مدرسة التجويد","دروس واختبارات",Modifier.weight(1f),colors,onOpenTajweed)
    FeatureCard("📚","مكتبة القصص","قصص وعبر",Modifier.weight(1f),colors,onOpenStories)
   }
   Spacer(Modifier.height(9.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){
    FeatureCard("🎧","مكتبة القرّاء","استمع للتلاوات",Modifier.weight(1f),colors,onOpenReciters)
    FeatureCard("🎚","استديو التسجيل","سجّل واستمع",Modifier.weight(1f),colors,onOpenRecorder)
   }
  }
  item {
   HomeSectionHeading("كل أداة في مكان مستقل","اختر الأداة التي تريدها مباشرة")
   Spacer(Modifier.height(9.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("سُبْحَانَ","التسبيح","عداد وهدف",Modifier.weight(1f),colors){onOpenTool("tasbih")}
    FeatureCard("الْحَمْدُ","التحميد","عداد وهدف",Modifier.weight(1f),colors){onOpenTool("tahmid")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("الله أكبر","التكبير","عداد وهدف",Modifier.weight(1f),colors){onOpenTool("takbir")}
    FeatureCard("أستغفر","الاستغفار","عداد وهدف",Modifier.weight(1f),colors){onOpenTool("istighfar")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("ﷺ","الصلاة على النبي","عداد وهدف",Modifier.weight(1f),colors){onOpenTool("salawat")}
    FeatureCard("📊","إنجاز اليوم","ملخص الأهداف",Modifier.weight(1f),colors){onOpenTool("achievement")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("📗","عداد القراءة","متابعة الآيات",Modifier.weight(1f),colors){onOpenTool("reading")}
    FeatureCard("🧠","الحفظ الجديد","هدف الحفظ",Modifier.weight(1f),colors){onOpenTool("memorization")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("🔁","مراجعة الحفظ","ثبّت محفوظك",Modifier.weight(1f),colors){onOpenTool("review")}
    FeatureCard("🕌","متابعة الصلاة","سجل صلواتك",Modifier.weight(1f),colors){onOpenTool("prayers")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("✓","مهام قرآنية","قائمة يومية",Modifier.weight(1f),colors){onOpenTool("tasks")}
    FeatureCard("📝","دفتر التدبر","ملاحظاتك",Modifier.weight(1f),colors){onOpenTool("notes")}
   }
   Spacer(Modifier.height(8.dp))
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("🌅","أذكار الصباح","قائمة متابعة",Modifier.weight(1f),colors){onOpenTool("morning")}
    FeatureCard("🌙","أذكار المساء","قائمة متابعة",Modifier.weight(1f),colors){onOpenTool("evening")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("📚","متابعة الختمة","الأجزاء المقروءة",Modifier.weight(1f),colors){onOpenTool("khatmah")}
    FeatureCard("🎧","ورد الاستماع","دقائق الاستماع",Modifier.weight(1f),colors){onOpenTool("listening")}
   }
   Spacer(Modifier.height(8.dp))
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FeatureCard("💡","فوائد اليوم","دفتر الفوائد",Modifier.weight(1f),colors){onOpenTool("reflection")}
    FeatureCard("🎯","أهداف الأسبوع","مهام أسبوعية",Modifier.weight(1f),colors){onOpenTool("goals")}
   }
   Card(onClick=onOpenDailyTools,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=colors.primaryContainer),modifier=Modifier.fillMaxWidth()){
    Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically){
     Column(Modifier.weight(1f)){Text("لوحة إدارة يومك",fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=colors.onPrimaryContainer);Text("افتح جميع العدادات والأهداف والمتابعات",fontSize=10.sp,color=colors.onPrimaryContainer)}
     Text("←",fontSize=23.sp,color=colors.onPrimaryContainer)
    }
   }
  }
  item {
   Card(shape=RoundedCornerShape(25.dp),colors=CardDefaults.cardColors(containerColor=colors.primaryContainer),modifier=Modifier.fillMaxWidth()) {
    Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
     Text("مركز الأدوات المتقدمة",fontSize=20.sp,fontWeight=FontWeight.Black,color=colors.onPrimaryContainer)
     Text("20 أداة إضافية للحفظ والمراجعة والتجويد والمتابعة",fontSize=11.sp,color=colors.onPrimaryContainer)
     Text("كل أداة تفتح صفحة مستقلة وتحفظ تقدمك على الجهاز",fontSize=10.sp,color=colors.onPrimaryContainer.copy(alpha=.8f))
    }
   }
   val extraTools=listOf(
    Triple("repeat","🔂","تكرار الآية"),Triple("pages","📄","عداد الصفحات"),
    Triple("juz","📚","متابعة الأجزاء"),Triple("surahfinish","🏁","إنجاز السور"),
    Triple("revisionrounds","🔁","جولات المراجعة"),Triple("listenminutes","🎧","دقائق الاستماع"),
    Triple("makharij","🗣","تدريب المخارج"),Triple("noonrules","نّ","أحكام النون"),
    Triple("meemrules","مّ","أحكام الميم"),Triple("maddpractice","〰","تدريب المدود"),
    Triple("waqf","⏸","علامات الوقف"),Triple("mistakes","✍","دفتر الأخطاء"),
    Triple("questions","❓","أسئلة للمعلم"),Triple("dua","🤲","دفتر الدعاء"),
    Triple("weeklyplan","🗓","خطة الأسبوع"),Triple("dailyreview","☑","مراجعة اليوم"),
    Triple("sessionlog","🎙","سجل الجلسات"),Triple("reflection2","💡","خواطر قرآنية"),
    Triple("readingstreak","🔥","أيام القراءة"),Triple("memorystreak","🧠","أيام الحفظ")
   )
   extraTools.chunked(2).forEach { pair ->
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
     pair.forEach { tool -> FeatureCard(tool.second,tool.third,"أداة مستقلة",Modifier.weight(1f),colors){onOpenTool(tool.first)} }
     if(pair.size==1) Spacer(Modifier.weight(1f))
    }
   }
  }
  item {
   HomeSectionHeading("فهرس السور","ابحث باسم السورة أو رقمها")
   OutlinedTextField(value=query,onValueChange={query=it},label={Text("اسم السورة أو رقمها")},leadingIcon={Text("⌕",fontSize=22.sp)},singleLine=true,shape=RoundedCornerShape(17.dp),modifier=Modifier.fillMaxWidth())
  }
  if(filtered.isEmpty())item{Text("لا توجد سورة مطابقة لبحثك",modifier=Modifier.fillMaxWidth(),textAlign=TextAlign.Center)}
  items(filtered){n->
   Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=colors.surface),modifier=Modifier.fillMaxWidth().clickable{onOpenSurah(n)}){
    Row(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically){
     Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(colors.primary.copy(alpha=.10f)),contentAlignment=Alignment.Center){Text(toArabic(n),color=colors.primary,fontWeight=FontWeight.Bold,fontSize=15.sp)}
     Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(SURAH_NAMES[n-1],fontSize=16.sp,fontWeight=FontWeight.Bold,color=colors.onSurface);Text("عدد الآيات: ${toArabic(repo.ayahCount(n))}",fontSize=10.sp,color=colors.onSurfaceVariant)};Text("فتح  ›",color=colors.primary,fontSize=12.sp,fontWeight=FontWeight.Bold)
    }
   }
  }
  item{Text("تنبيه: جلسة التسميع الحالية تسجّل صوتك وتتيح الاستماع إليه، ولا تصدر حكمًا آليًا على التجويد.",fontSize=10.sp,textAlign=TextAlign.Center,color=colors.onSurfaceVariant,modifier=Modifier.fillMaxWidth().padding(top=5.dp))}
 }
}

@Composable private fun HomeSectionHeading(title:String,subtitle:String){
 Column(verticalArrangement=Arrangement.spacedBy(3.dp)){Text(title,fontSize=19.sp,fontWeight=FontWeight.Black,color=MaterialTheme.colorScheme.onBackground);Text(subtitle,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}
}

@Composable private fun HeroPill(label:String,color:androidx.compose.ui.graphics.Color){Surface(color=color.copy(alpha=.15f),shape=CircleShape){Text(label,color=color,fontSize=10.sp,modifier=Modifier.padding(horizontal=10.dp,vertical=7.dp))}}

@Composable private fun FeatureCard(icon:String,title:String,subtitle:String,modifier:Modifier=Modifier,colors:ColorScheme=MaterialTheme.colorScheme,onClick:()->Unit){
 Card(onClick=onClick,shape=RoundedCornerShape(21.dp),colors=CardDefaults.cardColors(containerColor=colors.surface),modifier=modifier.heightIn(min=112.dp)){
  Column(Modifier.fillMaxWidth().padding(13.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
   Box(Modifier.size(39.dp).clip(RoundedCornerShape(13.dp)).background(colors.primary.copy(alpha=.12f)),contentAlignment=Alignment.Center){Text(icon,fontSize=20.sp,color=colors.primary)}
   Text(title,fontWeight=FontWeight.ExtraBold,fontSize=13.sp,color=colors.onSurface)
   Text(subtitle,fontSize=10.sp,color=colors.onSurfaceVariant)
  }
 }
}
