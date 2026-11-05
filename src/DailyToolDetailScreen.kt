package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

private data class ToolInfo(val title:String,val subtitle:String,val icon:String,val phrase:String,val kind:String,val goal:Int=33)
private val TOOL_INFO = mapOf(
 "tasbih" to ToolInfo("التسبيح","عداد مستقل لذكر سبحان الله","سُبْحَانَ","سبحان الله","counter",33),
 "tahmid" to ToolInfo("التحميد","عداد مستقل لذكر الحمد لله","الْحَمْدُ","الحمد لله","counter",33),
 "takbir" to ToolInfo("التكبير","عداد مستقل لذكر الله أكبر","الله أكبر","الله أكبر","counter",34),
 "istighfar" to ToolInfo("الاستغفار","استغفر الله وداوم على الذكر","أستغفر","أستغفر الله","counter",100),
 "salawat" to ToolInfo("الصلاة على النبي ﷺ","عداد مستقل للصلاة والسلام عليه","ﷺ","اللهم صل وسلم على نبينا محمد","counter",100),
 "achievement" to ToolInfo("إنجاز اليوم","قائمة إنجازات يومية قابلة للتحديد","🏆","إنجاز اليوم","tasks"),
 "reading" to ToolInfo("عداد القراءة","تابع عدد الآيات التي قرأتها","📖","آيات القراءة","counter"),
 "memorization" to ToolInfo("الحفظ الجديد","تابع الآيات الجديدة التي حفظتها","🧠","آيات الحفظ","counter"),
 "review" to ToolInfo("مراجعة الحفظ","ثبّت محفوظك بالمراجعة","🔁","آيات المراجعة","counter"),
 "prayers" to ToolInfo("متابعة الصلاة","سجل الصلوات الخمس","🕌","الصلوات الخمس","prayers"),
 "tasks" to ToolInfo("مهام قرآنية","قائمة مهامك اليومية","✓","مهامي القرآنية","tasks"),
 "notes" to ToolInfo("دفتر التدبر","احفظ خواطرك وفوائدك","📝","دفتر التدبر","notes"),
 "morning" to ToolInfo("أذكار الصباح","قائمة متابعة أذكار الصباح","🌅","أذكار الصباح","adhkar"),
 "evening" to ToolInfo("أذكار المساء","قائمة متابعة أذكار المساء","🌙","أذكار المساء","adhkar"),
 "khatmah" to ToolInfo("متابعة الختمة","علّم الأجزاء التي أتممت قراءتها","📚","ختمة القرآن","tasks"),
 "listening" to ToolInfo("ورد الاستماع","تابع جلسات الاستماع للتلاوات","🎧","دقائق الاستماع","counter"),
 "reflection" to ToolInfo("فوائد اليوم","دوّن فائدة قرآنية واحدة","💡","فائدة اليوم","notes"),
 "goals" to ToolInfo("أهداف الأسبوع","حدد إنجازاتك للأسبوع","🎯","إنجازات الأسبوع","tasks"),
 "repeat" to ToolInfo("تكرار الآية","سجل مرات تكرار الآية","🔂","مرات التكرار","counter",10),
 "pages" to ToolInfo("عداد الصفحات","تابع صفحات وردك","📄","الصفحات المقروءة","counter"),
 "juz" to ToolInfo("متابعة الأجزاء","قائمة الأجزاء الثلاثين","📚","أجزاء القرآن","tasks"),
 "surahfinish" to ToolInfo("إنجاز السور","حدد السور التي أتممت قراءتها","🏁","السور المكتملة","tasks"),
 "revisionrounds" to ToolInfo("جولات المراجعة","احسب جولات تثبيت الحفظ","🔁","جولات المراجعة","counter"),
 "listenminutes" to ToolInfo("دقائق الاستماع","سجل وقت الاستماع للتلاوة","🎧","دقائق الاستماع","counter"),
 "makharij" to ToolInfo("تدريب المخارج","قائمة متابعة مخارج الحروف","🗣","تدريب المخارج","tasks"),
 "noonrules" to ToolInfo("أحكام النون","راجع أحكام النون الساكنة والتنوين","نّ","أحكام النون","tasks"),
 "meemrules" to ToolInfo("أحكام الميم","راجع أحكام الميم الساكنة","مّ","أحكام الميم","tasks"),
 "maddpractice" to ToolInfo("تدريب المدود","تابع أنواع المد في دراستك","〰","تدريب المدود","tasks"),
 "waqf" to ToolInfo("علامات الوقف","راجع مواضع الوقف والابتداء","⏸","علامات الوقف","tasks"),
 "mistakes" to ToolInfo("دفتر الأخطاء","دوّن مواضع تحتاج إلى مراجعة","✍","مواضع المراجعة","notes"),
 "questions" to ToolInfo("أسئلة للمعلم","اجمع أسئلتك قبل حلقة القرآن","❓","أسئلتي","notes"),
 "dua" to ToolInfo("دفتر الدعاء","اكتب أدعيتك التي تريد تذكرها","🤲","دعائي","notes"),
 "weeklyplan" to ToolInfo("خطة الأسبوع","حدد مهامك القرآنية للأسبوع","🗓","خطة الأسبوع","tasks"),
 "dailyreview" to ToolInfo("مراجعة اليوم","قائمة مراجعة ختامية","☑","مراجعة اليوم","tasks"),
 "sessionlog" to ToolInfo("سجل الجلسات","دوّن ملخص جلسة التسميع","🎙","ملخص الجلسة","notes"),
 "reflection2" to ToolInfo("خواطر قرآنية","سجل فائدة تدبرتها","💡","خاطرتي","notes"),
 "readingstreak" to ToolInfo("أيام القراءة","سجل أيام المحافظة على الورد","🔥","أيام القراءة","counter"),
 "memorystreak" to ToolInfo("أيام الحفظ","سجل أيام الحفظ الجديد","🧠","أيام الحفظ","counter")
)

