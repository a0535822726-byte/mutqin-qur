package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeeniHomeScreen(dark:Boolean,onDark:(Boolean)->Unit,onQuran:()->Unit,onRecitation:()->Unit,onAdhkar:()->Unit,onTasbih:()->Unit,onLearning:()->Unit) {
  val c=MaterialTheme.colorScheme
  val hadithCount=hadithEntries.size
  val lessonCount=tajweedLessons.size
  val storyCount=quranStories.size
  LazyColumn(Modifier.fillMaxSize().background(Color(0xFF06130F)),contentPadding=PaddingValues(start=18.dp,end=18.dp,top=18.dp,bottom=30.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
    item {
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
        Column(verticalArrangement=Arrangement.spacedBy(3.dp)) {
          Text("مُتقن",fontSize=34.sp,fontWeight=FontWeight.Black,color=Color(0xFFE5C36A))
          Text("رحلتك إلى إتقان التلاوة والعلم",fontSize=12.sp,color=Color(0xFFB7C9C0))
        }
        Surface(shape=RoundedCornerShape(18.dp),color=Color(0xFF142B23),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFF426B58))) {
          Row(Modifier.padding(horizontal=10.dp,vertical=5.dp),verticalAlignment=Alignment.CenterVertically) {
            Text("الوضع الليلي",fontSize=10.sp,color=Color(0xFFE8E2CE))
            Switch(checked=dark,onCheckedChange=onDark)
          }
        }
      }
    }
    item {
      Card(shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF123D30)),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFFB9974C).copy(alpha=.55f))) {
        Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(13.dp),horizontalAlignment=Alignment.CenterHorizontally) {
          Text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color(0xFFE8C96F),textAlign=TextAlign.Center)
          Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",fontSize=27.sp,fontWeight=FontWeight.Bold,color=Color.White,textAlign=TextAlign.Center)
          Text("افتح مصحفك، وواصل وردك، وتعلّم كل يوم.",fontSize=13.sp,color=Color(0xFFD4E3DB),textAlign=TextAlign.Center)
          Button(onClick=onQuran,modifier=Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(17.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFE0BD60),contentColor=Color(0xFF10271F))) {
            Text("متابعة القرآن  ←",fontSize=17.sp,fontWeight=FontWeight.ExtraBold)
          }
        }
      }
    }
    item {
      Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
        listOf(Triple("٦٢٣٦","آية","القرآن"),Triple(toArabic(hadithCount),"حديث","الحديث"),Triple(toArabic(lessonCount),"درس","التجويد")).forEach { stat ->
          Card(Modifier.weight(1f),shape=RoundedCornerShape(19.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF142820)),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFF315544))) {
            Column(Modifier.fillMaxWidth().padding(vertical=13.dp,horizontal=5.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(3.dp)) {
              Text(stat.first,fontSize=19.sp,fontWeight=FontWeight.Black,color=Color(0xFFE5C36A))
              Text(stat.second,fontSize=11.sp,color=Color.White,fontWeight=FontWeight.Bold)
              Text(stat.third,fontSize=9.sp,color=Color(0xFFAFC4B8))
            }
          }
        }
      }
    }
    item { Text("أقسامك الرئيسية",fontSize=21.sp,fontWeight=FontWeight.ExtraBold,color=Color(0xFFF3EBD7)) }
    item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
      HomeTile("المصحف","قراءة وتدبر","۞",Modifier.weight(1f),onQuran)
      HomeTile("التسميع","تدريب ومراجعة","◉",Modifier.weight(1f),onRecitation)
    } }
    item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
      HomeTile("مكتبة العلم","حديث وفقه وسيرة","✧",Modifier.weight(1f),onLearning)
      HomeTile("الأذكار","أذكار وأدعية","❈",Modifier.weight(1f),onAdhkar)
    } }
    item { HomeTile("المسبحة الإلكترونية","عداد ذكر وأهداف يومية","◌",Modifier.fillMaxWidth(),onTasbih) }
    item {
      Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF101F1A)),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFF765F31))) {
        Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
          Text("مكتبة تتوسع بالمراجعة والتوثيق",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color(0xFFE5C36A))
          Text("المتاح حاليًا: ${toArabic(hadithCount)} حديثًا، ${toArabic(lessonCount)} درس تجويد، و${toArabic(storyCount)} مادة قصصية. الأعداد المستهدفة ليست أعدادًا منشورة بعد.",fontSize=12.sp,lineHeight=20.sp,color=Color(0xFFD0DDD6))
          Text("لن نضيف رواية أو ننسبها إلى صحابي إلا مع مصدر يمكن مراجعته.",fontSize=11.sp,color=Color(0xFFB8C8BE))
        }
      }
    }
  }
}

