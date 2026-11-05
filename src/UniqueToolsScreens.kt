package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import kotlin.random.Random

private data class QuizQuestion(val prompt:String,val options:List<String>,val answer:Int,val note:String)

@Composable
fun QuranQuizScreen(onBack:()->Unit) {
 val c=MaterialTheme.colorScheme
 val qs=listOf(
  QuizQuestion("كم عدد سور القرآن؟",listOf("110","112","114","116"),2,"عدد السور 114."),
  QuizQuestion("ما أطول سورة؟",listOf("البقرة","آل عمران","النساء","المائدة"),0,"سورة البقرة أطول سور القرآن."),
  QuizQuestion("ما أقصر سورة؟",listOf("العصر","الكوثر","الإخلاص","النصر"),1,"سورة الكوثر أقصرها من حيث عدد الآيات."),
  QuizQuestion("في أي شهر نزل القرآن؟",listOf("محرم","رجب","رمضان","شوال"),2,"نزل القرآن في شهر رمضان."),
  QuizQuestion("ما السورة التي لا تبدأ بالبسملة؟",listOf("التوبة","يونس","هود","الأنفال"),0,"سورة التوبة لا تبدأ بالبسملة."),
  QuizQuestion("ما السورة المعروفة بأم الكتاب؟",listOf("البقرة","الفاتحة","يس","الملك"),1,"من أسماء الفاتحة أم الكتاب."),
  QuizQuestion("كم جزءًا في القرآن؟",listOf("20","25","30","40"),2,"القرآن ثلاثون جزءًا."),
  QuizQuestion("ما السورة التي تعدل ثلث القرآن؟",listOf("الفلق","الناس","الإخلاص","الكافرون"),2,"ورد ذلك في فضل سورة الإخلاص.")
 )
 var index by remember { mutableIntStateOf(0) }
 var selected by remember { mutableIntStateOf(-1) }
 var score by remember { mutableIntStateOf(0) }
 var finished by remember { mutableStateOf(false) }
 Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
  TextButton(onClick=onBack){Text("رجوع")}
  Text("المسابقة القرآنية",fontSize=25.sp,fontWeight=FontWeight.Black,color=c.primary)
  Text("السؤال " + (index+1) + " من " + qs.size,color=c.onSurfaceVariant)
  LinearProgressIndicator(progress={(index+(if(selected>=0) 1 else 0)).toFloat()/qs.size},modifier=Modifier.fillMaxWidth())
  if(!finished) {
   Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(22.dp)) {
    Column(Modifier.padding(17.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
     Text(qs[index].prompt,fontSize=20.sp,fontWeight=FontWeight.Bold)
     qs[index].options.forEachIndexed { n,opt ->
      OutlinedButton(onClick={if(selected<0){selected=n;if(n==qs[index].answer)score++}},enabled=selected<0,modifier=Modifier.fillMaxWidth()){Text(opt,modifier=Modifier.fillMaxWidth(),textAlign=TextAlign.Start)}
     }
     if(selected>=0) {
      Text((if(selected==qs[index].answer)"صحيح. " else "الإجابة: "+qs[index].options[qs[index].answer]+". ")+qs[index].note,color=c.primary)
      Button(onClick={if(index==qs.lastIndex)finished=true else index++;selected=-1},modifier=Modifier.fillMaxWidth()){Text(if(index==qs.lastIndex)"عرض النتيجة" else "السؤال التالي")}
     }
    }
   }
  } else {
   Card(colors=CardDefaults.cardColors(containerColor=c.primaryContainer),shape=RoundedCornerShape(22.dp)) {
    Column(Modifier.fillMaxWidth().padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)) {
     Text("نتيجتك",fontWeight=FontWeight.Bold)
     Text(score.toString()+" / "+qs.size,fontSize=42.sp,fontWeight=FontWeight.Black,color=c.primary)
     Button(onClick={index=0;selected=-1;score=0;finished=false}){Text("إعادة المسابقة")}
    }
   }
  }
 }
}

@Composable
fun RandomAyahScreen(repo:QuranRepository,onBack:()->Unit) {
 val c=MaterialTheme.colorScheme
 var surah by remember { mutableIntStateOf(1) }
 var ayah by remember { mutableIntStateOf(1) }
 var note by remember { mutableStateOf("") }
 fun choose(){surah=Random.nextInt(1,repo.surahCount()+1);ayah=Random.nextInt(1,repo.ayahCount(surah)+1)}
 LaunchedEffect(Unit){choose()}
 Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
  TextButton(onClick=onBack){Text("رجوع")}
  Text("آية للتدبر",fontSize=25.sp,fontWeight=FontWeight.Black,color=c.primary)
  Text("اقرأ الآية بتأنٍ، ثم دوّن فائدة بعد الرجوع إلى تفسير موثوق.")
  Card(colors=CardDefaults.cardColors(containerColor=c.primaryContainer),shape=RoundedCornerShape(23.dp)) {
   Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(13.dp)) {
    Text(SURAH_NAMES[surah-1]+" • الآية "+toArabic(ayah),fontWeight=FontWeight.Bold,color=c.primary)
    Text("﴿ "+repo.ayah(surah,ayah)+" ﴾",fontSize=23.sp,lineHeight=42.sp,textAlign=TextAlign.Center,color=c.onPrimaryContainer)
    Button(onClick={choose()}){Text("آية أخرى")}
   }
  }
  OutlinedTextField(value=note,onValueChange={note=it},modifier=Modifier.fillMaxWidth(),minLines=4,label={Text("فائدتي أو سؤالي للتدبر")})
  Text("لا تعتمد على الخاطر وحده في تفسير الآية؛ ارجع إلى تفسير معتمد.",fontSize=12.sp,color=c.error)
 }
}

