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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeeniHomeScreen(dark:Boolean,onDark:(Boolean)->Unit,onQuran:()->Unit,onRecitation:()->Unit,onAdhkar:()->Unit,onTasbih:()->Unit,onLearning:()->Unit) {
  val c=MaterialTheme.colorScheme
  LazyColumn(Modifier.fillMaxSize().background(c.background),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
    item {
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
        Column { Text("مُتقن",fontSize=32.sp,fontWeight=FontWeight.ExtraBold,color=c.primary);Text("منصة القرآن والتعلم والعبادات",fontSize=14.sp,color=c.onSurfaceVariant) }
        Row(verticalAlignment=Alignment.CenterVertically) { Text("ليلي",fontSize=12.sp);Switch(checked=dark,onCheckedChange=onDark) }
      }
    }
    item {
      Card(colors=CardDefaults.cardColors(containerColor=c.primary),shape=RoundedCornerShape(24.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
          Text("السلام عليكم ورحمة الله",fontSize=21.sp,fontWeight=FontWeight.Bold,color=Color.White)
          Text("اجعل يومك عامراً بورد من القرآن وذكر الله.",fontSize=14.sp,color=Color.White.copy(alpha=.9f))
          Button(onClick=onQuran,colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFD4AF37),contentColor=Color(0xFF173B2B))) { Text("ابدأ ورد القرآن") }
        }
      }
    }
    item { Text("أقسام متقن",fontSize=20.sp,fontWeight=FontWeight.Bold,color=c.onBackground) }
    item { FeatureCard("📚","مكتبة العلوم الإسلامية","التجويد والتفسير والحديث والفقه والسيرة والقصص والاختبارات.",onLearning) }
    item { FeatureCard("📖","المصحف الشريف","تصفح السور وابحث بالاسم أو الرقم وحدد مقدار وردك.",onQuran) }
    item { FeatureCard("🎙","التسميع الصوتي","سجّل تلاوتك وراقب مستوى الميكروفون ثم استمع للتسجيل.",onRecitation) }
    item { FeatureCard("🤲","الأذكار","أذكار مختارة تقرؤها في أي وقت.",onAdhkar) }
    item { FeatureCard("📿","المسبحة الإلكترونية","عداد تسبيح يحفظ عددك حتى بعد إغلاق التطبيق.",onTasbih) }
    item { Text("تنبيه: تصحيح الكلمات وأحكام التجويد آلياً غير مفعّلة بعد؛ لا يعرض التطبيق درجات أو تصحيحات غير موثوقة.",fontSize=12.sp,color=c.error,lineHeight=19.sp) }
  }
}

@Composable
private fun FeatureCard(emoji:String,title:String,desc:String,onClick:()->Unit) {
  val c=MaterialTheme.colorScheme
  Card(Modifier.fillMaxWidth().clickable(onClick=onClick),shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=c.surface)) {
    Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
      Text(emoji,fontSize=27.sp)
      Column(Modifier.weight(1f)) { Text(title,fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary);Text(desc,fontSize=12.sp,color=c.onSurfaceVariant,lineHeight=18.sp) }
      Text("‹",fontSize=24.sp,color=c.secondary)
    }
  }
}

@Composable
fun AdhkarScreen(onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding().padding(16.dp)) {
    TextButton(onClick=onBack){Text("رجوع")}
    Text("الأذكار",fontSize=27.sp,fontWeight=FontWeight.Bold,color=c.primary)
    Text("أذكار مختارة",fontSize=13.sp,color=c.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))
    LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)) {
      val items=listOf("سبحان الله وبحمده، سبحان الله العظيم","أستغفر الله وأتوب إليه","لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير","اللهم صل وسلم على نبينا محمد","رضيت بالله رباً، وبالإسلام ديناً، وبمحمد صلى الله عليه وسلم نبياً")
      items(items.size) { i ->
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp)) {
          Text(items[i],Modifier.fillMaxWidth().padding(18.dp),fontSize=20.sp,lineHeight=34.sp,textAlign=TextAlign.Center,color=c.onSurface)
        }
      }
    }
  }
}