@Composable
private fun HomeTile(title:String,subtitle:String,symbol:String,modifier:Modifier,onClick:()->Unit) {
  Card(modifier.clickable(onClick=onClick),shape=RoundedCornerShape(23.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF142820)),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFF315544))) {
    Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
      Box(Modifier.size(48.dp).background(Color(0xFF244638),RoundedCornerShape(16.dp)),contentAlignment=Alignment.Center) {
        Text(symbol,fontSize=25.sp,color=Color(0xFFE5C36A),fontWeight=FontWeight.Bold)
      }
      Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text(title,fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color(0xFFF1EAD8))
        Text(subtitle,fontSize=10.sp,color=Color(0xFFB5C9BD))
      }
      Text("‹",fontSize=24.sp,color=Color(0xFFE5C36A),fontWeight=FontWeight.Bold)
    }
  }
}

@Composable
private fun FeatureCard(emoji:String,title:String,desc:String,onClick:()->Unit) {
  val c=MaterialTheme.colorScheme
  Card(Modifier.fillMaxWidth().clickable(onClick=onClick),shape=RoundedCornerShape(24.dp),
    colors=CardDefaults.cardColors(containerColor=c.surface),
    border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.25f))) {
    Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
      Box(Modifier.size(60.dp).background(c.primary.copy(alpha=.12f),RoundedCornerShape(20.dp)),contentAlignment=Alignment.Center) {
        Text(emoji,fontSize=28.sp,color=c.secondary)
      }
      Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
        Text(title,fontSize=19.sp,fontWeight=FontWeight.Bold,color=c.primary)
        Text(desc,fontSize=12.sp,color=c.onSurfaceVariant,lineHeight=18.sp)
      }
      Text("‹",fontSize=26.sp,color=c.secondary,fontWeight=FontWeight.Bold)
    }
  }
}

@Composable
fun AdhkarScreen(onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  var category by remember { mutableStateOf("عام") }
  val categories=listOf("عام","الصباح","المساء","النوم")
  val adhkar=mapOf(
    "عام" to listOf(
      "سبحان الله وبحمده، سبحان الله العظيم",
      "أستغفر الله وأتوب إليه",
      "لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير",
      "اللهم صل وسلم على نبينا محمد",
      "رضيت بالله رباً، وبالإسلام ديناً، وبمحمد صلى الله عليه وسلم نبياً"
    ),
    "الصباح" to listOf(
      "أصبحنا وأصبح الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له.",
      "اللهم بك أصبحنا، وبك أمسينا، وبك نحيا، وبك نموت، وإليك النشور.",
      "اللهم إني أسألك العفو والعافية في الدنيا والآخرة.",
      "سبحان الله وبحمده."
    ),
    "المساء" to listOf(
      "أمسينا وأمسى الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له.",
      "اللهم بك أمسينا، وبك أصبحنا، وبك نحيا، وبك نموت، وإليك المصير.",
      "اللهم إني أسألك العفو والعافية في الدنيا والآخرة.",
      "أستغفر الله وأتوب إليه."
    ),
    "النوم" to listOf(
      "باسمك اللهم أموت وأحيا.",
      "اللهم قني عذابك يوم تبعث عبادك.",
      "سبحان الله، والحمد لله، والله أكبر."
    )
  )
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding().padding(16.dp)) {
    TextButton(onClick=onBack){Text("رجوع")}
    Text("الأذكار",fontSize=27.sp,fontWeight=FontWeight.Bold,color=c.primary)
    Text("اختر القسم لعرض مجموعة من الأذكار",fontSize=13.sp,color=c.onSurfaceVariant)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
      categories.forEach { name ->
        FilterChip(selected=category==name,onClick={category=name},label={Text(name)})
      }
    }
    LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.weight(1f)) {
      items(adhkar[category]?.size ?: 0) { i ->
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp)) {
          Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text(toArabic(i+1),fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            Text(adhkar[category]!![i],fontSize=20.sp,lineHeight=34.sp,textAlign=TextAlign.Center,color=c.onSurface)
          }
        }
      }
      item { Text("هذه مجموعة مختارة؛ يُرجى الرجوع إلى مصدر موثوق للتحقق من النصوص والأعداد الخاصة بكل ذكر.",fontSize=11.sp,color=c.onSurfaceVariant,lineHeight=17.sp) }
    }
  }
}

