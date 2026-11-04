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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

private data class DhikrKind(val key: String, val title: String, val phrase: String, val defaultGoal: Int)

@Composable
fun DailyToolsScreen(prefs: AppPrefs, onBack: () -> Unit) {
  val colors = MaterialTheme.colorScheme
  val context = LocalContext.current
  val today = LocalDate.now().toString()
  val kinds = listOf(
    DhikrKind("tasbih", "التسبيح", "سبحان الله", 33),
    DhikrKind("tahmid", "التحميد", "الحمد لله", 33),
    DhikrKind("takbir", "التكبير", "الله أكبر", 34),
    DhikrKind("istighfar", "الاستغفار", "أستغفر الله", 100),
    DhikrKind("salawat", "الصلاة على النبي", "اللهم صل وسلم على نبينا محمد", 100)
  )
  var selected by remember { mutableIntStateOf(0) }
  var refresh by remember { mutableIntStateOf(0) }
  var showGoals by remember { mutableStateOf(false) }
  var verseGoal by remember { mutableIntStateOf(prefs.toolInt("vgoal_$today", 20).coerceAtLeast(1)) }
  var memorGoal by remember { mutableIntStateOf(prefs.toolInt("mgoal_$today", 5).coerceAtLeast(1)) }
  var reviewGoal by remember { mutableIntStateOf(prefs.toolInt("rgoal_$today", 10).coerceAtLeast(1)) }
  var verseCount by remember { mutableIntStateOf(prefs.toolInt("verses_$today")) }
  var memorCount by remember { mutableIntStateOf(prefs.toolInt("memor_$today")) }
  var reviewCount by remember { mutableIntStateOf(prefs.toolInt("review_$today")) }
  var note by remember { mutableStateOf(prefs.toolText("note_$today")) }
  var goalInput by remember { mutableStateOf("") }
  val prayers = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")
  var checked by remember { mutableStateOf(prayers.associateWith { prefs.toolFlag("prayer_${today}_$it") }) }
  val tasks = listOf("قراءة ورد القرآن", "حفظ آيات جديدة", "مراجعة المحفوظ", "الاستماع لتلاوة متقنة")
  var taskChecked by remember { mutableStateOf(tasks.associateWith { prefs.toolFlag("task_${today}_$it") }) }
  fun count(k: DhikrKind) = prefs.toolInt("dh_${today}_${k.key}")
  fun goal(k: DhikrKind) = prefs.toolInt("dhgoal_${k.key}", k.defaultGoal).coerceAtLeast(1)
  fun updateCount(key: String, value: Int) { prefs.setToolInt(key, value.coerceAtLeast(0)); refresh++ }
  fun progress(value: Int, target: Int) = (value.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)

  Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Column(Modifier.weight(1f)) {
        Text("لوحة رحلتك", fontSize = 25.sp, fontWeight = FontWeight.Black, color = colors.primary)
        Text("خطوات صغيرة ثابتة كل يوم", fontSize = 11.sp, color = colors.onSurfaceVariant)
      }
      Surface(shape = CircleShape, color = colors.primary.copy(alpha = .10f)) { Text(today, Modifier.padding(horizontal = 10.dp, vertical = 7.dp), fontSize = 10.sp, color = colors.primary) }
    }

    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = colors.primary)) {
      Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("﴿ فَاذْكُرُونِي أَذْكُرْكُمْ ﴾", color = colors.onPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("اختر وردك، تابع قراءتك، وسجّل إنجازك اليومي.", color = colors.onPrimary.copy(alpha = .86f), fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          MiniStat("الصلوات", "${checked.values.count { it }}/٥", colors.onPrimary)
          MiniStat("آيات اليوم", "$verseCount", colors.onPrimary)
          MiniStat("المهام", "${taskChecked.values.count { it }}/٤", colors.onPrimary)
        }
      }
    }

    SectionTitle("أذكارك", "خمسة أذكار بعدّادات وأهداف مستقلة")
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
      kinds.forEachIndexed { i, k ->
        FilterChip(selected = selected == i, onClick = { selected = i; refresh++ }, label = { Text(k.title, fontSize = 10.sp) })
      }
    }
    val active = kinds[selected]
    val activeCount = count(active)
    val activeGoal = goal(active)
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(17.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(active.phrase, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = colors.primary, textAlign = TextAlign.Center)
        Text(activeCount.toString(), fontSize = 54.sp, fontWeight = FontWeight.Black, color = colors.onSurface)
        Text("الهدف $activeGoal • المتبقي ${(activeGoal-activeCount).coerceAtLeast(0)}", fontSize = 11.sp, color = colors.onSurfaceVariant)
        LinearProgressIndicator(progress = { progress(activeCount, activeGoal) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape))
        Button(onClick = { updateCount("dh_${today}_${active.key}", activeCount+1) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(17.dp)) { Text("＋  تسبيحة واحدة", fontSize = 17.sp, fontWeight = FontWeight.Bold) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(onClick = { updateCount("dh_${today}_${active.key}", activeCount-1) }, modifier = Modifier.weight(1f)) { Text("تراجع −١") }
          OutlinedButton(onClick = { updateCount("dh_${today}_${active.key}", 0) }, modifier = Modifier.weight(1f)) { Text("تصفير هذا الذكر") }
          OutlinedButton(onClick = { goalInput = activeGoal.toString(); showGoals = true }, modifier = Modifier.weight(1f)) { Text("الهدف") }
        }
      }
    }
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)) {
      Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text("ملخص الأذكار الخمسة", fontWeight = FontWeight.Bold)
        kinds.forEach { k ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(k.title, Modifier.weight(1f), fontSize = 12.sp)
            Text("${count(k)} / ${goal(k)}", fontSize = 11.sp, color = colors.primary, fontWeight = FontWeight.Bold)
            TextButton(onClick = { selected = kinds.indexOf(k) }) { Text("فتح", fontSize = 11.sp) }
          }
          LinearProgressIndicator(progress = { progress(count(k), goal(k)) }, modifier = Modifier.fillMaxWidth().height(5.dp))
        }
        TextButton(onClick = { kinds.forEach { updateCount("dh_${today}_${it.key}", 0) } }, modifier = Modifier.align(Alignment.End)) { Text("تصفير جميع أذكار اليوم") }
      }
    }

    SectionTitle("ورد القرآن", "ثلاثة مسارات منفصلة للقراءة والحفظ والمراجعة")
    ProgressCard("القراءة", verseCount, verseGoal, "آية", { verseCount++; prefs.setToolInt("verses_$today", verseCount) }, { verseCount=(verseCount-1).coerceAtLeast(0); prefs.setToolInt("verses_$today", verseCount) }, { verseCount=0; prefs.setToolInt("verses_$today",0) }, colors)
    ProgressCard("الحفظ الجديد", memorCount, memorGoal, "آية", { memorCount++; prefs.setToolInt("memor_$today", memorCount) }, { memorCount=(memorCount-1).coerceAtLeast(0); prefs.setToolInt("memor_$today", memorCount) }, { memorCount=0; prefs.setToolInt("memor_$today",0) }, colors)
    ProgressCard("المراجعة", reviewCount, reviewGoal, "آية", { reviewCount++; prefs.setToolInt("review_$today", reviewCount) }, { reviewCount=(reviewCount-1).coerceAtLeast(0); prefs.setToolInt("review_$today", reviewCount) }, { reviewCount=0; prefs.setToolInt("review_$today",0) }, colors)
    OutlinedButton(onClick = { showGoals=true; goalInput="$verseGoal,$memorGoal,$reviewGoal" }, modifier = Modifier.fillMaxWidth()) { Text("تعديل أهداف القراءة والحفظ والمراجعة") }

    SectionTitle("متابعة يومك", "سجّل ما أنجزته بنفسك")
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp)) {
        Text("الصلوات الخمس", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        prayers.forEach { prayer ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked[prayer] == true, onCheckedChange = { v -> checked=checked.toMutableMap().apply{put(prayer,v)}; prefs.setToolFlag("prayer_${today}_$prayer",v) })
            Text(prayer, Modifier.weight(1f))
            Text(if (checked[prayer] == true) "تم تسجيلها" else "لم تُسجل", fontSize = 10.sp, color = colors.onSurfaceVariant)
          }
        }
      }
    }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("مهام قرآنية يومية", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        tasks.forEach { task ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = taskChecked[task] == true, onCheckedChange = { v -> taskChecked=taskChecked.toMutableMap().apply{put(task,v)}; prefs.setToolFlag("task_${today}_$task",v) })
            Text(task, Modifier.weight(1f), fontSize = 12.sp)
          }
        }
      }
    }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("دفتر التدبر وملاحظات الحفظ", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value=note, onValueChange={ note=it; prefs.setToolText("note_$today",it) }, modifier=Modifier.fillMaxWidth(), minLines=3, label={Text("اكتب فائدة أو آية تريد الرجوع إليها")})
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(onClick = { if (note.isNotBlank()) { val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(android.content.Intent.EXTRA_TEXT, note) }; context.startActivity(android.content.Intent.createChooser(send, "مشاركة الملاحظة")) } }, modifier=Modifier.weight(1f)) { Text("مشاركة الملاحظة") }
          OutlinedButton(onClick = { note=""; prefs.setToolText("note_$today","") }, modifier=Modifier.weight(1f)) { Text("مسح الملاحظة") }
        }
      }
    }
    Text("كل العدادات والمتابعات يدوية ومحفوظة محليًا على جهازك. لا يتحقق التطبيق تلقائيًا من أداء الصلاة أو صحة الحفظ.", color=colors.onSurfaceVariant, fontSize=10.sp, textAlign=TextAlign.Center, modifier=Modifier.fillMaxWidth().padding(bottom=12.dp))
  }

  if (showGoals) {
    AlertDialog(onDismissRequest={showGoals=false}, title={Text("تعديل الأهداف")}, text={
      Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value=goalInput,onValueChange={goalInput=it.filter{ch->ch.isDigit()||ch==','}.take(16)},label={Text(if (goalInput.contains(",")) "أهداف القراءة، الحفظ، المراجعة" else "هدف ${activeGoalLabel(kinds[selected])}")},singleLine=true)
        Text("لأهداف القرآن اكتب ثلاثة أرقام مفصولة بفواصل، مثل ٢٠، ٥، ١٠.", fontSize=10.sp, color=colors.onSurfaceVariant)
      }
    }, confirmButton={ TextButton(onClick={
      if (goalInput.contains(",")) {
        val p=goalInput.split(",").mapNotNull{it.toIntOrNull()?.coerceIn(1,9999)}
        if(p.size==3){verseGoal=p[0];memorGoal=p[1];reviewGoal=p[2];prefs.setToolInt("vgoal_$today",verseGoal);prefs.setToolInt("mgoal_$today",memorGoal);prefs.setToolInt("rgoal_$today",reviewGoal)}
      } else goalInput.toIntOrNull()?.coerceIn(1,9999)?.let{prefs.setToolInt("dhgoal_${kinds[selected].key}",it)}
      showGoals=false; refresh++
    }){Text("حفظ")} }, dismissButton={TextButton(onClick={showGoals=false}){Text("إلغاء")}})
  }
}

