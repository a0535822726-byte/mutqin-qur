package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun DeeniHomeScreen(dark:Boolean,onDark:(Boolean)->Unit,onQuran:()->Unit,onResumeReading:()->Unit,onRecitation:()->Unit,onAdhkar:()->Unit,onTasbih:()->Unit,onLearning:()->Unit) {
  val c=MaterialTheme.colorScheme
  val homeContext=LocalContext.current
  val lastReading=remember { AppPrefs(homeContext).lastPosition() }
  val hadithCount=hadithEntries.size
  val lessonCount=tajweedLessons.size
  val storyCount=quranStories.size
  LazyColumn(
    Modifier.fillMaxSize().background(c.background),
    contentPadding=PaddingValues(start=16.dp,end=16.dp,top=12.dp,bottom=34.dp),
    verticalArrangement=Arrangement.spacedBy(15.dp)
  ) {
    item {
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)) {
          Text("السَّلَامُ عَلَيْكَ",fontSize=10.sp,color=c.onSurfaceVariant,fontWeight=FontWeight.SemiBold)
          Text("مَرْحَبًا بِكَ فِي مُتْقِن",fontSize=18.sp,color=c.onBackground,fontWeight=FontWeight.Black)
        }
        Surface(shape=RoundedCornerShape(30.dp),color=c.surface,border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant)) {
          Row(Modifier.padding(start=10.dp,end=4.dp),verticalAlignment=Alignment.CenterVertically) {
            Text(if(dark)"ليلي" else "نهاري",fontSize=10.sp,color=c.onSurface)
            Switch(checked=dark,onCheckedChange=onDark)
          }
        }
      }
    }
    item {
      Card(
        Modifier.fillMaxWidth(),
        shape=RoundedCornerShape(topStart=34.dp,topEnd=12.dp,bottomEnd=34.dp,bottomStart=12.dp),
        colors=CardDefaults.cardColors(containerColor=Color(0xFF062A20)),
        border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFFD6B56D).copy(alpha=.8f)),
        elevation=CardDefaults.cardElevation(defaultElevation=5.dp)
      ) {
        Box(Modifier.fillMaxWidth()) {
          Column(Modifier.fillMaxWidth().padding(horizontal=22.dp,vertical=23.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)) {
                Text("مُتْقِن",fontSize=31.sp,fontWeight=FontWeight.Black,color=Color(0xFFF7F1E4),letterSpacing=1.sp)
                Text("رفيق رحلتك مع القرآن",fontSize=11.sp,color=Color(0xFFD6B56D))
              }
              Box(Modifier.size(58.dp).border(1.dp,Color(0xFFD6B56D).copy(alpha=.7f),RoundedCornerShape(20.dp)).padding(5.dp).border(1.dp,Color(0xFFD6B56D).copy(alpha=.35f),RoundedCornerShape(16.dp)),contentAlignment=Alignment.Center) {
                Text("۞",fontSize=35.sp,color=Color(0xFFE5C77E))
              }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFD6B56D).copy(alpha=.45f)))
            Text("وَرْدُكَ يَبْدَأُ مِنْ هُنَا",fontSize=10.sp,color=Color(0xFFE5C77E),fontWeight=FontWeight.Bold,letterSpacing=1.4.sp)
            Text("اجعل للقرآن\nنصيبًا من يومك.",fontSize=30.sp,lineHeight=39.sp,fontWeight=FontWeight.ExtraBold,color=Color(0xFFFFFCF5))
            Text("مصحف للقراءة، ومراجعة للتسميع، ومكتبة تعينك على التعلّم.",fontSize=12.sp,lineHeight=21.sp,color=Color(0xFFE6EDE7).copy(alpha=.85f))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
              Button(onClick=onQuran,modifier=Modifier.weight(1.15f).height(52.dp),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFE0BD72),contentColor=Color(0xFF10291F))) {
                Text("افتح المصحف  ←",fontWeight=FontWeight.ExtraBold,fontSize=12.sp)
              }
              OutlinedButton(onClick=onRecitation,modifier=Modifier.weight(.85f).height(52.dp),shape=RoundedCornerShape(15.dp),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0xFFD6B56D).copy(alpha=.75f)),colors=ButtonDefaults.outlinedButtonColors(contentColor=Color(0xFFFFF8E8))) {
                Text("ابدأ التسميع",fontWeight=FontWeight.Bold,fontSize=12.sp)
              }
            }
          }
        }
      }
    }
    if (lastReading != null) {
      item {
        Card(
          modifier=Modifier.fillMaxWidth().clickable(onClick=onResumeReading),
          shape=RoundedCornerShape(20.dp),
          colors=CardDefaults.cardColors(containerColor=c.surface),
          border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.55f))
        ) {
          Row(Modifier.fillMaxWidth().padding(horizontal=15.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(44.dp).background(c.primary.copy(alpha=.10f),RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center) {
              Text("↶",fontSize=25.sp,color=c.secondary,fontWeight=FontWeight.Bold)
            }
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
              Text("أكمل من حيث توقفت",fontSize=14.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
              Text("سورة ${SURAH_NAMES[lastReading.first-1]} • الآيات ${toArabic(lastReading.second)}–${toArabic(lastReading.third)}",fontSize=11.sp,color=c.onSurfaceVariant)
            }
            Text("متابعة  ‹",fontSize=11.sp,color=c.secondary,fontWeight=FontWeight.Bold)
          }
        }
      }
    }
    item {
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)) {
          Text("مَكْتَبَتُكَ الْعِلْمِيَّة",fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=c.onBackground)
          Text("محتوى الأقسام المتاح داخل التطبيق",fontSize=10.sp,color=c.onSurfaceVariant)
        }
        Text("إحصاءات",fontSize=10.sp,color=c.secondary,fontWeight=FontWeight.Bold)
      }
      Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        listOf(Triple(toArabic(hadithCount),"حديث","الحديث"),Triple(toArabic(lessonCount),"درس","التجويد"),Triple(toArabic(storyCount),"قصة","القصص")).forEach { stat ->
          Card(Modifier.weight(1f),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.outlineVariant)) {
            Column(Modifier.fillMaxWidth().padding(vertical=14.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(3.dp)) {
              Text(stat.first,fontSize=19.sp,fontWeight=FontWeight.Black,color=c.secondary)
              Text(stat.second,fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.onSurface)
              Text(stat.third,fontSize=9.sp,color=c.onSurfaceVariant)
            }
          }
        }
      }
    }
    item {
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
          Text("مَسَارَاتُكَ",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=c.onBackground)
          Text("كل أدواتك الأساسية في واجهة واحدة",fontSize=10.sp,color=c.onSurfaceVariant)
        }
        Text("٤ أقسام",fontSize=10.sp,color=c.secondary,fontWeight=FontWeight.Bold)
      }
      Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
        Card(Modifier.fillMaxWidth().clickable(onClick=onQuran),shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.42f))) {
          Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) {
            Box(Modifier.size(54.dp).background(Color(0xFF0B4938),RoundedCornerShape(17.dp)),contentAlignment=Alignment.Center){Text("۞",fontSize=32.sp,color=Color(0xFFE0BD72))}
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
              Text("المصحف الشريف",fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
              Text("قراءة الصفحات • فهرس السور • البحث والحفظ",fontSize=10.sp,color=c.onSurfaceVariant)
            }
            Text("‹",fontSize=26.sp,color=c.secondary,fontWeight=FontWeight.Bold)
          }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
          HomeTile("التسميع","راجع تلاوتك","◉",Modifier.weight(1f),onRecitation)
          HomeTile("مكتبة العلم","تجويد وحديث وفقه","✧",Modifier.weight(1f),onLearning)
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
          HomeTile("الأذكار","أوراد يومية","❈",Modifier.weight(1f),onAdhkar)
          HomeTile("المسبحة","عداد الذكر","◌",Modifier.weight(1f),onTasbih)
        }
      }
    }
    item {
      Surface(Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),color=c.surfaceVariant.copy(alpha=.7f)) {
        Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
          Text("✦",fontSize=18.sp,color=c.secondary)
          Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)) {
            Text("خطوة صغيرة كل يوم",fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.onSurface)
            Text("ابدأ بصفحة، أو آيات قليلة، واجعل الاستمرار رفيقك.",fontSize=10.sp,color=c.onSurfaceVariant)
          }
        }
      }
      Text("مُتْقِن  •  عناية بالقرآن علمًا وعملًا",Modifier.fillMaxWidth().padding(top=5.dp),textAlign=TextAlign.Center,fontSize=9.sp,color=c.onSurfaceVariant)
    }
  }
}
@Composable
private fun HomeTile(title:String,subtitle:String,symbol:String,modifier:Modifier,onClick:()->Unit) {
  val c=MaterialTheme.colorScheme
  Card(modifier.clickable(onClick=onClick),shape=RoundedCornerShape(24.dp),
    colors=CardDefaults.cardColors(containerColor=c.surface),
    border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.28f)),
    elevation=CardDefaults.cardElevation(defaultElevation=0.dp)) {
    Column(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=17.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
        Box(Modifier.size(44.dp).background(c.primary.copy(alpha=.12f),RoundedCornerShape(15.dp)).border(1.dp,c.secondary.copy(alpha=.32f),RoundedCornerShape(15.dp)),contentAlignment=Alignment.Center) {
          Text(symbol,fontSize=23.sp,color=c.secondary,fontWeight=FontWeight.Bold)
        }
        Text("۞",fontSize=17.sp,color=c.secondary.copy(alpha=.72f))
      }
      Text(title,fontSize=14.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
      Text(subtitle,fontSize=10.sp,lineHeight=17.sp,color=c.onSurfaceVariant)
      Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(c.outlineVariant))
        Text("  افتح القسم  ←",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
      }
    }
  }
}