@Composable
fun TasbihScreen(prefs:AppPrefs,onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  var count by remember { mutableIntStateOf(prefs.tasbihCount()) }
  var phraseIndex by remember { mutableIntStateOf(0) }
  var target by remember { mutableIntStateOf(33) }
  var showReset by remember { mutableStateOf(false) }
  val phrases = listOf("سُبْحَانَ اللَّهِ","الْحَمْدُ لِلَّهِ","اللَّهُ أَكْبَرُ","أَسْتَغْفِرُ اللَّهَ","أَذْكَرُ اللَّهَ")
  val targets = listOf(33,100,500,1000)
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(16.dp)) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) { TextButton(onClick=onBack){Text("رجوع")};Text("المسبحة الإلكترونية",fontSize=22.sp,fontWeight=FontWeight.Bold,color=c.primary) }
    Text("اختر الذكر",fontSize=14.sp,color=c.onSurfaceVariant)
    Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
      phrases.forEachIndexed { index, phrase ->
        OutlinedButton(onClick={phraseIndex=index},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.outlinedButtonColors(containerColor=if(phraseIndex==index)c.primary.copy(alpha=.12f) else Color.Transparent)) { Text(phrase) }
      }
    }
    Text(phrases[phraseIndex],fontSize=27.sp,fontWeight=FontWeight.Bold,color=c.primary,textAlign=TextAlign.Center)
    Text(count.toString().map { '٠'+(it-'0') }.joinToString(""),fontSize=56.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
    Text("الهدف: ${toArabic(target)} • المتبقي: ${toArabic((target-count).coerceAtLeast(0))}",fontSize=14.sp,color=c.secondary,fontWeight=FontWeight.Bold)
    LinearProgressIndicator(progress={ (count.toFloat()/target).coerceIn(0f,1f) },modifier=Modifier.fillMaxWidth())
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) { targets.forEach { t-> OutlinedButton(onClick={target=t},modifier=Modifier.weight(1f)) { Text(toArabic(t)) } } }
    Button(
      onClick = {
        count += 1
        prefs.setTasbihCount(count)
      },
      modifier = Modifier.fillMaxWidth().height(68.dp),
      shape = RoundedCornerShape(22.dp)
    ) {
      Text("تسبيح +١", fontSize = 22.sp)
    }
    OutlinedButton(
      onClick = {
        count = (count - 1).coerceAtLeast(0)
        prefs.setTasbihCount(count)
      },
      enabled = count > 0,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("تراجع عن آخر تسبيحة −١")
    }
    if(count>=target) Text("أحسنت! وصلت إلى هدفك. يمكنك مواصلة الذكر أو بدء هدف جديد.",fontSize=14.sp,color=c.primary,textAlign=TextAlign.Center)
    OutlinedButton(onClick={showReset=true},modifier=Modifier.fillMaxWidth()) { Text("إعادة العداد") }
    Text("يُحفظ العدد تلقائياً على جهازك.",fontSize=12.sp,color=c.onSurfaceVariant)
  }
  if(showReset) {
    AlertDialog(
      onDismissRequest={showReset=false},
      title={Text("إعادة عداد التسبيح؟")},
      text={Text("سيُعاد العدد الحالي إلى صفر.")},
      confirmButton={TextButton(onClick={count=0;prefs.setTasbihCount(0);showReset=false}){Text("إعادة")}},
      dismissButton={TextButton(onClick={showReset=false}){Text("إلغاء")}}
    )
  }
}

