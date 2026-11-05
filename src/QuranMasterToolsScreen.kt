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
import kotlin.math.ceil

private data class MasterTool(val id:String,val name:String,val hint:String,val icon:String)
private val MASTER_TOOLS = listOf(
 MasterTool("surah_progress","إنجاز السور","متابعة إتمام قراءة السور","📘"),
 MasterTool("juz_progress","إنجاز الأجزاء","متابعة الأجزاء الثلاثين","📚"),
 MasterTool("hizb_progress","إنجاز الأحزاب","متابعة الأحزاب الستين","🧭"),
 MasterTool("rub_progress","إنجاز الأرباع","متابعة أرباع الحزب","🧩"),
 MasterTool("pace_calc","حاسبة سرعة القراءة","احسب وقت الورد حسب سرعتك","⏱️"),
 MasterTool("finish_date","موعد ختمتك","تقدير موعد الإكمال","📅"),
 MasterTool("memor_target","هدف الحفظ","احسب الآيات اليومية المطلوبة","🎯"),
 MasterTool("review_cycle","دورة المراجعة","جدول مراجعة متدرج","🔁"),
 MasterTool("word_frequency","إحصاء كلمة قرآنية","اعرف مواضع كلمة في النص المتاح","🔎"),
 MasterTool("phrase_search","البحث عن عبارة","ابحث عن عبارة داخل الآيات","🔍"),
 MasterTool("letter_frequency","إحصاء حرف","احسب تكرار حرف في سورة","🔤"),
 MasterTool("ayah_stats","تحليل أطوال الآيات","أقصر وأطول آية ومتوسط الكلمات","📊"),
 MasterTool("ayah_locator","محدد الآية","اعرض آية برقم السورة والآية","📍"),
 MasterTool("neighbor_compare","مقارنة آيتين","اعرض آيتين للمقارنة","⚖️"),
 MasterTool("memory_check","اختبار الاستحضار","اكتب ما حفظته وقارنه بالنص","🧠"),
 MasterTool("session_split","تقسيم جلسة","وزع وقتك بين القراءة والحفظ والمراجعة","🗂️"),
 MasterTool("monthly_target","خطة شهرية","احسب هدفك خلال ثلاثين يوماً","🗓️"),
 MasterTool("reading_log","سجل القراءة","سجل صفحات ومدة جلسة القراءة","📖"),
 MasterTool("revision_backlog","قائمة المتراكم","رتب مواضع تحتاج مراجعة","📝"),
 MasterTool("personal_goals","أهدافي القرآنية","قائمة إنجازات مستقلة تحفظ محلياً","⭐")
)

@Composable
fun QuranMasterToolsScreen(repo:QuranRepository,prefs:AppPrefs,onBack:()->Unit) {
 var selected by remember { mutableStateOf<String?>(null) }
 val c=MaterialTheme.colorScheme
 Column(Modifier.fillMaxSize().background(c.background).verticalScroll(rememberScrollState()).padding(15.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
  Row(verticalAlignment=Alignment.CenterVertically) {
   TextButton(onClick=onBack){Text("رجوع")}
   Column(Modifier.weight(1f)) {
    Text("مركز الإتقان المتقدم",fontSize=23.sp,fontWeight=FontWeight.Black,color=c.primary)
    Text("20 أداة إضافية مستقلة للحفظ والقراءة والتحليل",fontSize=11.sp,color=c.onSurfaceVariant)
   }
   Text("🏅",fontSize=26.sp)
  }
  MASTER_TOOLS.chunked(2).forEach { pair ->
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
    pair.forEach { item ->
     Card(onClick={selected=item.id},modifier=Modifier.weight(1f).heightIn(min=105.dp),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c.surface)) {
      Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
       Text(item.icon,fontSize=22.sp)
       Text(item.name,fontWeight=FontWeight.Bold,fontSize=13.sp,color=c.onSurface)
       Text(item.hint,fontSize=10.sp,color=c.onSurfaceVariant)
      }
     }
    }
    if(pair.size==1) Spacer(Modifier.weight(1f))
   }
  }
 }
 selected?.let { MasterToolDetail(it,repo,prefs){selected=null} }
}