@Composable
fun AdhkarScreen(onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  var category by remember { mutableStateOf("عام") }
  var showResetAdhkar by remember { mutableStateOf(false) }
  val context=LocalContext.current
  val store=remember { context.getSharedPreferences("mutqin_adhkar",android.content.Context.MODE_PRIVATE) }
  val dayKey=remember { java.text.SimpleDateFormat("yyyyMMdd",java.util.Locale.US).format(java.util.Date()) }
  val counts=remember(dayKey) { mutableStateMapOf<String,Int>().apply {
    listOf("عام" to 5,"الصباح" to 4,"المساء" to 4,"النوم" to 3).forEach { pair -> repeat(pair.second) { i -> val k="$dayKey-${pair.first}-$i"; this[k]=store.getInt(k,0) } }
  } }
  val categories=listOf("عام","الصباح","المساء","النوم")
  val adhkar=mapOf(
    "عام" to listOf("سبحان الله وبحمده، سبحان الله العظيم","أستغفر الله وأتوب إليه","لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير","اللهم صل وسلم على نبينا محمد","رضيت بالله رباً، وبالإسلام ديناً، وبمحمد صلى الله عليه وسلم نبياً"),
    "الصباح" to listOf("أصبحنا وأصبح الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له.","اللهم بك أصبحنا، وبك أمسينا، وبك نحيا، وبك نموت، وإليك النشور.","اللهم إني أسألك العفو والعافية في الدنيا والآخرة.","سبحان الله وبحمده."),
    "المساء" to listOf("أمسينا وأمسى الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له.","اللهم بك أمسينا، وبك أصبحنا، وبك نحيا، وبك نموت، وإليك المصير.","اللهم إني أسألك العفو والعافية في الدنيا والآخرة.","أستغفر الله وأتوب إليه."),
    "النوم" to listOf("باسمك اللهم أموت وأحيا.","اللهم قني عذابك يوم تبعث عبادك.","سبحان الله، والحمد لله، والله أكبر.")
  )
  val itemsNow=adhkar[category].orEmpty()
  val total=itemsNow.indices.sumOf { counts["$dayKey-$category-$it"] ?: 0 }
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=9.dp).background(c.surface,RoundedCornerShape(18.dp)).border(1.dp,c.secondary.copy(alpha=.24f),RoundedCornerShape(18.dp)).padding(horizontal=11.dp,vertical=5.dp),verticalAlignment=Alignment.CenterVertically) {
      TextButton(onClick=onBack){Text("‹  الرئيسية",fontWeight=FontWeight.Bold)}
      Spacer(Modifier.weight(1f))
      Column(horizontalAlignment=Alignment.End,verticalArrangement=Arrangement.spacedBy(1.dp)) {
        Text("مُتْقِن  •  الْوِرْدُ الْيَوْمِي",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
        Text("مَجْلِسُ الذِّكْر",fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
      }
    }
    LazyColumn(contentPadding=PaddingValues(start=16.dp,end=16.dp,top=4.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
      item {
        Card(shape=RoundedCornerShape(31.dp),colors=CardDefaults.cardColors(containerColor=c.primary),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.78f))) {
          Column(Modifier.fillMaxWidth().padding(21.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
            Text("وَرْدٌ يُنِيرُ الْيَوْم",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=1.2.sp)
            Text("اذكر الله بقلب حاضر",fontSize=24.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
            Text("مساحة هادئة لأذكارك اليومية، مع عدّاد مستقل لكل يوم يُحفظ على هذا الجهاز.",fontSize=12.sp,lineHeight=20.sp,color=c.onPrimary.copy(alpha=.82f))
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
              Surface(shape=RoundedCornerShape(12.dp),color=c.onPrimary.copy(alpha=.12f)){Text("${toArabic(itemsNow.size)} أذكار",Modifier.padding(horizontal=10.dp,vertical=8.dp),fontSize=10.sp,color=c.onPrimary,fontWeight=FontWeight.Bold)}
              Surface(shape=RoundedCornerShape(12.dp),color=c.secondary.copy(alpha=.18f)){Text("مجموع تكراراتك: ${toArabic(total)}",Modifier.padding(horizontal=10.dp,vertical=8.dp),fontSize=10.sp,color=c.onPrimary,fontWeight=FontWeight.Bold)}
            }
          }
        }
      }
      item {
        Text("اختر وِردك",fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=c.onBackground)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
          categories.forEach { name -> FilterChip(selected=category==name,onClick={category=name},label={Text(name,fontSize=11.sp)},modifier=Modifier.weight(1f)) }
        }
      }
      items(itemsNow.size) { i ->
        val key="$dayKey-$category-$i"
        val n=counts[key] ?: 0
        Card(shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.18f)),elevation=CardDefaults.cardElevation(defaultElevation=0.dp)) {
          Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
              Text("الذِّكر  ${toArabic(i+1)}",fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.secondary)
              Surface(shape=RoundedCornerShape(20.dp),color=c.primary.copy(alpha=.09f)){Text("التكرار  ${toArabic(n)}",Modifier.padding(horizontal=11.dp,vertical=6.dp),fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.primary)}
            }
            Text(itemsNow[i],Modifier.fillMaxWidth().padding(vertical=5.dp),fontSize=19.sp,lineHeight=34.sp,textAlign=TextAlign.Center,color=c.onSurface)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
              Button(onClick={counts[key]=n+1;store.edit().putInt(key,n+1).apply()},modifier=Modifier.weight(1f).height(46.dp),shape=RoundedCornerShape(14.dp)){Text("تسبيح  +١",fontWeight=FontWeight.Bold)}
              OutlinedButton(onClick={val v=(n-1).coerceAtLeast(0);counts[key]=v;store.edit().putInt(key,v).apply()},enabled=n>0,modifier=Modifier.height(46.dp),shape=RoundedCornerShape(14.dp)){Text("تراجع")}
            }
          }
        }
      }
      item {
        Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant)) {
          Column(Modifier.fillMaxWidth().padding(13.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
            Text("تنبيه توثيقي",fontSize=11.sp,fontWeight=FontWeight.Bold,color=c.primary)
            Text("هذه مجموعة مختارة. ينبغي مراجعة النصوص والأعداد والأوقات على مصادر الأذكار المعتمدة قبل اعتمادها كمكتبة موثقة.",fontSize=11.sp,lineHeight=18.sp,color=c.onSurfaceVariant)
            TextButton(onClick={showResetAdhkar=true}){Text("تصفير عدادات هذا القسم")}
          }
        }
      }
    }
  }
  if(showResetAdhkar) AlertDialog(
    onDismissRequest={showResetAdhkar=false},
    title={Text("تصفير عدادات هذا القسم؟")},
    text={Text("سيتم تصفير تكرارات قسم $category لهذا اليوم فقط، ولن تتأثر بقية الأقسام أو الأيام.")},
    confirmButton={TextButton(onClick={counts.keys.filter{it.startsWith("$dayKey-$category-")}.forEach{key->counts[key]=0;store.edit().putInt(key,0).apply()};showResetAdhkar=false}){Text("تصفير")}},
    dismissButton={TextButton(onClick={showResetAdhkar=false}){Text("إلغاء")}}
  )
}