private data class LearningSectionInfo(
  val title: String,
  val subtitle: String,
  val target: String,
  val scope: String,
  val status: String
)

private val learningSections = listOf(
  LearningSectionInfo("التجويد","تعلم أحكام التلاوة خطوة بخطوة","300 درس","الحكم والتعريف والسبب والموضع والمثال والأداء والأخطاء والتمرين والاختبار.","متاح: ${tajweedLessons.size} درسًا"),
  LearningSectionInfo("التفسير","فهم معاني الآيات","تفسير 6,236 آية","تفسير الآيات وربط كل شرح بمصدره ومرجعه.","قيد البناء"),
  LearningSectionInfo("الحديث","السنة النبوية مع التخريج","7,000 حديث","النص والراوي والكتاب والتخريج والدرجة والشرح، دون نسبة حكم بلا مرجع.","متاح: 30 مادة"),
  LearningSectionInfo("الفقه","مسائل العبادات والمعاملات","6,000 مسألة","السؤال والحكم والدليل والشرح والخلاف المعتبر والمراجع.","متاح: 30 مدخلًا"),
  LearningSectionInfo("السيرة والصحابة","سير وتراجم موثقة","7,000 مادة","تراجم الصحابة والسيرة مع إحالات للمصادر وتمييز الروايات.","قيد البناء"),
  LearningSectionInfo("القصص الإسلامية","قصص الأنبياء والسيرة والتاريخ والصحابة","6,000 قصة ومادة","عرض القصة مع الأدلة والمراجع وبيان ما صح وما اختلف فيه.","متاح: 30 قصة ومادة"),
  LearningSectionInfo("الاختبارات","اختبر فهمك وحفظك","7,000 سؤال","أسئلة متدرجة بإجابات مفسرة ومصدر لكل معلومة.","قيد البناء"),
  LearningSectionInfo("الأذكار والأدعية","أذكار موثقة حسب الوقت","مكتبة موسعة","النص والمصدر والدرجة والوقت والعدد الثابت عند ثبوته.","متاح: أذكار مختارة")
)

@Composable
fun LearningHubScreen(onBack:()->Unit,onOpen:(String)->Unit) {
  val c=MaterialTheme.colorScheme
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal=14.dp, vertical=8.dp),verticalAlignment=Alignment.CenterVertically) {
      TextButton(onClick=onBack){Text("رجوع")}
      Text("مكتبة متقن",fontSize=24.sp,fontWeight=FontWeight.Bold,color=c.primary)
    }
    Text("مصادر التعلم والعبادات في مكان واحد",Modifier.padding(horizontal=16.dp),fontSize=13.sp,color=c.onSurfaceVariant)
    LazyColumn(contentPadding=PaddingValues(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
      items(learningSections.size) { i ->
        val item=learningSections[i]
        val publishedCount=when(item.title) {
          "التجويد" -> tajweedLessons.size
          "الحديث" -> hadithEntries.size
          "الفقه" -> fiqhEntries.size
          "القصص الإسلامية" -> quranStories.size
          else -> 0
        }
        val status=if(publishedCount>0) "متاح: ${toArabic(publishedCount)} مادة" else "قيد البناء"
        Card(Modifier.fillMaxWidth().clickable { onOpen(item.title) },shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c.surface)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
              Text(item.title,fontSize=18.sp,fontWeight=FontWeight.Bold,color=c.primary)
              Surface(color=if(publishedCount>0) c.primary.copy(alpha=.12f) else c.secondary.copy(alpha=.15f),shape=RoundedCornerShape(30.dp)) {
                Text(status,Modifier.padding(horizontal=10.dp,vertical=5.dp),fontSize=10.sp,color=c.onSurface)
              }
            }
            Text(item.subtitle,fontSize=13.sp,color=c.onSurface)
            Text("الهدف: ${item.target}",fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=c.secondary)
            Text(item.scope,fontSize=12.sp,color=c.onSurfaceVariant,lineHeight=18.sp)
          }
        }
      }
      item { Text("الأعداد أهداف للمحتوى النهائي وليست أعدادًا منشورة حاليًا. لن نملأ المكتبة بمواد مكررة أو غير موثقة.",Modifier.fillMaxWidth().padding(8.dp),fontSize=11.sp,color=c.error,lineHeight=17.sp) }
    }
  }
}

