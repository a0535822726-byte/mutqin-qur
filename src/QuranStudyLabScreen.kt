// أدوات مستقلة لحفظ ملاحظات المتعلم محلياً على الجهاز.
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class LabTool(val id:String,val title:String,val subtitle:String,val icon:String)
private val LAB_TOOLS = listOf(
 LabTool("bookmark_index","فهرس علامات الوقف","اعرض مواضعك المحفوظة","🔖"),
 LabTool("page_calculator","حاسبة ختمة الصفحات","احسب عدد الأيام حسب وردك","🧮"),
 LabTool("verse_calculator","حاسبة الحفظ","احسب مدة حفظ مقدار محدد","📐"),
 LabTool("vocabulary","قاموسك القرآني","سجل كلمة ومعناها الذي تعلمته","📒"),
 LabTool("hard_verses","مواضع تحتاج تثبيتاً","دوّن مواضع الآيات الصعبة","📍"),
 LabTool("similar_verses","مقارنة المتشابهات","قارن نصين تكتبهما بنفسك","⚖️"),
 LabTool("teacher_questions","أسئلة للمعلم","اجمع أسئلتك في قائمة واحدة","❔"),
 LabTool("lesson_journal","دفتر الدروس","احفظ ملخص درس الحلقة","📝"),
 LabTool("weekly_schedule","جدول الأسبوع","تابع مهامك خلال سبعة أيام","🗓️"),
 LabTool("self_review","تقييم ذاتي","قيّم استعدادك للمراجعة","⭐"),
 LabTool("tajweed_cards","بطاقات التجويد","اقلب البطاقة لمراجعة التعريف","🃏"),
 LabTool("letters_practice","تدريب الحروف","تابع الحروف التي تدربت عليها","🔤"),
 LabTool("waqf_guide","دليل علامات الوقف","تعرف على أشهر العلامات","⏸️"),
 LabTool("reflection_prompts","أسئلة التدبر","اختر سؤالاً يساعدك على التأمل","💭"),
 LabTool("session_log","سجل جلسات القرآن","وثق مدة كل جلسة","⏱️"),
 LabTool("listening_log","سجل الاستماع","وثق القارئ ومدة الاستماع","🎧"),
 LabTool("halaqa_attendance","حضور الحلقة","سجل حضورك خلال الأسبوع","🏫"),
 LabTool("focus_planner","مخطط جلسة التركيز","قسم وقت الجلسة إلى مراحل","🎯"),
 LabTool("weekly_report","ملخص تقدمك","اجمع أرقام إنجازاتك الأسبوعية","📊"),
 LabTool("personal_glossary","مصطلحاتي","احفظ المصطلحات التي تتعلمها","📚")
)

@Composable
fun QuranStudyLabScreen(repo: QuranRepository, prefs: AppPrefs, onBack:()->Unit) {
 var selected by remember { mutableStateOf<String?>(null) }
 val colors=MaterialTheme.colorScheme
 Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
  Row(verticalAlignment=Alignment.CenterVertically) {
   TextButton(onClick=onBack){Text("رجوع")}
   Column(Modifier.weight(1f)) {
    Text("مختبر الدراسة القرآنية",fontSize=22.sp,fontWeight=FontWeight.Black,color=colors.primary)
    Text("20 أداة متنوعة للتعلم والتنظيم والمتابعة",fontSize=11.sp,color=colors.onSurfaceVariant)
   }
   Text("🧰",fontSize=26.sp)
  }
  LAB_TOOLS.chunked(2).forEach { pair ->
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
    pair.forEach { tool ->
     Card(onClick={selected=tool.id},modifier=Modifier.weight(1f).heightIn(min=105.dp),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
       Text(tool.icon,fontSize=23.sp)
       Text(tool.title,fontWeight=FontWeight.Bold,fontSize=13.sp,color=colors.onSurface)
       Text(tool.subtitle,fontSize=10.sp,color=colors.onSurfaceVariant)
      }
     }
    }
    if(pair.size==1) Spacer(Modifier.weight(1f))
   }
  }
 }
 selected?.let { id -> LabToolDetail(id,repo,prefs){selected=null} }
}