@Composable
fun TasbihScreen(prefs:AppPrefs,onBack:()->Unit) {
  val c=MaterialTheme.colorScheme
  val context=LocalContext.current
  val phrasePrefs=remember { context.getSharedPreferences("mutqin_tasbih_phrases",android.content.Context.MODE_PRIVATE) }
  val initialPhraseIndex=remember { phrasePrefs.getInt("selected_phrase",0).coerceIn(0,4) }
  var phraseIndex by remember { mutableIntStateOf(initialPhraseIndex) }
  var count by remember { mutableIntStateOf(phrasePrefs.getInt("phrase_$initialPhraseIndex",if(initialPhraseIndex==0)prefs.tasbihCount() else 0)) }
  var target by remember { mutableIntStateOf(phrasePrefs.getInt("target",33).let { if(it in listOf(33,100,500,1000)) it else 33 }) }
  var showReset by remember { mutableStateOf(false) }
  val phrases=listOf("سُبْحَانَ اللَّهِ","الْحَمْدُ لِلَّهِ","اللَّهُ أَكْبَرُ","أَسْتَغْفِرُ اللَّهَ","أَذْكَرُ اللَّهَ")
  val targets=listOf(33,100,500,1000)
  val progress=(count.toFloat()/target).coerceIn(0f,1f)
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=9.dp).background(c.surface,RoundedCornerShape(18.dp)).border(1.dp,c.secondary.copy(alpha=.24f),RoundedCornerShape(18.dp)).padding(horizontal=11.dp,vertical=5.dp),verticalAlignment=Alignment.CenterVertically) {
      TextButton(onClick=onBack){Text("‹  الرئيسية",fontWeight=FontWeight.Bold)}
      Spacer(Modifier.weight(1f))
      Column(horizontalAlignment=Alignment.End,verticalArrangement=Arrangement.spacedBy(1.dp)) {
        Text("مُتْقِن  •  وِرْدُ الذِّكْر",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
        Text("مِسْبَاحُ مُتْقِن",fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
      }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=18.dp,vertical=8.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(14.dp)) {
      Card(shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=c.primary),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.78f))) {
        Column(Modifier.fillMaxWidth().padding(horizontal=21.dp,vertical=22.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
          Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("۞",fontSize=23.sp,color=c.secondary)
            Spacer(Modifier.width(8.dp))
            Text("لَا تَغْفُلْ عَنِ الذِّكْر",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=1.1.sp)
            Spacer(Modifier.weight(1f))
            Text("مُتْقِن",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.onPrimary.copy(alpha=.7f))
          }
          Text("مسبحة متقن",fontSize=26.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
          Text("اختر الذكر والهدف، ثم تابع عدّك بهدوء. يُحفظ كل عدّاد تلقائيًا.",fontSize=12.sp,lineHeight=20.sp,color=c.onPrimary.copy(alpha=.84f))
          Box(Modifier.fillMaxWidth().height(1.dp).background(c.secondary.copy(alpha=.42f)))
          Text("ذِكْرٌ يَسْكُنُ بِهِ الْقَلْب",fontSize=10.sp,color=c.secondary,fontWeight=FontWeight.Bold,letterSpacing=1.sp)
        }
      }
      Text("اختر الذكر",Modifier.fillMaxWidth(),fontSize=15.sp,fontWeight=FontWeight.Bold,color=c.onBackground)
      Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        phrases.chunked(3).forEach { rowPhrases ->
          Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            rowPhrases.forEach { phrase ->
              val index=phrases.indexOf(phrase)
              Surface(modifier=Modifier.weight(1f).clickable{phraseIndex=index;phrasePrefs.edit().putInt("selected_phrase",index).apply();count=phrasePrefs.getInt("phrase_$index",if(index==0)prefs.tasbihCount() else 0)},shape=RoundedCornerShape(17.dp),color=if(phraseIndex==index)c.primary else c.surface,border=androidx.compose.foundation.BorderStroke(if(phraseIndex==index)1.5.dp else 1.dp,if(phraseIndex==index)c.secondary else c.outlineVariant)) {
                Text(phrase,Modifier.fillMaxWidth().padding(horizontal=7.dp,vertical=13.dp),fontSize=10.sp,textAlign=TextAlign.Center,fontWeight=FontWeight.Bold,color=if(phraseIndex==index)c.onPrimary else c.onSurface,maxLines=2)
              }
            }
            if(rowPhrases.size<3) repeat(3-rowPhrases.size) { Spacer(Modifier.weight(1f)) }
          }
        }
      }
      Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.34f)),elevation=CardDefaults.cardElevation(defaultElevation=0.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)) {
          Text("الذكر المختار",fontSize=10.sp,color=c.onSurfaceVariant,fontWeight=FontWeight.Bold)
          Text(phrases[phraseIndex],fontSize=21.sp,fontWeight=FontWeight.Bold,color=c.primary,textAlign=TextAlign.Center)
          Box(Modifier.size(204.dp).background(c.primary.copy(alpha=.04f),RoundedCornerShape(120.dp)).padding(8.dp).background(c.surfaceVariant,RoundedCornerShape(120.dp)).border(1.5.dp,c.secondary.copy(alpha=.8f),RoundedCornerShape(120.dp)).clickable{
            count+=1;phrasePrefs.edit().putInt("phrase_$phraseIndex",count).apply();prefs.setTasbihCount(count)
          },contentAlignment=Alignment.Center) {
            Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
              Text(toArabic(count),fontSize=43.sp,fontWeight=FontWeight.Black,color=c.primary)
              Text("لِلْعَدِّ اضْغَطْ هُنَا",fontSize=10.sp,color=c.onSurfaceVariant,fontWeight=FontWeight.Bold)
            }
          }
          Text("الهدف  ${toArabic(target)}     •     المتبقي  ${toArabic((target-count).coerceAtLeast(0))}",fontSize=12.sp,color=c.secondary,fontWeight=FontWeight.Bold)
          LinearProgressIndicator(progress={progress},modifier=Modifier.fillMaxWidth().height(7.dp))
          Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            targets.forEach{t->FilterChip(selected=target==t,onClick={target=t;phrasePrefs.edit().putInt("target",t).apply()},label={Text(toArabic(t),fontSize=10.sp)},modifier=Modifier.weight(1f))}
          }
          Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            Button(onClick={count+=1;phrasePrefs.edit().putInt("phrase_$phraseIndex",count).apply();prefs.setTasbihCount(count)},modifier=Modifier.weight(1f).height(49.dp),shape=RoundedCornerShape(15.dp)){Text("تسبيح +١",fontWeight=FontWeight.Bold)}
            OutlinedButton(onClick={count=(count-1).coerceAtLeast(0);phrasePrefs.edit().putInt("phrase_$phraseIndex",count).apply();prefs.setTasbihCount(count)},enabled=count>0,modifier=Modifier.height(49.dp),shape=RoundedCornerShape(15.dp)){Text("تراجع −١")}
          }
          if(count>=target) Text("أحسنت! وصلت إلى هدفك، ويمكنك مواصلة الذكر.",fontSize=12.sp,color=c.tertiary,textAlign=TextAlign.Center,fontWeight=FontWeight.Bold)
        }
      }
      OutlinedButton(onClick={showReset=true},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp)){Text("إعادة عداد الذكر الحالي")}
      Text("أعدادك محفوظة على هذا الجهاز فقط.",fontSize=10.sp,color=c.onSurfaceVariant)
    }
  }
  if(showReset) AlertDialog(onDismissRequest={showReset=false},title={Text("إعادة عداد التسبيح؟")},text={Text("سيُعاد عدد الذكر المحدد حاليًا إلى صفر.")},
    confirmButton={TextButton(onClick={count=0;phrasePrefs.edit().putInt("phrase_$phraseIndex",0).apply();prefs.setTasbihCount(0);showReset=false}){Text("إعادة")}},
    dismissButton={TextButton(onClick={showReset=false}){Text("إلغاء")}})
}