@Composable
private fun MiniStat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
  Surface(color=color.copy(alpha=.14f), shape=RoundedCornerShape(12.dp)) { Column(Modifier.padding(horizontal=10.dp,vertical=7.dp)) { Text(value,color=color,fontWeight=FontWeight.Bold); Text(label,color=color,fontSize=9.sp) } }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
  Column(verticalArrangement=Arrangement.spacedBy(2.dp)) {
    Text(title,fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=MaterialTheme.colorScheme.onBackground)
    Text(subtitle,fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun ProgressCard(title: String, value: Int, target: Int, unit: String, add: () -> Unit, undo: () -> Unit, reset: () -> Unit, colors: androidx.compose.material3.ColorScheme) {
  Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
    Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
      Row(verticalAlignment=Alignment.CenterVertically) { Text(title,Modifier.weight(1f),fontWeight=FontWeight.Bold); Text("$value / $target $unit",color=colors.primary,fontSize=12.sp,fontWeight=FontWeight.Bold) }
      LinearProgressIndicator(progress={(value.toFloat()/target.coerceAtLeast(1)).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth().height(6.dp))
      Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
        Button(onClick=add,modifier=Modifier.weight(1f)){Text("+١")}
        OutlinedButton(onClick=undo,modifier=Modifier.weight(1f)){Text("تراجع")}
        OutlinedButton(onClick=reset,modifier=Modifier.weight(1f)){Text("تصفير")}
      }
    }
  }
}

private fun activeGoalLabel(kind: DhikrKind) = kind.title