@Composable
private fun LabToolDetail(id:String,repo:QuranRepository,prefs:AppPrefs,onClose:()->Unit) {
 val tool=LAB_TOOLS.first{it.id==id}
 val c=MaterialTheme.colorScheme
 var a by remember(id){mutableStateOf(prefs.toolText("lab_${id}_a"))}
 var b by remember(id){mutableStateOf(prefs.toolText("lab_${id}_b"))}
 var result by remember(id){mutableStateOf("")}
 var revealed by remember(id){mutableStateOf(false)}
 val week=listOf("الأحد","الاثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت")
 val letters=listOf("ء","ا","ب","ت","ث","ج","ح","خ","د","ذ","ر","ز","س","ش","ص","ض","ط","ظ","ع","غ","ف","ق","ك","ل","م","ن","ه","و","ي")
 val prompts=listOf("ما المعنى العام للآيات التي قرأتها؟","ما الأمر أو الخلق الذي تدعوني الآيات للتفكر فيه؟","ما الكلمة التي تحتاج أن تبحث عن معناها؟","ما الفائدة التي ستطبقها اليوم؟","ما السؤال الذي تريد عرضه على معلمك؟")
 AlertDialog(onDismissRequest=onClose, title={Text(tool.title,fontWeight=FontWeight.Black,color=c.primary)}, text={
  Column(Modifier.fillMaxWidth().heightIn(max=510.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(9.dp)) {
   Text(tool.subtitle,fontSize=11.sp,color=c.onSurfaceVariant)
   when(id) {
    "bookmark_index" -> {
     Text("علاماتك المحفوظة في كل سورة:",fontWeight=FontWeight.Bold)
     val saved=(1..repo.surahCount()).mapNotNull{s->prefs.bookmark(s)?.let{ "${SURAH_NAMES[s-1]} — الآية ${toArabic(it)}" }}
     if(saved.isEmpty()) Text("لا توجد علامات محفوظة حتى الآن. افتح سورة واحفظ موضعاً من داخل المصحف.")
     else saved.forEach{Text("• $it")}
    }
    "page_calculator" -> {
     OutlinedTextField(a,{a=it.filter(Char::isDigit);prefs.setToolText("lab_${id}_a",a)},label={Text("كم صفحة تقرأ يومياً؟")},singleLine=true)
     val n=a.toIntOrNull()?:0
     Text(if(n>0) "بحساب تقريبي على مصحف من 604 صفحات: ${(604+n-1)/n} يوماً لإتمام القراءة." else "أدخل عدداً صحيحاً أكبر من صفر.")
    }
    "verse_calculator" -> {
     OutlinedTextField(a,{a=it.filter(Char::isDigit);prefs.setToolText("lab_${id}_a",a)},label={Text("عدد الآيات المطلوب حفظها")},singleLine=true)
     OutlinedTextField(b,{b=it.filter(Char::isDigit);prefs.setToolText("lab_${id}_b",b)},label={Text("كم آية في اليوم؟")},singleLine=true)
     val x=a.toIntOrNull()?:0;val y=b.toIntOrNull()?:0
     Text(if(x>0&&y>0) "تحتاج تقريباً ${(x+y-1)/y} يوماً، مع تخصيص وقت للمراجعة." else "أدخل العددين لحساب المدة.")
    }
    "vocabulary","personal_glossary","hard_verses","teacher_questions","lesson_journal" -> {
     OutlinedTextField(a,{a=it;prefs.setToolText("lab_${id}_a",a)},label={Text(when(id){"vocabulary"->"الكلمة القرآنية";"personal_glossary"->"المصطلح";"hard_verses"->"السورة ورقم الآية";"teacher_questions"->"سؤالك للمعلم";else->"عنوان الدرس أو ملخصه"})},modifier=Modifier.fillMaxWidth(),minLines=2)
     OutlinedTextField(b,{b=it;prefs.setToolText("lab_${id}_b",b)},label={Text(when(id){"vocabulary"->"المعنى الذي تعلمته";"personal_glossary"->"شرح المصطلح";"hard_verses"->"ما الذي تريد تثبيته؟";"teacher_questions"->"تفاصيل إضافية (اختياري)";else->"ملاحظات الدرس"})},modifier=Modifier.fillMaxWidth(),minLines=2)
     Button(onClick={if(a.isNotBlank()){val old=prefs.toolText("lab_${id}_list");prefs.setToolText("lab_${id}_list",(old+"\n• "+a+(if(b.isNotBlank())" — "+b else "")).takeLast(8000));result="تمت الإضافة إلى سجلك."}},modifier=Modifier.fillMaxWidth()){Text("حفظ في السجل")}
     Text(prefs.toolText("lab_${id}_list").ifBlank{"لا توجد مدخلات محفوظة بعد."},fontSize=12.sp)
    }
    "similar_verses" -> {
     OutlinedTextField(a,{a=it;prefs.setToolText("lab_${id}_a",a)},label={Text("النص الأول")},modifier=Modifier.fillMaxWidth(),minLines=3)
     OutlinedTextField(b,{b=it;prefs.setToolText("lab_${id}_b",b)},label={Text("النص الثاني")},modifier=Modifier.fillMaxWidth(),minLines=3)
     Button(onClick={result=if(a.isBlank()||b.isBlank())"أدخل النصين أولاً." else "الأول: ${a.length} حرفاً • الثاني: ${b.length} حرفاً. راجع مواضع الاختلاف بنفسك مع المصحف."},modifier=Modifier.fillMaxWidth()){Text("مقارنة النصين")}
     if(result.isNotBlank())Text(result)
    }
    "weekly_schedule","halaqa_attendance" -> {
     Text(if(id=="weekly_schedule")"ضع علامة عند إنجاز وردك في اليوم." else "سجل أيام حضور الحلقة.")
     week.forEachIndexed{index,day->
      var checked by remember(id,index){mutableStateOf(prefs.toolFlag("lab_${id}_$index"))}
      Row(verticalAlignment=Alignment.CenterVertically){
       Checkbox(checked=checked,onCheckedChange={v->checked=v;prefs.setToolFlag("lab_${id}_$index",v)})
       Text(day,Modifier.weight(1f))
       if(checked)Text(if(id=="weekly_schedule")"أُنجز" else "حاضر",color=c.primary,fontSize=11.sp)
      }
     }
    }
    "self_review" -> {
     listOf("ضبط الحفظ","سلامة النطق","تطبيق أحكام التجويد","الثبات دون تلقين").forEachIndexed{index,label->
      var score by remember(index){mutableFloatStateOf(prefs.toolInt("lab_self_$index",3).toFloat())}
      Text("$label: ${score.toInt()} من 5",fontWeight=FontWeight.SemiBold)
      Slider(value=score,onValueChange={score=it},valueRange=1f..5f,steps=3,onValueChangeFinished={prefs.setToolInt("lab_self_$index",score.toInt())})
     }
     Text("هذا تقييم شخصي للتوجيه، وليس تصحيحاً آلياً للتلاوة.")
    }
    "tajweed_cards" -> {
     val cards=listOf("الإظهار الحلقي" to "إظهار النون الساكنة أو التنوين عند حروف الحلق.","الإدغام" to "إدخال حرف ساكن في حرف متحرك ليصيرا حرفاً واحداً مشدداً.","الإقلاب" to "قلب النون الساكنة أو التنوين ميماً مخفاة عند الباء.","الإخفاء" to "النطق بالنون الساكنة أو التنوين بصفة بين الإظهار والإدغام.")
     cards.forEachIndexed{index,pair->
      var show by remember(index){mutableStateOf(false)}
      Card(onClick={show=!show},colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),modifier=Modifier.fillMaxWidth()){
       Column(Modifier.padding(10.dp)){Text(pair.first,fontWeight=FontWeight.Bold);Text(if(show)pair.second else "اضغط لإظهار التعريف",fontSize=12.sp,color=c.onSurfaceVariant)}
      }
     }
    }
    "letters_practice" -> {
     Text("حدد الحروف التي تدربت على نطقها. لا يغني ذلك عن التلقي من معلم.")
     letters.chunked(7).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){row.forEach{letter->
      var done by remember(letter){mutableStateOf(prefs.toolFlag("lab_letter_$letter"))}
      Column(horizontalAlignment=Alignment.CenterHorizontally){Text(letter,fontSize=20.sp);Checkbox(checked=done,onCheckedChange={v->done=v;prefs.setToolFlag("lab_letter_$letter",v)})}
     }}}
    }
    "waqf_guide" -> {
     listOf("مـ" to "علامة الوقف اللازم في مواضعها بالمصحف.","لا" to "علامة تفيد النهي عن الوقف في موضعها.","ج" to "علامة جواز الوقف.","قلى" to "الوقف أولى.","صلى" to "الوصل أولى.").forEach{(mark,desc)->Row(verticalAlignment=Alignment.CenterVertically){Text(mark,fontSize=22.sp,fontWeight=FontWeight.Black,color=c.primary,modifier=Modifier.width(55.dp));Text(desc,fontSize=12.sp)}}
     Text("قد تختلف تفاصيل الضبط والعلامات بحسب طبعة المصحف؛ اتبع مصحفك المعتمد.",fontSize=10.sp,color=c.onSurfaceVariant)
    }
    "reflection_prompts" -> {
     Button(onClick={result=prompts.random()},modifier=Modifier.fillMaxWidth()){Text("أعطني سؤال تدبر")}
     Text(result.ifBlank{"اضغط لاختيار سؤال."},fontSize=16.sp,fontWeight=FontWeight.Bold)
     Text("التدبر لا يعني الجزم بتفسير الآية من غير علم؛ ارجع إلى تفسير موثوق.",fontSize=10.sp,color=c.onSurfaceVariant)
    }
    "session_log","listening_log" -> {
     OutlinedTextField(a,{a=it;prefs.setToolText("lab_${id}_a",a)},label={Text(if(id=="session_log")"مدة الجلسة بالدقائق" else "اسم القارئ")},modifier=Modifier.fillMaxWidth())
     OutlinedTextField(b,{b=it;prefs.setToolText("lab_${id}_b",b)},label={Text(if(id=="session_log")"ماذا أنجزت؟" else "مدة الاستماع بالدقائق")},modifier=Modifier.fillMaxWidth())
     Button(onClick={if(a.isNotBlank()){val old=prefs.toolText("lab_${id}_list");prefs.setToolText("lab_${id}_list",(old+"\n• "+a+" — "+b).takeLast(8000));result="حُفظت الجلسة."}},modifier=Modifier.fillMaxWidth()){Text("حفظ الجلسة")}
     Text(prefs.toolText("lab_${id}_list").ifBlank{"لا توجد جلسات مسجلة."},fontSize=11.sp)
    }
    "focus_planner" -> {
     OutlinedTextField(a,{a=it.filter(Char::isDigit);prefs.setToolText("lab_${id}_a",a)},label={Text("مدة الجلسة بالدقائق")},singleLine=true)
     val mins=a.toIntOrNull()?:0
     if(mins>0) {
      val prep=mins*10/100
      val reading=mins*60/100
      val review=mins*30/100
      val notes=(mins-prep-reading-review).coerceAtLeast(0)
      Text("خطة مقترحة لجلسة من $mins دقيقة:")
      Text("• $prep دقيقة: تهيئة وتحديد هدف الجلسة")
      Text("• $reading دقيقة: قراءة أو حفظ بتركيز")
      Text("• $review دقيقة: مراجعة ما قرأت أو حفظت")
      Text("• $notes دقيقة: تثبيت الملاحظات وتحديد موضع المتابعة")
      Text("يمكنك تعديل التقسيم حسب طاقتك ووقت الحلقة.",fontSize=10.sp,color=c.onSurfaceVariant)
     } else Text("أدخل مدة الجلسة ليتم تقسيمها.")
    }
    "weekly_report" -> {
     val completed=(0..6).count{prefs.toolFlag("lab_weekly_schedule_$it")}
     val attendance=(0..6).count{prefs.toolFlag("lab_halaqa_attendance_$it")}
     Text("إنجاز الورد الأسبوعي: $completed من 7 أيام",fontSize=16.sp,fontWeight=FontWeight.Bold)
     LinearProgressIndicator(progress={completed/7f},modifier=Modifier.fillMaxWidth())
     Text("حضور الحلقة: $attendance من 7 أيام",fontSize=16.sp,fontWeight=FontWeight.Bold)
     LinearProgressIndicator(progress={attendance/7f},modifier=Modifier.fillMaxWidth())
     Text("هذا الملخص يعتمد على العلامات التي سجلتها في جدول الأسبوع والحضور.")
    }
   }
   if(result.isNotBlank() && id !in listOf("similar_verses","reflection_prompts","vocabulary","personal_glossary","hard_verses","teacher_questions","lesson_journal","session_log","listening_log")) Text(result,fontSize=11.sp,color=c.primary)
  }
 },confirmButton={TextButton(onClick=onClose){Text("إغلاق")}})
}