private data class LearningSectionInfo(
  val title: String,
  val subtitle: String,
  val target: String,
  val scope: String,
  val status: String
)

private val learningSections = listOf(
  LearningSectionInfo("التجويد","تعلم أحكام التلاوة خطوة بخطوة","300 درس مستهدف","الحكم والتعريف والسبب والموضع والمثال والأداء والأخطاء والتمرين والاختبار.","المتاح فعليًا: ${tajweedLessons.size} درسًا"),
  LearningSectionInfo("التفسير","فهم معاني الآيات","تفسير 6,236 آية","تفسير الآيات وربط كل شرح بمصدره ومرجعه.","قيد البناء"),
  LearningSectionInfo("الحديث","السنة النبوية مع التخريج","7,000 حديث مستهدف","النص والراوي والكتاب والتخريج والدرجة والشرح، دون نسبة حكم بلا مرجع.","المتاح فعليًا: ${hadithEntries.size} حديثًا"),
  LearningSectionInfo("الفقه","مسائل العبادات والمعاملات","6,000 مسألة مستهدفة","السؤال والحكم والدليل والشرح والخلاف المعتبر والمراجع.","المتاح فعليًا: ${fiqhEntries.size} مدخلًا"),
  LearningSectionInfo("السيرة والصحابة","سير وتراجم موثقة","7,000 مادة","تراجم الصحابة والسيرة مع إحالات للمصادر وتمييز الروايات.","قيد البناء"),
  LearningSectionInfo("القصص الإسلامية","قصص الأنبياء والسيرة والتاريخ والصحابة","6,000 مادة مستهدفة","عرض القصة مع الأدلة والمراجع وبيان ما صح وما اختلف فيه.","المتاح فعليًا: ${quranStories.size} قصة ومادة"),
  LearningSectionInfo("الاختبارات","اختبر فهمك وحفظك","7,000 سؤال مستهدف","أسئلة متدرجة بإجابات مفسرة ومصدر لكل معلومة.","المتاح فعليًا: ${mutqinQuizQuestions.size} سؤالًا"),
  LearningSectionInfo("الأذكار والأدعية","أذكار موثقة حسب الوقت","مكتبة موسعة","النص والمصدر والدرجة والوقت والعدد الثابت عند ثبوته.","متاح: أذكار مختارة")
)

