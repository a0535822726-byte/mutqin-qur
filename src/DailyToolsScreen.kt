package com.mutqin.app

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

@Composable
fun DailyToolsScreen(prefs: AppPrefs, onBack: () -> Unit) {
  var count by remember { mutableIntStateOf(prefs.toolInt("dhikr_count")) }
  var goal by remember { mutableIntStateOf(prefs.toolInt("dhikr_goal", 33).coerceAtLeast(1)) }
  var verses by remember { mutableIntStateOf(prefs.toolInt("daily_verses")) }
  var verseGoal by remember { mutableIntStateOf(prefs.toolInt("verse_goal", 20).coerceAtLeast(1)) }
  var note by remember { mutableStateOf(prefs.toolText("reflection_note")) }
  var saved by remember { mutableStateOf(false) }
  var showGoal by remember { mutableStateOf(false) }
  var goalText by remember { mutableStateOf(goal.toString()) }
  var verseGoalText by remember { mutableStateOf(verseGoal.toString()) }
  val prayers = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")
  var checked by remember { mutableStateOf(prayers.associateWith { prefs.toolFlag("prayer_$it") }) }
  val colors = MaterialTheme.colorScheme

  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Column(Modifier.weight(1f)) {
        Text("مركز إنجازك اليومي", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
        Text("أدوات تحفظ تقدمك على هذا الجهاز", fontSize = 11.sp, color = colors.onSurfaceVariant)
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = colors.surface), shape = RoundedCornerShape(22.dp)) {
      Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("مسبحة إلكترونية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(count.toString(), fontSize = 48.sp, fontWeight = FontWeight.Black, color = colors.primary)
        Text("الهدف: $goal  •  المتبقي: ${(goal-count).coerceAtLeast(0)}", color = colors.onSurfaceVariant)
        LinearProgressIndicator(progress = { (count.toFloat()/goal).coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { count++; prefs.setToolInt("dhikr_count", count) }) { Text("تسبيح +١") }
          OutlinedButton(onClick = { count = (count-1).coerceAtLeast(0); prefs.setToolInt("dhikr_count", count) }) { Text("−١") }
          OutlinedButton(onClick = { count=0; prefs.setToolInt("dhikr_count",0) }) { Text("تصفير") }
        }
        TextButton(onClick = { showGoal = true; goalText = goal.toString(); verseGoalText = verseGoal.toString() }) { Text("تعديل الأهداف") }
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = colors.surface), shape = RoundedCornerShape(22.dp)) {
      Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("هدف القراءة اليومي", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("أنجزت $verses من $verseGoal آية", color = colors.primary, fontWeight = FontWeight.SemiBold)
        LinearProgressIndicator(progress = { (verses.toFloat()/verseGoal).coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { verses++; prefs.setToolInt("daily_verses", verses) }) { Text("أتممت آية +١") }
          OutlinedButton(onClick = { verses=(verses-1).coerceAtLeast(0); prefs.setToolInt("daily_verses",verses) }) { Text("تراجع") }
          OutlinedButton(onClick = { verses=0; prefs.setToolInt("daily_verses",0) }) { Text("بدء جديد") }
        }
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = colors.surface), shape = RoundedCornerShape(22.dp)) {
      Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("متابعة الصلوات", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("ضع علامة بعد أداء الصلاة؛ يمكنك تعديلها في أي وقت.", fontSize = 11.sp, color = colors.onSurfaceVariant)
        prayers.forEach { prayer ->
          Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked[prayer] == true, onCheckedChange = { value ->
              checked = checked.toMutableMap().apply { put(prayer, value) }
              prefs.setToolFlag("prayer_$prayer", value)
            })
            Text(prayer, Modifier.weight(1f))
            Text(if (checked[prayer] == true) "تم" else "لم تُعلّم", color = colors.onSurfaceVariant, fontSize = 11.sp)
          }
        }
        Text("المجموع: ${checked.values.count { it }} من ٥", color = colors.primary, fontWeight = FontWeight.Bold)
      }
    }

    Card(colors = CardDefaults.cardColors(containerColor = colors.surface), shape = RoundedCornerShape(22.dp)) {
      Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("تأملاتك وملاحظات الحفظ", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        OutlinedTextField(value = note, onValueChange = { note=it; saved=false }, modifier = Modifier.fillMaxWidth(), minLines = 3, label = { Text("اكتب ملاحظة أو موضعًا تريد مراجعته") })
        Button(onClick = { prefs.setToolText("reflection_note", note); saved=true }, modifier = Modifier.fillMaxWidth()) { Text("حفظ الملاحظة") }
        if (saved) Text("حُفظت ملاحظتك على الجهاز.", color = colors.primary, fontSize = 12.sp)
      }
    }
    Text("الأدوات تحفظ بياناتها محليًا على هاتفك. عدّاد القراءة والصلوات يدوي، ولا يتحقق التطبيق تلقائيًا من أدائها.", color = colors.onSurfaceVariant, fontSize = 10.sp)
  }

  if (showGoal) {
    AlertDialog(
      onDismissRequest = { showGoal=false },
      title = { Text("ضبط أهدافك") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value=goalText,onValueChange={goalText=it.filter(Char::isDigit).take(4)},label={Text("هدف التسبيح")},singleLine=true)
          OutlinedTextField(value=verseGoalText,onValueChange={verseGoalText=it.filter(Char::isDigit).take(4)},label={Text("هدف الآيات")},singleLine=true)
        }
      },
      confirmButton = {
        TextButton(onClick = {
          goal=goalText.toIntOrNull()?.coerceIn(1,9999) ?: goal
          verseGoal=verseGoalText.toIntOrNull()?.coerceIn(1,9999) ?: verseGoal
          prefs.setToolInt("dhikr_goal",goal); prefs.setToolInt("verse_goal",verseGoal); showGoal=false
        }) { Text("حفظ") }
      },
      dismissButton = { TextButton(onClick={showGoal=false}) { Text("إلغاء") } }
    )
  }
}