@Composable
fun TasbihScreen(prefs:AppPrefs,onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  var count by remember { mutableIntStateOf(prefs.tasbihCount()) }
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(18.dp)) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) { TextButton(onClick=onBack){Text("رجوع")};Text("المسبحة الإلكترونية",fontSize=22.sp,fontWeight=FontWeight.Bold,color=c.primary) }
    Spacer(Modifier.height(22.dp))
    Text("سُبْحَانَ اللَّهِ",fontSize=30.sp,fontWeight=FontWeight.Bold,color=c.primary)
    Text(count.toString().map { '٠'+(it-'0') }.joinToString(""),fontSize=54.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
    Button(onClick={count++;prefs.setTasbihCount(count)},modifier=Modifier.fillMaxWidth().height(64.dp),shape=RoundedCornerShape(22.dp)) { Text("تسبيح",fontSize=22.sp) }
    OutlinedButton(onClick={count=0;prefs.setTasbihCount(0)},modifier=Modifier.fillMaxWidth()) { Text("تصفير العداد") }
    Text("يُحفظ العدد تلقائياً على جهازك.",fontSize=12.sp,color=c.onSurfaceVariant)
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
  LearningSectionInfo("التجويد","تعلم أحكام التلاوة خطوة بخطوة","300 درس","الحكم والتعريف والسبب والموضع والمثال والأداء والأخطاء والتمرين والاختبار.","قيد البناء"),
  LearningSectionInfo("التفسير","فهم معاني الآيات","تفسير 6,236 آية","تفسير الآيات وربط كل شرح بمصدره ومرجعه.","قيد البناء"),
  LearningSectionInfo("الحديث","السنة النبوية مع التخريج","7,000 حديث","النص والراوي والكتاب والتخريج والدرجة والشرح، دون نسبة حكم بلا مرجع.","قيد البناء"),
  LearningSectionInfo("الفقه","مسائل العبادات والمعاملات","6,000 مسألة","السؤال والحكم والدليل والشرح والخلاف المعتبر والمراجع.","قيد البناء"),
  LearningSectionInfo("السيرة والصحابة","سير وتراجم موثقة","7,000 مادة","تراجم الصحابة والسيرة مع إحالات للمصادر وتمييز الروايات.","قيد البناء"),
  LearningSectionInfo("القصص الإسلامية","قصص الأنبياء والسيرة والتاريخ","6,000 قصة ومادة","عرض القصة مع الأدلة والمراجع وبيان ما صح وما اختلف فيه.","قيد البناء"),
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
        Card(Modifier.fillMaxWidth().clickable { onOpen(item.title) },shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c.surface)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
              Text(item.title,fontSize=18.sp,fontWeight=FontWeight.Bold,color=c.primary)
              Surface(color=if(item.status.startsWith("متاح")) c.primary.copy(alpha=.12f) else c.secondary.copy(alpha=.15f),shape=RoundedCornerShape(30.dp)) {
                Text(item.status,Modifier.padding(horizontal=10.dp,vertical=5.dp),fontSize=10.sp,color=c.onSurface)
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
  val info=learningSections.firstOrNull { it.title==section }
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding().padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
    TextButton(onClick=onBack){Text("رجوع إلى المكتبة")}
    Text(section,fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
    if(info!=null) {
      Text(info.subtitle,fontSize=15.sp,color=c.onSurfaceVariant)
      Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
          Text("نطاق القسم",fontWeight=FontWeight.Bold,color=c.primary)
          Text(info.scope,fontSize=14.sp,lineHeight=23.sp,color=c.onSurface)
          Text("العدد المستهدف: ${info.target}",fontWeight=FontWeight.Bold,color=c.secondary)
          Text("الحالة الحالية: ${info.status}",fontSize=13.sp,color=c.onSurfaceVariant)
        }
      }
    }
    Text("سيُضاف المحتوى مادةً مادة مع المصدر والتخريج أو المرجع والتحقق قبل إظهاره. لا توجد حاليًا مكتبة مكتملة بهذا العدد.",fontSize=13.sp,lineHeight=22.sp,color=c.error)
  }
}