@Composable
fun LearningHubScreen(onBack:()->Unit,onOpen:(String)->Unit) {
  val c=MaterialTheme.colorScheme
  val publishedTotal=hadithEntries.size+tajweedLessons.size+fiqhEntries.size+quranStories.size+mutqinQuizQuestions.size
  val progressContext=LocalContext.current
  val progressPrefs=remember { AppPrefs(progressContext) }
  val completedTotal=learningSections.sumOf { progressPrefs.completedLearningIds(it.title).size }
  val studyProgress=if(publishedTotal>0) (completedTotal.toFloat()/publishedTotal).coerceIn(0f,1f) else 0f
  val sectionIcons=mapOf("التجويد" to "۞","الحديث" to "❖","الفقه" to "⚖","السيرة والصحابة" to "✧","القصص الإسلامية" to "❈","الاختبارات" to "؟","الأذكار والأدعية" to "◉")
  Column(Modifier.fillMaxSize().background(c.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=10.dp)
      .background(c.surface,RoundedCornerShape(20.dp))
      .border(1.dp,c.secondary.copy(alpha=.3f),RoundedCornerShape(20.dp))
      .padding(horizontal=12.dp, vertical=9.dp),verticalAlignment=Alignment.CenterVertically) {
      Surface(onClick=onBack,shape=RoundedCornerShape(12.dp),color=c.primary.copy(alpha=.09f)) {
        Text("‹",Modifier.padding(horizontal=14.dp,vertical=5.dp),fontSize=22.sp,fontWeight=FontWeight.Bold,color=c.primary)
      }
      Spacer(Modifier.weight(1f))
      Column(horizontalAlignment=Alignment.End,verticalArrangement=Arrangement.spacedBy(1.dp)) {
        Text("مُتْقِن  •  مَسَارُ الْعِلْم",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
        Text("المكتبة العلمية",fontSize=15.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
      }
    }
    LazyColumn(contentPadding=PaddingValues(start=16.dp,end=16.dp,top=5.dp,bottom=28.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
      item {
        Card(shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=c.primary),
          border=androidx.compose.foundation.BorderStroke(1.4.dp,c.secondary.copy(alpha=.78f))) {
          Column(Modifier.fillMaxWidth().padding(horizontal=24.dp,vertical=25.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
              Text("۞",fontSize=22.sp,color=c.secondary)
              Spacer(Modifier.width(7.dp))
              Text("مَسَارُ التَّعَلُّم",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
              Spacer(Modifier.weight(1f))
              Text("مُتْقِن",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.onPrimary.copy(alpha=.7f))
            }
            Text("مكتبةٌ للعلم والنور",fontSize=25.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
            Text("القرآن وعلومه والسنة والسيرة والفقه؛ تصفّح أبواب المعرفة في تجربة واحدة هادئة وواضحة.",fontSize=12.sp,lineHeight=21.sp,color=c.onPrimary.copy(alpha=.86f))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
              Surface(shape=RoundedCornerShape(12.dp),color=c.onPrimary.copy(alpha=.12f)) {
                Text("${toArabic(learningSections.size)} أقسام",Modifier.padding(horizontal=11.dp,vertical=8.dp),fontSize=11.sp,color=c.onPrimary,fontWeight=FontWeight.Bold)
              }
              Surface(shape=RoundedCornerShape(12.dp),color=c.secondary.copy(alpha=.18f)) {
                Text("${toArabic(publishedTotal)} مادة منشورة",Modifier.padding(horizontal=11.dp,vertical=8.dp),fontSize=11.sp,color=c.onPrimary,fontWeight=FontWeight.Bold)
              }
            }
          }
        }
      }
      item {
        Card(shape=RoundedCornerShape(23.dp),colors=CardDefaults.cardColors(containerColor=c.surface),
          border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.42f))) {
          Column(Modifier.fillMaxWidth().padding(17.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text("مِرْصَادُ التَّقَدُّم",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=1.sp)
                Text("خطواتك في طلب العلم",fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
              }
              Text("✦",fontSize=25.sp,color=c.secondary)
            }
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Bottom,horizontalArrangement=Arrangement.SpaceBetween) {
              Text("${toArabic(completedTotal)} مادة تمت مراجعتها",fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.primary)
              Text("من ${toArabic(publishedTotal)}",fontSize=11.sp,color=c.onSurfaceVariant)
            }
            LinearProgressIndicator(progress={studyProgress},modifier=Modifier.fillMaxWidth().height(7.dp),
              color=c.secondary,trackColor=c.surfaceVariant)
            Text(if(completedTotal==0) "ابدأ بأول درس أو حديث، وسيظهر تقدمك هنا تلقائيًا." else "واصل على مهل؛ تقدمك محفوظ على هذا الجهاز.",fontSize=11.sp,lineHeight=18.sp,color=c.onSurfaceVariant)
          }
        }
      }
      item {
        Text("استكشف الأقسام",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=c.onBackground)
        Text("افتح أي قسم لمتابعة المحتوى والتقدم المحفوظ على جهازك.",fontSize=11.sp,color=c.onSurfaceVariant)
      }
      items(learningSections.size) { i ->
        val item=learningSections[i]
        val publishedCount=when(item.title) {
          "التجويد" -> tajweedLessons.size
          "الحديث" -> hadithEntries.size
          "الفقه" -> fiqhEntries.size
          "القصص الإسلامية" -> quranStories.size
          "الاختبارات" -> mutqinQuizQuestions.size
          else -> 0
        }
        val status=if(publishedCount>0) "متاح: ${toArabic(publishedCount)} مادة" else "قيد البناء"
        Card(
          Modifier.fillMaxWidth().clickable { onOpen(item.title) },
          shape=RoundedCornerShape(24.dp),
          colors=CardDefaults.cardColors(containerColor=c.surface),
          border=androidx.compose.foundation.BorderStroke(1.2.dp,c.secondary.copy(alpha=.42f)),
          elevation=CardDefaults.cardElevation(defaultElevation=0.dp)
        ) {
          Column(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=19.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) {
              Box(Modifier.size(52.dp).background(c.primary.copy(alpha=.10f),RoundedCornerShape(17.dp)).border(1.dp,c.secondary.copy(alpha=.32f),RoundedCornerShape(17.dp)),contentAlignment=Alignment.Center) {
                Text(sectionIcons[item.title] ?: "✦",fontSize=25.sp,fontWeight=FontWeight.Bold,color=c.secondary)
              }
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                Text(item.title,fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=c.onSurface)
                Text(item.subtitle,fontSize=11.sp,color=c.onSurfaceVariant,lineHeight=18.sp)
              }
              Text("‹",fontSize=26.sp,color=c.secondary,fontWeight=FontWeight.Bold)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(c.outlineVariant.copy(alpha=.8f)))
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
              Surface(shape=RoundedCornerShape(30.dp),color=if(publishedCount>0)c.tertiary.copy(alpha=.12f) else c.error.copy(alpha=.09f)) {
                Text(status,Modifier.padding(horizontal=12.dp,vertical=7.dp),fontSize=10.sp,fontWeight=FontWeight.Bold,color=if(publishedCount>0)c.tertiary else c.error)
              }
              Text("استكشف القسم  ←",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            }
          }
        }
      }
      item {
        Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c.surfaceVariant)) {
          Text("الأعداد المعروضة هي المواد الموجودة حاليًا فقط. الأهداف المستقبلية لا تُحسب ضمن المنشور، ويجب استكمال التوثيق والمراجعة قبل اعتماد المحتوى.",Modifier.fillMaxWidth().padding(14.dp),fontSize=11.sp,lineHeight=18.sp,color=c.onSurfaceVariant)
        }
      }
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
  var quizIndex by remember(section) { mutableStateOf(0) }
  var quizSelected by remember(section) { mutableStateOf<Int?>(null) }
  var quizScore by remember(section) { mutableStateOf(0) }
  var showQuizResult by remember(section) { mutableStateOf(false) }
  val quizMistakes = remember(section) { mutableStateListOf<String>() }
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
    query.isBlank() || normalizeAr(listOf(story.title,story.summary,story.verses,story.lessons.joinToString(" "),story.sourceNote,story.chapters.joinToString(" ")).joinToString(" ")).contains(query)
  }
  LazyColumn(
    Modifier.fillMaxSize().background(c.background).safeDrawingPadding(),
    contentPadding=PaddingValues(16.dp),
    verticalArrangement=Arrangement.spacedBy(12.dp)
  ) {
    item {
      Column(Modifier.fillMaxWidth().background(c.primary).padding(horizontal=18.dp, vertical=15.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
          Surface(onClick=onBack,shape=RoundedCornerShape(13.dp),color=c.onPrimary.copy(alpha=.12f)) {
            Text("‹",Modifier.padding(horizontal=14.dp,vertical=7.dp),fontSize=23.sp,color=c.onPrimary)
          }
          Spacer(Modifier.weight(1f))
          Text("مُتْقِن  /  مَكْتَبَةُ الْعِلْم",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=1.sp)
        }
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Bottom,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
          Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(7.dp)) {
            Text("رِحْلَةُ تَعَلُّمٍ",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=2.sp)
            Text(section,fontSize=29.sp,fontWeight=FontWeight.Black,color=c.onPrimary)
            if(info!=null) Text(info.subtitle,fontSize=12.sp,lineHeight=20.sp,color=c.onPrimary.copy(alpha=.82f))
          }
          Box(Modifier.size(62.dp).background(c.onPrimary.copy(alpha=.10f),RoundedCornerShape(20.dp)).border(1.dp,c.secondary.copy(alpha=.55f),RoundedCornerShape(20.dp)),contentAlignment=Alignment.Center) {
            Text("۞",fontSize=35.sp,color=c.secondary)
          }
        }
        if(info!=null) {
          Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Surface(shape=RoundedCornerShape(12.dp),color=c.onPrimary.copy(alpha=.12f)) {
              Text("الهدف: ${info.target}",Modifier.padding(horizontal=11.dp,vertical=8.dp),fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.onPrimary)
            }
            Surface(shape=RoundedCornerShape(12.dp),color=c.secondary.copy(alpha=.18f)) {
              Text("مسار معرفي",Modifier.padding(horizontal=11.dp,vertical=8.dp),fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.onPrimary)
            }
          }
        }
      }
      if(section=="التجويد" || section=="الحديث" || section=="الفقه" || section=="القصص الإسلامية") {
        OutlinedTextField(value=searchQuery,onValueChange={searchQuery=it},modifier=Modifier.fillMaxWidth(),
          singleLine=true,shape=RoundedCornerShape(15.dp),label={Text("ابحث داخل ${section}")},placeholder={Text("كلمة أو عنوان أو مصدر")})
      }
      if(section=="التجويد" || section=="الحديث" || section=="الفقه" || section=="القصص الإسلامية") {
        val total=when(section){"التجويد"->tajweedLessons.size;"الحديث"->hadithEntries.size;"الفقه"->fiqhEntries.size;else->quranStories.size}
        val done=completedIds.size
        Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=c.surface),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.58f)),elevation=CardDefaults.cardElevation(defaultElevation=2.dp)) {
          Column(Modifier.fillMaxWidth().background(c.surfaceVariant.copy(alpha=.30f),RoundedCornerShape(20.dp)).padding(horizontal=16.dp,vertical=15.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
              Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(30.dp).background(c.primary.copy(alpha=.10f),RoundedCornerShape(10.dp)),contentAlignment=Alignment.Center) {
                  Text("۞",fontSize=18.sp,fontWeight=FontWeight.Bold,color=c.secondary)
                }
                Text("تقدمك في هذا القسم",fontSize=12.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
              }
              Surface(shape=RoundedCornerShape(10.dp),color=c.secondary.copy(alpha=.13f)) {
                Text("${toArabic(done)} / ${toArabic(total)}",Modifier.padding(horizontal=10.dp,vertical=6.dp),fontSize=11.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
              }
            }
            LinearProgressIndicator(progress={if(total==0)0f else done.toFloat()/total},modifier=Modifier.fillMaxWidth().height(7.dp),color=c.secondary,trackColor=c.outlineVariant,strokeCap=androidx.compose.ui.graphics.StrokeCap.Round)
            Text("ضع علامة بعد إكمال المادة؛ سيُحفظ تقدمك على هذا الجهاز.",fontSize=10.sp,color=c.onSurfaceVariant)
          }
        }
      }
    }
    if(section=="الاختبارات") {
      item {
        val question = mutqinQuizQuestions.getOrNull(quizIndex)
        Card(
          modifier=Modifier.fillMaxWidth(),
          shape=RoundedCornerShape(22.dp),
          colors=CardDefaults.cardColors(containerColor=c.surface),
          border=androidx.compose.foundation.BorderStroke(1.dp,c.primary.copy(alpha=.25f))
        ) {
          Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("اختبر معلوماتك",fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=c.primary)
            Text("بنك الأسئلة المنشور: ${mutqinQuizQuestions.size} سؤالًا من أصل 7,000 مستهدف.",fontSize=12.sp,color=c.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
              listOf("محاولات: ${toArabic(progressPrefs.quizAttempts())}","أفضل نتيجة: ${toArabic(progressPrefs.quizBestScore())}","آخر نتيجة: ${toArabic(progressPrefs.quizLastScore())}/${toArabic(progressPrefs.quizLastTotal())}").forEach { stat ->
                Surface(modifier=Modifier.weight(1f),shape=RoundedCornerShape(12.dp),color=c.surfaceVariant) {
                  Text(stat,modifier=Modifier.padding(horizontal=8.dp,vertical=9.dp),fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.primary,textAlign=TextAlign.Center)
                }
              }
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
              Text("السؤال ${toArabic(quizIndex+1)} من ${toArabic(mutqinQuizQuestions.size)}",fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.secondary)
              Text("نتيجتك: ${toArabic(quizScore)}",fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.primary)
            }
            LinearProgressIndicator(progress={ (quizIndex+1).toFloat()/mutqinQuizQuestions.size.coerceAtLeast(1) },modifier=Modifier.fillMaxWidth())
            if(question!=null) {
              Text(question.question,fontSize=18.sp,fontWeight=FontWeight.Bold,lineHeight=27.sp,color=c.onSurface)
              question.choices.forEachIndexed { choiceIndex, choice ->
                val chosen=quizSelected==choiceIndex
                val correct=choiceIndex==question.correctIndex
                val bg=if(quizSelected!=null && correct) Color(0xFF1E704A).copy(alpha=.16f) else if(chosen) c.error.copy(alpha=.12f) else c.surfaceVariant
                Surface(
                  modifier=Modifier.fillMaxWidth().clickable(enabled=quizSelected==null) {
                    quizSelected=choiceIndex
                    if(correct) quizScore++ else quizMistakes.add("س${toArabic(quizIndex+1)}: ${question.question}\nالإجابة الصحيحة: ${question.choices[question.correctIndex]}\nالتوضيح: ${question.explanation}")
                  },
                  shape=RoundedCornerShape(14.dp),
                  color=bg,
                  border=androidx.compose.foundation.BorderStroke(1.dp,if(quizSelected!=null && (correct || chosen)) (if(correct) Color(0xFF278553) else c.error) else c.outlineVariant)
                ) {
                  Text("${listOf("أ","ب","ج","د").getOrElse(choiceIndex){"•"}}. $choice",modifier=Modifier.padding(14.dp),fontSize=14.sp,color=c.onSurface)
                }
              }
              if(quizSelected!=null) {
                val isCorrect=quizSelected==question.correctIndex
                Text(if(isCorrect) "إجابة صحيحة، أحسنت!" else "الإجابة الصحيحة: ${question.choices[question.correctIndex]}",fontSize=14.sp,fontWeight=FontWeight.Bold,color=if(isCorrect) Color(0xFF278553) else c.error)
                Text(question.explanation,fontSize=13.sp,lineHeight=21.sp,color=c.onSurface)
                Text("المرجع: ${question.reference}",fontSize=11.sp,lineHeight=17.sp,color=c.onSurfaceVariant)
              }
              Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick={quizIndex=0;quizSelected=null;quizScore=0;quizMistakes.clear();showQuizResult=false},modifier=Modifier.weight(1f)) { Text("إعادة الاختبار") }
                Button(
                  onClick={
                    if(quizIndex<mutqinQuizQuestions.lastIndex) { quizIndex++; quizSelected=null }
                    else { progressPrefs.saveQuizResult(quizScore,mutqinQuizQuestions.size); showQuizResult=true }
                  },
                  enabled=quizSelected!=null,
                  modifier=Modifier.weight(1f)
                ) { Text(if(quizIndex==mutqinQuizQuestions.lastIndex) "إنهاء وبدء من جديد" else "السؤال التالي") }
              }
            }
          }
        }
      }
    } else if(section=="التجويد") {
      item {
        Card(colors=CardDefaults.cardColors(containerColor=c.primary),shape=RoundedCornerShape(22.dp),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.7f))) {
          Column(Modifier.fillMaxWidth().padding(horizontal=17.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text("مِيزَانُ التَّلَاوَة",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary,letterSpacing=1.4.sp)
                Text("رحلتك في إتقان التجويد",fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
              }
              Text("۞",fontSize=30.sp,color=c.secondary)
            }
            Text("${tajweedLessons.size} درسًا متاحًا من أصل ٣٠٠",fontSize=12.sp,fontWeight=FontWeight.Bold,color=c.onPrimary)
            LinearProgressIndicator(progress={ (tajweedLessons.size/300f).coerceIn(0f,1f) },modifier=Modifier.fillMaxWidth().height(6.dp),color=c.secondary,trackColor=c.onPrimary.copy(alpha=.18f))
            Text("تعرّف على القاعدة، تأمل الأمثلة، ثم راجع التدريب والأخطاء الشائعة والمراجع. المحتوى قيد الاستكمال والمراجعة العلمية.",fontSize=11.sp,lineHeight=18.sp,color=c.onPrimary.copy(alpha=.82f))
          }
        }
      }
      items(filteredTajweed.size) { index ->
        val lesson=filteredTajweed[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(21.dp),border=androidx.compose.foundation.BorderStroke(1.1.dp,c.secondary.copy(alpha=.38f)),elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
          Column(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)) {
              Box(Modifier.size(35.dp).background(c.primary.copy(alpha=.09f),RoundedCornerShape(11.dp)).border(1.dp,c.secondary.copy(alpha=.4f),RoundedCornerShape(11.dp)),contentAlignment=Alignment.Center) {
                Text(toArabic(lesson.id),fontSize=12.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary)
              }
              Text(lesson.title,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=c.primary,modifier=Modifier.weight(1f))
              Text("تجويد",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            }
            Divider(color=c.outlineVariant.copy(alpha=.7f),thickness=.8.dp)
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
        Card(colors=CardDefaults.cardColors(containerColor=c.primary),shape=RoundedCornerShape(22.dp),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.65f))) {
          Column(Modifier.fillMaxWidth().padding(horizontal=17.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text("مِنْ هَدْيِ النَّبِي ﷺ",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
                Text("مكتبة الحديث الشريف",fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
              }
              Text("۞",fontSize=29.sp,color=c.secondary)
            }
            Text("الأحاديث المنشورة: ${hadithEntries.size}  •  نتائج البحث: ${filteredHadith.size}",fontSize=11.sp,fontWeight=FontWeight.SemiBold,color=c.onPrimary.copy(alpha=.86f))
            Text("اقرأ الحديث، وتعرّف على راويه ومصدره ودرجته وشرحه.",fontSize=11.sp,color=c.onPrimary.copy(alpha=.78f))
          }
        }
        if(filteredHadith.isEmpty()) Text("لا توجد أحاديث تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
      }
      items(filteredHadith.size) { index ->
        val h=filteredHadith[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(21.dp),border=androidx.compose.foundation.BorderStroke(1.1.dp,c.secondary.copy(alpha=.38f)),elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
          Column(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)) {
              Box(Modifier.size(34.dp).background(c.primary.copy(alpha=.09f),RoundedCornerShape(11.dp)).border(1.dp,c.secondary.copy(alpha=.4f),RoundedCornerShape(11.dp)),contentAlignment=Alignment.Center) {
                Text(toArabic(h.id),fontSize=11.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary)
              }
              Text(h.title,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=c.primary,modifier=Modifier.weight(1f))
              Text("حديث",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            }
            Divider(color=c.outlineVariant.copy(alpha=.7f),thickness=.8.dp)
            Text(h.text,fontSize=19.sp,lineHeight=33.sp,color=c.onSurface,textAlign=TextAlign.Center)
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
        Card(colors=CardDefaults.cardColors(containerColor=c.primary),shape=RoundedCornerShape(22.dp),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.65f))) {
          Column(Modifier.fillMaxWidth().padding(horizontal=17.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text("فَهْمٌ وَعَمَل",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
                Text("دليل الفقه الميسّر",fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
              }
              Text("۩",fontSize=29.sp,color=c.secondary)
            }
            Text("المسائل: ${fiqhEntries.size}  •  نتائج البحث: ${filteredFiqh.size}",fontSize=11.sp,fontWeight=FontWeight.SemiBold,color=c.onPrimary.copy(alpha=.88f))
            Text("مداخل تعليمية عامة، وليست فتاوى شخصية؛ راجع أهل العلم فيما يستجد.",fontSize=11.sp,color=c.onPrimary.copy(alpha=.8f),lineHeight=17.sp)
          }
        }
        if(filteredFiqh.isEmpty()) Text("لا توجد مسائل تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
      }
      items(filteredFiqh.size) { index ->
        val f=filteredFiqh[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(21.dp),border=androidx.compose.foundation.BorderStroke(1.1.dp,c.secondary.copy(alpha=.38f)),elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
          Column(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)) {
              Box(Modifier.size(34.dp).background(c.primary.copy(alpha=.09f),RoundedCornerShape(11.dp)).border(1.dp,c.secondary.copy(alpha=.4f),RoundedCornerShape(11.dp)),contentAlignment=Alignment.Center) {
                Text(toArabic(f.id),fontSize=11.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary)
              }
              Text(f.title,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=c.primary,modifier=Modifier.weight(1f))
              Text("فقه",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            }
            Divider(color=c.outlineVariant.copy(alpha=.7f),thickness=.8.dp)
            Text("السؤال",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            Text(f.question,fontSize=14.sp,fontWeight=FontWeight.SemiBold,color=c.onSurface)
            Text("الخلاصة",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            Text(f.ruling,fontSize=14.sp,lineHeight=23.sp,color=c.onSurface)
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
        Card(colors=CardDefaults.cardColors(containerColor=c.primary),shape=RoundedCornerShape(22.dp),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.65f))) {
          Column(Modifier.fillMaxWidth().padding(horizontal=17.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
              Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text("عِبَرٌ وَآيَات",fontSize=10.sp,fontWeight=FontWeight.Bold,color=c.secondary)
                Text("رحاب القصص الإسلامية",fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=c.onPrimary)
              }
              Text("۞",fontSize=29.sp,color=c.secondary)
            }
            Text("القصص المنشورة: ${quranStories.size}  •  نتائج البحث: ${filteredStories.size}",fontSize=11.sp,fontWeight=FontWeight.SemiBold,color=c.onPrimary.copy(alpha=.88f))
            Text("تأمل الملخص والآيات والدروس المستفادة، وتحقق من الروايات من مصادرها.",fontSize=11.sp,lineHeight=17.sp,color=c.onPrimary.copy(alpha=.8f))
          }
        }
        if(filteredStories.isEmpty()) Text("لا توجد قصص تطابق عبارة البحث.",fontSize=13.sp,color=c.onSurfaceVariant)
      }
      items(filteredStories.size) { index ->
        val story=filteredStories[index]
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(21.dp),border=androidx.compose.foundation.BorderStroke(1.1.dp,c.secondary.copy(alpha=.38f)),elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
          Column(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)) {
              Box(Modifier.size(35.dp).background(c.primary.copy(alpha=.09f),RoundedCornerShape(12.dp)).border(1.dp,c.secondary.copy(alpha=.4f),RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center) {
                Text("${story.id}",fontSize=11.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary)
              }
              Text(story.title,fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=c.primary,modifier=Modifier.weight(1f))
              Text("قصة",fontSize=9.sp,fontWeight=FontWeight.Bold,color=c.secondary)
            }
            Divider(color=c.outlineVariant.copy(alpha=.7f),thickness=.8.dp)
            Text(story.summary,fontSize=14.sp,lineHeight=25.sp,color=c.onSurface)
            Text("مواضع القصة: ${story.verses}",fontSize=13.sp,fontWeight=FontWeight.SemiBold,color=c.secondary)
            if(story.chapters.isNotEmpty()) {
              Text("النسخة المطوّلة: ${story.chapters.size} فصول",fontSize=14.sp,fontWeight=FontWeight.Bold,color=c.primary)
              story.chapters.forEachIndexed { chapterIndex, chapter ->
                Card(colors=CardDefaults.cardColors(containerColor=c.surfaceVariant),shape=RoundedCornerShape(19.dp),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.24f))) {
                  Column(Modifier.fillMaxWidth().padding(horizontal=15.dp,vertical=13.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                      Surface(shape=RoundedCornerShape(10.dp),color=c.primary.copy(alpha=.10f)) {
                        Text("الفصل " + toArabic(chapterIndex+1),Modifier.padding(horizontal=11.dp,vertical=6.dp),fontSize=10.sp,fontWeight=FontWeight.ExtraBold,color=c.secondary)
                      }
                      Spacer(Modifier.weight(1f))
                      Text("من رحاب القصة",fontSize=10.sp,fontWeight=FontWeight.SemiBold,color=c.onSurfaceVariant)
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(c.outlineVariant.copy(alpha=.75f)))
                    Text(chapter,Modifier.fillMaxWidth(),fontSize=14.sp,lineHeight=26.sp,color=c.onSurface)
                  }
                }
              }
            } else {
              Text("هذه القصة لم تُضف إليها فصول مطوّلة بعد؛ سيظهر محتواها المتاح فقط دون ادعاء اكتمالها.",fontSize=12.sp,lineHeight=19.sp,color=c.error)
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
        Card(colors=CardDefaults.cardColors(containerColor=c.surface),shape=RoundedCornerShape(18.dp),border=androidx.compose.foundation.BorderStroke(1.dp,c.secondary.copy(alpha=.22f))) {
          Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text("نطاق القسم",fontWeight=FontWeight.Bold,color=c.primary)
            if(info!=null) Text(info.scope,fontSize=14.sp,lineHeight=23.sp,color=c.onSurface)
            Text("المحتوى المنشور في هذا القسم لم يكتمل بعد. ستظهر كل مادة مع مصدرها ومرجعها وحالة التحقق.",fontSize=13.sp,lineHeight=21.sp,color=c.error)
          }
        }
      }
    }
  }
  if(section=="الاختبارات" && showQuizResult) {
    val percent=(quizScore*100f/mutqinQuizQuestions.size.coerceAtLeast(1)).toInt()
    AlertDialog(
      onDismissRequest={showQuizResult=false},
      title={Text("اكتمل الاختبار",fontWeight=FontWeight.ExtraBold)},
      text={Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
        Text("نتيجتك: ${toArabic(quizScore)} من ${toArabic(mutqinQuizQuestions.size)}",fontSize=17.sp,fontWeight=FontWeight.Bold,color=c.primary)
        Text("النسبة: ${toArabic(percent)}٪",fontSize=15.sp,fontWeight=FontWeight.Bold)
        LinearProgressIndicator(progress={percent/100f},modifier=Modifier.fillMaxWidth())
        Text(when { percent>=90 -> "ممتاز! إتقان رائع، واصل المراجعة."; percent>=70 -> "أحسنت! بقيت نقاط قليلة لتتقنها."; percent>=50 -> "بداية جيدة؛ راجع الإجابات والمراجع."; else -> "لا بأس، أعد المحاولة بعد مراجعة الشرح." },fontSize=13.sp,lineHeight=21.sp)
        Text("أفضل نتيجة محفوظة: ${toArabic(progressPrefs.quizBestScore())}",fontSize=11.sp,color=c.onSurfaceVariant)
        if(quizMistakes.isNotEmpty()) {
          Text("مراجعة الإجابات التي أخطأت فيها",fontSize=13.sp,fontWeight=FontWeight.Bold,color=c.error)
          quizMistakes.forEach { mistake -> Text(mistake,fontSize=11.sp,lineHeight=18.sp,color=c.onSurface) }
        } else {
          Text("أجبت عن جميع الأسئلة إجابة صحيحة في هذه المحاولة.",fontSize=12.sp,color=c.tertiary)
        }
      }},
      confirmButton={TextButton(onClick={quizIndex=0;quizSelected=null;quizScore=0;quizMistakes.clear();showQuizResult=false}){Text("اختبار جديد")}},
      dismissButton={TextButton(onClick={showQuizResult=false}){Text("إغلاق")}}
    )
  }
}