@Composable
fun DailyToolDetailScreen(prefs:AppPrefs, toolId:String, onBack:()->Unit) {
 val info=TOOL_INFO[toolId] ?: TOOL_INFO.getValue("tasbih")
 val colors=MaterialTheme.colorScheme
 val today=LocalDate.now().toString()
 val key="single_${toolId}_${today}"
 var count by remember(toolId,today) { mutableIntStateOf(prefs.toolInt(key)) }
 var goal by remember(toolId) { mutableIntStateOf(prefs.toolInt("goal_${toolId}",info.goal).coerceAtLeast(1)) }
 var goalText by remember { mutableStateOf(goal.toString()) }
 var note by remember(toolId,today) { mutableStateOf(prefs.toolText(key+"_note")) }
 val items=when(toolId) {
  "prayers" -> listOf("الفجر","الظهر","العصر","المغرب","العشاء")
  "morning" -> listOf("آية الكرسي","الإخلاص والمعوذتان","أصبحنا وأصبح الملك لله","اللهم بك أصبحنا","رضيت بالله ربًا")
  "evening" -> listOf("آية الكرسي","الإخلاص والمعوذتان","أمسينا وأمسى الملك لله","اللهم بك أمسينا","رضيت بالله ربًا")
  "tasks" -> listOf("قراءة ورد القرآن","حفظ آيات جديدة","مراجعة المحفوظ","الاستماع لتلاوة متقنة")
  "goals" -> listOf("قراءة يومية","حفظ جديد","مراجعة","استماع للتلاوة")
  "makharij" -> listOf("تدريب طرف اللسان","مراجعة الحروف الحلقية","تمييز الحروف المتقاربة","الاستماع لنموذج متقن")
  "noonrules" -> listOf("الإظهار","الإدغام بغنة","الإدغام بغير غنة","الإقلاب","الإخفاء")
  "meemrules" -> listOf("الإخفاء الشفوي","الإدغام الشفوي","الإظهار الشفوي")
  "maddpractice" -> listOf("المد الطبيعي","المد المتصل","المد المنفصل","المد اللازم")
  "waqf" -> listOf("مراجعة علامة الوقف اللازم","مراجعة الوقف الجائز","تحديد موضع استئناف مناسب","عرض المواضع على معلم")
  "weeklyplan" -> listOf("تحديد ورد القراءة","تحديد مقدار الحفظ","تحديد وقت المراجعة","جلسة استماع")
  "dailyreview" -> listOf("قرأت وردي","راجعت محفوظي","استمعت لتلاوة متقنة","دوّنت فائدة اليوم")
  "achievement" -> listOf("أتممت ورد القراءة","راجعت محفوظًا سابقًا","حفظت آيات جديدة","استمعت لتلاوة متقنة","دوّنت فائدة قرآنية","أنجزت أذكار اليوم")
  "khatmah", "juz" -> (1..30).map { "الجزء ${toArabic(it)}" }
  "surahfinish" -> SURAH_NAMES
  else -> emptyList()
 }
 var checks by remember(toolId,today) { mutableStateOf(items.associateWith { prefs.toolFlag("${key}_${it}") }) }
 Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
  Row(verticalAlignment=Alignment.CenterVertically) {
   TextButton(onClick=onBack) { Text("رجوع") }
   Column(Modifier.weight(1f)) { Text(info.title,fontSize=23.sp,fontWeight=FontWeight.Black,color=colors.primary); Text(info.subtitle,fontSize=11.sp,color=colors.onSurfaceVariant) }
   Text(info.icon,fontSize=27.sp)
  }
  Card(shape=RoundedCornerShape(25.dp),colors=CardDefaults.cardColors(containerColor=colors.primary)) {
   Column(Modifier.fillMaxWidth().padding(19.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(9.dp)) {
    Text(info.phrase,fontSize=22.sp,fontWeight=FontWeight.Bold,color=colors.onPrimary,textAlign=TextAlign.Center)
    Text(if(info.kind=="counter") count.toString() else "${checks.values.count{it}} / ${items.size}",fontSize=49.sp,fontWeight=FontWeight.Black,color=colors.onPrimary)
    if(info.kind=="counter") {
     Text("الهدف: $goal • المتبقي: ${(goal-count).coerceAtLeast(0)}",fontSize=12.sp,color=colors.onPrimary.copy(alpha=.88f))
     LinearProgressIndicator(progress={(count.toFloat()/goal).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),color=colors.onPrimary,trackColor=colors.onPrimary.copy(alpha=.2f))
     Button(onClick={count++;prefs.setToolInt(key,count)},modifier=Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(17.dp),colors=ButtonDefaults.buttonColors(containerColor=colors.surface,contentColor=colors.primary)){Text("＋  إضافة واحدة",fontSize=17.sp,fontWeight=FontWeight.Bold)}
     Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
      OutlinedButton(onClick={count=(count-1).coerceAtLeast(0);prefs.setToolInt(key,count)},modifier=Modifier.weight(1f)){Text("تراجع")}
      OutlinedButton(onClick={count=0;prefs.setToolInt(key,0)},modifier=Modifier.weight(1f)){Text("تصفير")}
     }
     Row(verticalAlignment=Alignment.CenterVertically) {
      OutlinedTextField(value=goalText,onValueChange={goalText=it.filter(Char::isDigit).take(5)},modifier=Modifier.weight(1f),singleLine=true,label={Text("الهدف")})
      Button(onClick={goal=goalText.toIntOrNull()?.coerceIn(1,99999)?:goal;prefs.setToolInt("goal_${toolId}",goal);goalText=goal.toString()}){Text("حفظ")}
     }
    } else {
     Text("ضع علامة عند إتمام كل بند",fontSize=12.sp,color=colors.onPrimary.copy(alpha=.9f))
     if (items.isNotEmpty()) {
      LinearProgressIndicator(
       progress={(checks.values.count { it }.toFloat()/items.size).coerceIn(0f,1f)},
       modifier=Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
       color=colors.onPrimary,
       trackColor=colors.onPrimary.copy(alpha=.2f)
      )
      Text("أنجزت ${checks.values.count { it }} من ${items.size}",fontSize=11.sp,color=colors.onPrimary)
     }
    }
   }
  }
  if(items.isNotEmpty()) {
   if (toolId == "khatmah" || toolId == "juz" || toolId == "surahfinish") {
    OutlinedTextField(
     value=note,
     onValueChange={note=it;prefs.setToolText(key+"_note",it)},
     modifier=Modifier.fillMaxWidth(),
     singleLine=true,
     label={Text(if(toolId=="surahfinish") "ابحث عن سورة" else "ملاحظة للختمة")}
    )
   }
   Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
     val visibleItems = if (toolId == "surahfinish" && note.isNotBlank()) items.filter { normalizeAr(it).contains(normalizeAr(note.trim())) } else items
     visibleItems.forEach { item ->
      Row(verticalAlignment=Alignment.CenterVertically) {
       Checkbox(checked=checks[item]==true,onCheckedChange={v->checks=checks.toMutableMap().apply{put(item,v)};prefs.setToolFlag("${key}_${item}",v)})
       Text(item,Modifier.weight(1f),fontWeight=FontWeight.SemiBold)
       if(checks[item]==true) Text("تم",color=colors.primary,fontSize=11.sp)
      }
      HorizontalDivider(color=colors.outlineVariant.copy(alpha=.5f))
     }
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
      Text("${checks.values.count { it }} / ${items.size}",fontSize=11.sp,color=colors.primary,fontWeight=FontWeight.Bold)
      TextButton(onClick={items.forEach{prefs.setToolFlag("${key}_${it}",false)};checks=items.associateWith{false}}){Text("إعادة ضبط")}
     }
    }
   }
  }
  if(info.kind=="notes") {
   OutlinedTextField(value=note,onValueChange={note=it;prefs.setToolText(key+"_note",it)},modifier=Modifier.fillMaxWidth(),minLines=6,label={Text("اكتب فائدتك أو تدبرك هنا")},shape=RoundedCornerShape(18.dp))
   Text("يُحفظ ما تكتبه تلقائيًا على جهازك.",fontSize=11.sp,color=colors.onSurfaceVariant)
  } else {
   Text("بيانات هذه الأداة محفوظة محليًا على جهازك لهذا اليوم.",fontSize=11.sp,color=colors.onSurfaceVariant)
  }
 }
}