@Composable
fun MemorizationChallengeScreen(repo:QuranRepository,prefs:AppPrefs,onBack:()->Unit) {
 val c=MaterialTheme.colorScheme
 var surahInput by remember { mutableStateOf("1") }
 var ayahInput by remember { mutableStateOf("1") }
 val surah=(surahInput.toIntOrNull()?:1).coerceIn(1,repo.surahCount())
 val ayah=(ayahInput.toIntOrNull()?:1).coerceIn(1,repo.ayahCount(surah))
 var revealed by remember { mutableStateOf(false) }
 var tries by remember { mutableIntStateOf(prefs.toolInt("challenge_tries")) }
 var mastered by remember { mutableIntStateOf(prefs.toolInt("challenge_mastered")) }
 Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
  TextButton(onClick=onBack){Text("رجوع")}
  Text("اختبر حفظك",fontSize=25.sp,fontWeight=FontWeight.Black,color=c.primary)
  Text("استحضر الآية من حفظك قبل كشف النص.")
  OutlinedTextField(value=surahInput,onValueChange={surahInput=it.filter(Char::isDigit).take(3);revealed=false},modifier=Modifier.fillMaxWidth(),label={Text("رقم السورة")})
  Text("السورة: "+SURAH_NAMES[surah-1])
  OutlinedTextField(value=ayahInput,onValueChange={ayahInput=it.filter(Char::isDigit).take(4);revealed=false},modifier=Modifier.fillMaxWidth(),label={Text("رقم الآية")})
  Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(22.dp)) {
   Column(Modifier.fillMaxWidth().padding(17.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
    Text(SURAH_NAMES[surah-1]+" • الآية "+toArabic(ayah),fontWeight=FontWeight.Bold,color=c.primary)
    if(revealed) Text(repo.ayah(surah,ayah),fontSize=22.sp,lineHeight=40.sp,textAlign=TextAlign.Center) else Text("حاول تذكّر الآية ثم اكشفها للمراجعة.",textAlign=TextAlign.Center)
    Button(onClick={revealed=!revealed},modifier=Modifier.fillMaxWidth()){Text(if(revealed)"إخفاء الآية" else "كشف الآية")}
    if(revealed) Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
     OutlinedButton(onClick={tries++;prefs.setToolInt("challenge_tries",tries);revealed=false},modifier=Modifier.weight(1f)){Text("أحتاج مراجعة")}
     Button(onClick={tries++;mastered++;prefs.setToolInt("challenge_tries",tries);prefs.setToolInt("challenge_mastered",mastered);revealed=false},modifier=Modifier.weight(1f)){Text("أتقنتها")}
    }
   }
  }
  Text("المحاولات: "+tries+" • أتقنت: "+mastered,color=c.primary,fontWeight=FontWeight.Bold)
 }
}

@Composable
fun KhatmahPlannerScreen(prefs:AppPrefs,onBack:()->Unit) {
 val c=MaterialTheme.colorScheme
 var input by remember { mutableStateOf(prefs.toolInt("planner_pages",20).toString()) }
 val pages=(input.toIntOrNull()?:1).coerceIn(1,604)
 val days=(604+pages-1)/pages
 val finishDate=LocalDate.now().plusDays(days.toLong())
 Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
  TextButton(onClick=onBack){Text("رجوع")}
  Text("مخطط الختمة",fontSize=25.sp,fontWeight=FontWeight.Black,color=c.primary)
  Text("حدد عدد الصفحات التي تستطيع قراءتها يوميًا.")
  OutlinedTextField(value=input,onValueChange={input=it.filter(Char::isDigit).take(3)},modifier=Modifier.fillMaxWidth(),label={Text("صفحة يوميًا")})
  Card(colors=CardDefaults.cardColors(containerColor=c.primaryContainer),shape=RoundedCornerShape(22.dp)) {
   Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
    Text("604 صفحة تقريبًا")
    Text(days.toString()+" يومًا",fontSize=40.sp,fontWeight=FontWeight.Black,color=c.primary)
    Text("موعد الإكمال التقريبي: "+finishDate.toString())
    Text("وردك اليومي: "+pages+" صفحة")
   }
  }
  Button(onClick={prefs.setToolInt("planner_pages",pages);input=pages.toString()},modifier=Modifier.fillMaxWidth()){Text("حفظ هدفي")}
  Text("الحساب تقريبي ويعتمد على مصحف من 604 صفحات.",fontSize=12.sp,color=c.onSurfaceVariant)
 }
}
