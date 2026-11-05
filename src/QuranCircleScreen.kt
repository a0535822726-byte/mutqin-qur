package com.mutqin.app

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun QuranCircleScreen(prefs: AppPrefs, onBack: () -> Unit) {
  val colors = MaterialTheme.colorScheme
  val context = LocalContext.current
  val today = LocalDate.now().toString()
  var students by remember { mutableStateOf(prefs.toolText("circle_students").split("\n").filter { it.isNotBlank() }) }
  var newStudent by remember { mutableStateOf("") }
  var assignment by remember { mutableStateOf(prefs.toolText("circle_assignment_$today")) }
  var generalNote by remember { mutableStateOf(prefs.toolText("circle_note_$today")) }
  var refresh by remember { mutableIntStateOf(0) }
  val presentCount = remember(refresh, students) { students.count { prefs.toolFlag("circle_att_${today}_${it.hashCode()}") } }

  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement=Arrangement.spacedBy(13.dp)) {
    Row(verticalAlignment=Alignment.CenterVertically) {
      TextButton(onClick=onBack) { Text("رجوع") }
      Column(Modifier.weight(1f)) {
        Text("إدارة حلقة القرآن",fontSize=23.sp,fontWeight=FontWeight.Black,color=colors.primary)
        Text("متابعة الطلاب والحضور والتسميع",fontSize=11.sp,color=colors.onSurfaceVariant)
      }
      Text("📗",fontSize=27.sp)
    }
    Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=colors.primary)) {
      Column(Modifier.fillMaxWidth().padding(17.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("حلقتك اليوم",fontSize=21.sp,fontWeight=FontWeight.Bold,color=colors.onPrimary)
        Text(today,fontSize=11.sp,color=colors.onPrimary.copy(alpha=.85f))
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
          CircleStat("الطلاب","${students.size}",colors.onPrimary)
          CircleStat("الحضور","$presentCount",colors.onPrimary)
          CircleStat("الغياب","${(students.size-presentCount).coerceAtLeast(0)}",colors.onPrimary)
        }
      }
    }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
        Text("قائمة الطلاب",fontSize=17.sp,fontWeight=FontWeight.Bold,color=colors.primary)
        OutlinedTextField(value=newStudent,onValueChange={newStudent=it.take(40)},modifier=Modifier.fillMaxWidth(),singleLine=true,label={Text("اسم الطالب")})
        Button(onClick={
          val name=newStudent.trim()
          if(name.isNotEmpty() && students.none{it.equals(name,true)}) {
            students=students+name
            prefs.setToolText("circle_students",students.joinToString("\n"))
            newStudent=""
            refresh++
          }
        },modifier=Modifier.fillMaxWidth(),enabled=newStudent.trim().isNotEmpty()) { Text("＋ إضافة طالب") }
        if(students.isEmpty()) Text("أضف أسماء طلاب الحلقة لتبدأ متابعة الحضور والتسميع.",fontSize=12.sp,color=colors.onSurfaceVariant)
        students.forEach { student ->
          val key="circle_att_${today}_${student.hashCode()}"
          Row(verticalAlignment=Alignment.CenterVertically) {
            Checkbox(checked=prefs.toolFlag(key),onCheckedChange={prefs.setToolFlag(key,it);refresh++})
            Column(Modifier.weight(1f)) {
              Text(student,fontWeight=FontWeight.SemiBold)
              Text(if(prefs.toolFlag(key)) "حاضر اليوم" else "لم يسجل الحضور",fontSize=10.sp,color=colors.onSurfaceVariant)
            }
            TextButton(onClick={
              students=students.filterNot{it==student}
              prefs.setToolText("circle_students",students.joinToString("\n"))
              refresh++
            }) { Text("حذف",color=colors.error) }
          }
          HorizontalDivider(color=colors.outlineVariant)
        }
      }
    }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("واجب الحفظ والمراجعة",fontSize=17.sp,fontWeight=FontWeight.Bold,color=colors.primary)
        OutlinedTextField(value=assignment,onValueChange={assignment=it;prefs.setToolText("circle_assignment_$today",it)},modifier=Modifier.fillMaxWidth(),minLines=2,label={Text("مثال: حفظ سورة الملك من ١ إلى ٥ ومراجعة سورة النبأ")})
        Text("يُحفظ الواجب لهذا اليوم على جهازك.",fontSize=10.sp,color=colors.onSurfaceVariant)
      }
    }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("ملاحظات المعلم",fontSize=17.sp,fontWeight=FontWeight.Bold,color=colors.primary)
        OutlinedTextField(value=generalNote,onValueChange={generalNote=it;prefs.setToolText("circle_note_$today",it)},modifier=Modifier.fillMaxWidth(),minLines=3,label={Text("ملاحظات الحلقة، الأخطاء المتكررة، والتنبيهات")})
      }
    }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("تقرير الحلقة",fontSize=17.sp,fontWeight=FontWeight.Bold,color=colors.primary)
        Text("أنشئ تقريرًا نصيًا لمشاركته بعد مراجعته.",fontSize=11.sp,color=colors.onSurfaceVariant)
        Button(onClick={
          val report=buildString {
            append("تقرير حلقة القرآن\nالتاريخ: ").append(today)
            append("\nعدد الطلاب: ").append(students.size)
            append("\nالحضور: ").append(presentCount)
            append("\nالغياب: ").append((students.size-presentCount).coerceAtLeast(0))
            append("\n\nالطلاب:\n")
            students.forEach { append("- ").append(it).append(if(prefs.toolFlag("circle_att_${today}_${it.hashCode()}")) " (حاضر)" else " (غائب/غير مسجل)").append("\n") }
            append("\nواجب اليوم:\n").append(assignment.ifBlank{"لم يحدد"})
            append("\n\nملاحظات:\n").append(generalNote.ifBlank{"لا توجد"})
          }
          val intent=Intent(Intent.ACTION_SEND).apply { type="text/plain";putExtra(Intent.EXTRA_TEXT,report) }
          context.startActivity(Intent.createChooser(intent,"مشاركة تقرير الحلقة"))
        },modifier=Modifier.fillMaxWidth()) { Text("مشاركة تقرير الحلقة") }
      }
    }
    Text("البيانات تحفظ محليًا على هذا الهاتف. هذه الأداة لا تتحقق آليًا من صحة الحفظ أو الحضور، ولا ترسل بيانات الطلاب إلى أي جهة.",fontSize=10.sp,color=colors.onSurfaceVariant)
  }
}

@Composable
private fun CircleStat(label:String,value:String,color:androidx.compose.ui.graphics.Color) {
  Surface(color=color.copy(alpha=.14f),shape=RoundedCornerShape(13.dp)) {
    Column(Modifier.padding(horizontal=12.dp,vertical=8.dp)) {
      Text(value,fontSize=20.sp,fontWeight=FontWeight.Black,color=color)
      Text(label,fontSize=10.sp,color=color)
    }
  }
}