@Composable
private fun MasterToolDetail(id:String,repo:QuranRepository,prefs:AppPrefs,onClose:()->Unit) {
 val item=MASTER_TOOLS.first{it.id==id}
 val c=MaterialTheme.colorScheme
 var a by remember(id){mutableStateOf(prefs.toolText("master_${id}_a"))}
 var b by remember(id){mutableStateOf(prefs.toolText("master_${id}_b"))}
 var result by remember(id){mutableStateOf("")}
 val maxItems=when(id){"surah_progress"->114;"juz_progress"->30;"hizb_progress"->60;else->240}
 AlertDialog(onDismissRequest=onClose,title={Text(item.name,fontWeight=FontWeight.Black,color=c.primary)},text={
  Column(Modifier.fillMaxWidth().heightIn(max=520.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(9.dp)) {
   Text(item.hint,fontSize=11.sp,color=c.onSurfaceVariant)
   when(id) {
    "surah_progress","juz_progress","hizb_progress","rub_progress" -> {
     val label=when(id){"surah_progress"->"السورة";"juz_progress"->"الجزء";"hizb_progress"->"الحزب";else->"الربع"}
     val count=(1..maxItems).count{prefs.toolFlag("master_${id}_$it")}
     Text("المنجز: $count من $maxItems",fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary)
     LinearProgressIndicator(progress={count.toFloat()/maxItems},modifier=Modifier.fillMaxWidth())
     (1..maxItems).chunked(4).forEach { group ->
      Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
       group.forEach { n ->
        var checked by remember(id,n){mutableStateOf(prefs.toolFlag("master_${id}_$n"))}
        FilterChip(selected=checked,onClick={checked=!checked;prefs.setToolFlag("master_${id}_$n",checked)},label={Text("$label ${toArabic(n)}",fontSize=9.sp)})
       }
      }
     }
    }
    "pace_calc" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"عدد الصفحات")
     Field(b,{b=it.filter(Char::isDigit);prefs.setToolText("master_${id}_b",b)},"كم دقيقة تستغرق لكل صفحة؟")
     val pages=a.toIntOrNull()?:0;val mins=b.toIntOrNull()?:0
     Text(if(pages>0&&mins>0)"الوقت التقريبي: ${pages*mins} دقيقة." else "أدخل عدد الصفحات ومتوسط دقائق الصفحة.")
    }
    "finish_date" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"صفحات تقرؤها يومياً")
     val n=a.toIntOrNull()?:0
     if(n>0){val days=ceil(604.0/n).toInt();Text("بمعدل $n صفحة يومياً، تحتاج $days يوماً تقريباً.");Text("موعد الإكمال التقديري: ${LocalDate.now().plusDays(days.toLong())}")}
     else Text("أدخل عدد الصفحات اليومية.")
    }
    "memor_target" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"كم آية تريد حفظها؟")
     Field(b,{b=it.filter(Char::isDigit);prefs.setToolText("master_${id}_b",b)},"خلال كم يوم؟")
     val x=a.toIntOrNull()?:0;val y=b.toIntOrNull()?:0
     Text(if(x>0&&y>0)"هدفك اليومي: ${ceil(x.toDouble()/y).toInt()} آيات تقريباً، مع مراجعة ما سبق." else "أدخل العدد والمدة.")
    }
    "review_cycle" -> {
     Field(a,{a=it;prefs.setToolText("master_${id}_a",a)},"اسم السورة أو موضع الحفظ")
     Text("خطة مقترحة للمراجعة:")
     listOf("بعد الحفظ مباشرة","بعد يوم","بعد 3 أيام","بعد أسبوع","بعد أسبوعين","بعد شهر").forEachIndexed{n,label->
      var done by remember(id,n){mutableStateOf(prefs.toolFlag("master_${id}_$n"))}
      Row(verticalAlignment=Alignment.CenterVertically){Checkbox(done,{done=it;prefs.setToolFlag("master_${id}_$n",it)});Text(label)}
     }
     Text("الموضع: ${a.ifBlank{"لم تحدد موضعاً"}}")
    }
    "word_frequency","phrase_search" -> {
     Field(a,{a=it;prefs.setToolText("master_${id}_a",a)},if(id=="word_frequency")"الكلمة" else "العبارة")
     Button(onClick={
      if(a.trim().length<2) result="اكتب حرفين على الأقل."
      else {
       var hits=0;val found=mutableListOf<String>()
       for(s in 1..repo.surahCount()) for(v in 1..repo.ayahCount(s)) {
        val text=repo.ayah(s,v)
        val match=if(id=="word_frequency") normalizeAr(text).split(Regex("\\s+")).any{it.trim('،','؛','.',':','!','؟')==normalizeAr(a.trim())} else normalizeAr(text).contains(normalizeAr(a.trim()))
        if(match){hits++;if(found.size<30)found.add("${SURAH_NAMES[s-1]} • الآية ${toArabic(v)}: $text")}
       }
       result="عدد المواضع: $hits\\n"+found.joinToString("\\n")
      }
     },modifier=Modifier.fillMaxWidth()){Text("ابدأ البحث في النص المتاح")}
     if(result.isNotBlank())Text(result,fontSize=12.sp)
     Text("البحث نصّي وقد لا يطابق جميع صور الرسم والتشكيل.",fontSize=10.sp,color=c.onSurfaceVariant)
    }
    "letter_frequency" -> {
     Field(a,{a=it;prefs.setToolText("master_${id}_a",a)},"رقم السورة (1–114)")
     Field(b,{b=it;prefs.setToolText("master_${id}_b",b)},"الحرف المراد عده")
     val s=(a.toIntOrNull()?:0)
     if(s in 1..114&&b.isNotBlank())Text("تكرر الحرف «${b.first()}» ${repo.ayah((s),1).let { (1..repo.ayahCount(s)).sumOf { n -> repo.ayah(s,n).count { ch -> ch==b.first() } }} } مرة في ${SURAH_NAMES[s-1]}.")
     else Text("أدخل رقم سورة وحرفاً واحداً.")
    }
    "ayah_stats" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"رقم السورة")
     val s=(a.toIntOrNull()?:0)
     if(s in 1..114) {
      val counts=(1..repo.ayahCount(s)).map{n->repo.ayah(s,n).trim().split(Regex("\\s+")).count{it.isNotBlank()}}
      val min=counts.minOrNull()?:0;val max=counts.maxOrNull()?:0;val avg=counts.average()
      Text("${SURAH_NAMES[s-1]}: أقصر آية ${counts.indexOf(min)+1} (${min} كلمات تقريباً).")
      Text("أطول آية ${counts.indexOf(max)+1} ($max كلمات تقريباً).")
      Text("متوسط طول الآية: %.1f كلمة تقريباً".format(avg))
     } else Text("أدخل رقم سورة من 1 إلى 114.")
    }
    "ayah_locator" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"رقم السورة")
     Field(b,{b=it.filter(Char::isDigit);prefs.setToolText("master_${id}_b",b)},"رقم الآية")
     val s=a.toIntOrNull()?:0;val v=b.toIntOrNull()?:0
     if(s in 1..114&&v in 1..repo.ayahCount(s))Text("${SURAH_NAMES[s-1]} • الآية ${toArabic(v)}\\n${repo.ayah(s,v)}",fontSize=18.sp,lineHeight=32.sp)
     else Text("أدخل رقم سورة وآية صحيحين.")
    }
    "neighbor_compare" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"رقم السورة")
     Field(b,{b=it;prefs.setToolText("master_${id}_b",b)},"رقما الآيتين مفصولان بفاصلة")
     val s=a.toIntOrNull()?:0;val nums=b.split(",").mapNotNull{it.trim().toIntOrNull()}
     if(s in 1..114&&nums.size==2&&nums.all{it in 1..repo.ayahCount(s)}) {
      nums.forEach{n->Text("الآية ${toArabic(n)}: ${repo.ayah(s,n)}",fontSize=16.sp)}
      Text("هذه مقارنة نصية للعرض، وليست حكماً على تشابه المعنى.",fontSize=10.sp,color=c.onSurfaceVariant)
     } else Text("أدخل رقم سورة ورقمين مثل 5,8.")
    }
    "memory_check" -> {
     Field(a,{a=it;prefs.setToolText("master_${id}_a",a)},"رقم السورة")
     Field(b,{b=it;prefs.setToolText("master_${id}_b",b)},"رقم الآية")
     var answer by remember(id){mutableStateOf("")}
     OutlinedTextField(answer,{answer=it},modifier=Modifier.fillMaxWidth(),label={Text("اكتب الآية من حفظك")},minLines=3)
     val s=a.toIntOrNull()?:0;val v=b.toIntOrNull()?:0
     Button(onClick={result=if(s in 1..114&&v in 1..repo.ayahCount(s))if(normalizeAr(answer.trim())==normalizeAr(repo.ayah(s,v).trim()))"النص مطابق بعد تبسيط بعض أشكال الحروف." else "هناك اختلاف نصي؛ راجع النص المعروض في المصحف وصحح بنفسك." else "تحقق من رقم السورة والآية."},modifier=Modifier.fillMaxWidth()){Text("قارن إجابتي بالنص")}
     if(result.isNotBlank())Text(result)
     if(s in 1..114&&v in 1..repo.ayahCount(s))Text("النص المرجعي: ${repo.ayah(s,v)}",fontSize=13.sp)
     Text("المقارنة نصية فقط ولا تقيّم الأداء الصوتي أو أحكام التجويد.",fontSize=10.sp,color=c.onSurfaceVariant)
    }
    "session_split" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"مدة الجلسة بالدقائق")
     val n=a.toIntOrNull()?:0
     if(n>0){Text("قراءة 40%: ${n*40/100} دقيقة");Text("حفظ 30%: ${n*30/100} دقيقة");Text("مراجعة 20%: ${n*20/100} دقيقة");Text("راحة وتثبيت 10%: ${n-(n*90/100)} دقيقة")}
     else Text("أدخل مدة الجلسة.")
    }
    "monthly_target" -> {
     Field(a,{a=it.filter(Char::isDigit);prefs.setToolText("master_${id}_a",a)},"عدد الصفحات التي تريد إنجازها هذا الشهر")
     val n=a.toIntOrNull()?:0
     if(n>0)Text("هدفك اليومي لمدة 30 يوماً: ${ceil(n/30.0).toInt()} صفحة تقريباً.")
     else Text("أدخل هدف الصفحات الشهري.")
    }
    "reading_log","revision_backlog","personal_goals" -> {
     Field(a,{a=it;prefs.setToolText("master_${id}_a",a)},when(id){"reading_log"->"عدد الصفحات أو موضع القراءة";"revision_backlog"->"الموضع الذي يحتاج مراجعة";else->"اكتب هدفك"}) 
     Field(b,{b=it;prefs.setToolText("master_${id}_b",b)},when(id){"reading_log"->"مدة الجلسة بالدقائق";"revision_backlog"->"سبب أو ملاحظة";else->"تفاصيل الهدف (اختياري)"})
     Button(onClick={if(a.isNotBlank()){val old=prefs.toolText("master_${id}_list");prefs.setToolText("master_${id}_list",(old+"\\n• "+a+(if(b.isNotBlank())" — "+b else "")).takeLast(8000));result="تم الحفظ في سجل هذه الأداة."}},modifier=Modifier.fillMaxWidth()){Text("حفظ الإدخال")}
     Text(prefs.toolText("master_${id}_list").ifBlank{"لا توجد إدخالات محفوظة."},fontSize=12.sp)
    }
   }
  }
 },confirmButton={TextButton(onClick=onClose){Text("إغلاق")}})
}

@Composable
private fun Field(value:String,onValue:(String)->Unit,label:String) {
 OutlinedTextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth(),label={Text(label)},singleLine=true)
}