@Composable
fun LearningSectionScreen(section:String,onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  val context = LocalContext.current
  val progressPrefs = remember { AppPrefs(context) }
  var completedIds by remember(section) { mutableStateOf(progressPrefs.completedLearningIds(section)) }
  fun toggleCompleted(id: Int, checked: Boolean) {
    progressPrefs.setLearningCompleted(section, id, checked)
    completedIds = if (checked) completedIds + id else completedIds - id
  }
  val info=learningSections.firstOrNull { it.title==section }
  var searchQuery by remember(section) { mutableStateOf("") }
  val query = normalizeAr(searchQuery.trim())
  val filteredTajweed = tajweedLessons.filter { lesson ->
    query.isBlank() || normalizeAr(listOf(lesson.title,lesson.objective,lesson.definition,lesson.rule,lesson.examples.joinToString(" "),lesson.practice,lesson.commonMistake,lesson.references).joinToString(" ")).contains(query)
  }
  val filteredHadith: List<HadithEntry> = hadithEntries.filter { h: HadithEntry ->
    query.isBlank() || normalizeAr(listOf(h.title,h.text,h.narrator,h.source,h.reference,h.grade,h.explanation).joinToString(" ")).contains(query)
  }
  val filteredFiqh = fiqhEntries.filter { f ->
    query.isBlank() || normalizeAr(listOf(f.title,f.question,f.ruling,f.evidence,f.explanation,f.scope,f.references).joinToString(" ")).contains(query)
  }
  val filteredStories = quranStories.filter { story ->
    query.isBlank() || normalizeAr(listOf(story.title,story.summary,story.verses,story.lessons.joinToString(" "),story.sourceNote).joinToString(" ")).contains(query)
  }
  LazyColumn(
    Modifier.fillMaxSize().background(c.background).safeDrawingPadding(),
    contentPadding=PaddingValues(16.dp),
    verticalArrangement=Arrangement.spacedBy(12.dp)
  ) {
    item {
      TextButton(onClick=onBack){Text("رجوع إلى المكتبة")}
      Text(section,fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
      if(info!=null) {
        Text(info.subtitle,fontSize=15.sp,color=c.onSurfaceVariant)
        Text("الهدف النهائي: ${info.target}",fontSize=13.sp,fontWeight=FontWeight.Bold,color=c.secondary)
      }
      if(section=="التجويد" || section=="الحديث" || section=="الفقه" || section=="القصص الإسلامية") {
        OutlinedTextField(
          value=searchQuery,
          onValueChange={searchQuery=it},
          modifier=Modifier.fillMaxWidth(),
          singleLine=true,
          label={Text("ابحث داخل ${section}")},
          placeholder={Text("اكتب كلمة أو عنوانًا أو مصدرًا")}
        )
      }
      if(section=="التجويد" || section=="الحديث" || section=="الفقه" || section=="القصص الإسلامية") {
        val total = when(section) { "التجويد" -> tajweedLessons.size; "الحديث" -> hadithEntries.size; "الفقه" -> fiqhEntries.size; else -> quranStories.size }
        val done = completedIds.size
        Text("تقدمك في هذا القسم: $done من $total",fontSize=14.sp,fontWeight=FontWeight.Bold,color=c.primary)
        LinearProgressIndicator(progress={ if(total==0) 0f else done.toFloat()/total },modifier=Modifier.fillMaxWidth())
        Text("ضع علامة بعد إكمال المادة؛ سيُحفظ تقدمك على هذا الجهاز.",fontSize=11.sp,color=c.onSurfaceVariant)
      }
    }
    if(section=="التجويد") {
      item {
        Text("دروس التجويد المتاحة: ${tajweedLessons.size} من أصل 300 درس مستهدف",fontSize=14.sp,fontWeight=FontWeight.Bold,color=c.primary)
        Text("كل درس يعرض الهدف والتعريف والقاعدة والأمثلة والتدريب والأخطاء الشائعة والمراجع. يلزم استكمال المراجعة العلمية قبل اعتماد بقية المنهج.",fontSize=12.sp,lineHeight=19.sp,color=c.onSurfaceVariant)
      }
      items(filteredTajweed.size) { index ->
        val lesson=filteredTajweed[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("الدرس ${lesson.id}: ${lesson.title}",fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary)
            Text("الهدف: ${lesson.objective}",fontSize=13.sp,color=c.onSurface)
            Text("التعريف: ${lesson.definition}",fontSize=13.sp,lineHeight=20.sp,color=c.onSurface)
            Text("القاعدة والشرح: ${lesson.rule}",fontSize=13.sp,lineHeight=20.sp,color=c.onSurface)
            Text("أمثلة:",fontWeight=FontWeight.Bold,fontSize=13.sp,color=c.secondary)
            lesson.examples.forEach { example -> Text("• $example",fontSize=14.sp,color=c.onSurface) }
            Text("تدريب عملي: ${lesson.practice}",fontSize=13.sp,lineHeight=20.sp,color=c.onSurface)
            Text("خطأ شائع: ${lesson.commonMistake}",fontSize=13.sp,lineHeight=20.sp,color=c.error)
            Text("المرجع: ${lesson.references}",fontSize=11.sp,lineHeight=17.sp,color=c.onSurfaceVariant)
            Row(verticalAlignment=Alignment.CenterVertically) {
              Checkbox(checked=lesson.id in completedIds,onCheckedChange={toggleCompleted(lesson.id,it)})
              Text("تمت مراجعة هذا الدرس",fontSize=12.sp,color=c.primary)
            }
          }
        }
      }
      item {
        if(filteredTajweed.isEmpty()) Text("لا توجد دروس تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
        Text("نتائج البحث: ${filteredTajweed.size} من ${tajweedLessons.size} درسًا. المتبقي لإكمال الهدف: ${(300-tajweedLessons.size).coerceAtLeast(0)} درسًا.",fontSize=12.sp,color=c.error)
      }
    } else if(section=="الحديث") {
      item {
        Text("الأحاديث المنشورة: ${hadithEntries.size} — نتائج البحث: ${filteredHadith.size}.",fontSize=13.sp,color=c.onSurfaceVariant)
        if(filteredHadith.isEmpty()) Text("لا توجد أحاديث تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
      }
      items(filteredHadith.size) { index ->
        val h=filteredHadith[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("الحديث ${h.id}: ${h.title}",fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary)
            Text(h.text,fontSize=19.sp,lineHeight=31.sp,color=c.onSurface,textAlign=TextAlign.Center)
            if(h.text.contains("...")) Text("تنبيه: هذا النص معروض كمقتطف، ويحتاج استكمال النص قبل اعتماده.",fontSize=11.sp,color=c.error)
            Text("الصحابي الراوي: ${h.narrator}",fontSize=12.sp,color=c.onSurfaceVariant)
            Text("المصدر: ${h.source} — ${h.reference}",fontSize=12.sp,color=c.secondary)
            Text("الدرجة: ${h.grade}",fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.primary)
            Text("شرح مختصر: ${h.explanation}",fontSize=13.sp,lineHeight=21.sp,color=c.onSurface)
            Row(verticalAlignment=Alignment.CenterVertically) {
              Checkbox(checked=h.id in completedIds,onCheckedChange={toggleCompleted(h.id,it)})
              Text("تمت مراجعة هذا الحديث",fontSize=12.sp,color=c.primary)
            }
          }
        }
      }
    } else if(section=="الفقه") {
      item {
        Text("المسائل المنشورة: ${fiqhEntries.size} — نتائج البحث: ${filteredFiqh.size}. هذه مداخل تأسيسية وليست فتاوى شخصية.",fontSize=13.sp,color=c.onSurfaceVariant)
        if(filteredFiqh.isEmpty()) Text("لا توجد مسائل تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
      }
      items(filteredFiqh.size) { index ->
        val f=filteredFiqh[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("المسألة ${f.id}: ${f.title}",fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary)
            Text("السؤال: ${f.question}",fontSize=14.sp,fontWeight=FontWeight.SemiBold,color=c.onSurface)
            Text("الخلاصة: ${f.ruling}",fontSize=14.sp,lineHeight=22.sp,color=c.onSurface)
            Text("الدليل: ${f.evidence}",fontSize=13.sp,lineHeight=20.sp,color=c.secondary)
            Text("الشرح والتنبيه: ${f.explanation}",fontSize=13.sp,lineHeight=21.sp,color=c.onSurface)
            Text("النطاق: ${f.scope}",fontSize=12.sp,lineHeight=19.sp,color=c.error)
            Text("المراجع: ${f.references}",fontSize=11.sp,lineHeight=17.sp,color=c.onSurfaceVariant)
            Row(verticalAlignment=Alignment.CenterVertically) {
              Checkbox(checked=f.id in completedIds,onCheckedChange={toggleCompleted(f.id,it)})
              Text("تمت مراجعة هذه المسألة",fontSize=12.sp,color=c.primary)
            }
          }
        }
      }
    } else if(section=="القصص الإسلامية") {
      item {
        Text("القصص المنشورة: ${quranStories.size} — نتائج البحث: ${filteredStories.size}. ملخصات مستندة إلى آيات القرآن الكريم.",fontSize=13.sp,color=c.onSurfaceVariant)
        if(filteredStories.isEmpty()) Text("لا توجد قصص تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
      }
      items(filteredStories.size) { index ->
        val story=filteredStories[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("القصة ${story.id}: ${story.title}",fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary)
            Text(story.summary,fontSize=14.sp,lineHeight=23.sp,color=c.onSurface)
            Text("مواضع القصة: ${story.verses}",fontSize=13.sp,fontWeight=FontWeight.SemiBold,color=c.secondary)
            if(story.chapters.isNotEmpty()) {
              Text("النسخة المطوّلة: ${story.chapters.size} فصول",fontSize=14.sp,fontWeight=FontWeight.Bold,color=c.primary)
              story.chapters.forEachIndexed { chapterIndex, chapter ->
                Card(colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(14.dp)) {
                  Text(chapter,Modifier.fillMaxWidth().padding(14.dp),fontSize=14.sp,lineHeight=25.sp,color=c.onSurface)
                }
              }
            } else {
              Text("هذه بطاقة موجزة؛ النسخة المطوّلة ذات عشرة فصول لم تُستكمل لهذه القصة بعد.",fontSize=12.sp,lineHeight=19.sp,color=c.error)
            }
            Text("الدروس المستفادة:",fontSize=13.sp,fontWeight=FontWeight.Bold,color=c.primary)
            story.lessons.forEach { lesson -> Text("• $lesson",fontSize=13.sp,color=c.onSurface) }
            Text("منهج التوثيق: ${story.sourceNote}",fontSize=12.sp,lineHeight=19.sp,color=c.onSurfaceVariant)
            Row(verticalAlignment=Alignment.CenterVertically) {
              Checkbox(checked=story.id in completedIds,onCheckedChange={toggleCompleted(story.id,it)})
              Text("تمت مراجعة هذه القصة",fontSize=12.sp,color=c.primary)
            }
          }
        }
      }
    } else {
      item {
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(20.dp)) {
          Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text("نطاق القسم",fontWeight=FontWeight.Bold,color=c.primary)
            if(info!=null) Text(info.scope,fontSize=14.sp,lineHeight=23.sp,color=c.onSurface)
            Text("المحتوى المنشور في هذا القسم لم يكتمل بعد. ستظهر كل مادة مع مصدرها ومرجعها وحالة التحقق.",fontSize=13.sp,lineHeight=21.sp,color=c.error)
          }
        }
      }
    }
  }
}
