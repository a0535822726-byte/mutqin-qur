package com.mutqin.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class TajweedQuestion(val prompt: String, val choices: List<String>, val answer: Int, val note: String)

@Composable
fun TajweedScreen(onBack: () -> Unit) {
  val questions = remember {
    listOf(
      TajweedQuestion(
        "ما أحكام النون الساكنة والتنوين؟",
        listOf("الإظهار والإدغام والإقلاب والإخفاء", "القلقلة فقط", "المد الطبيعي فقط"),
        0,
        "أحكام النون الساكنة والتنوين أربعة: الإظهار، والإدغام، والإقلاب، والإخفاء."
      ),
      TajweedQuestion(
        "ما حروف الإظهار الحلقي؟",
        listOf("يرملون", "ء هـ ع ح غ خ", "قطب جد"),
        1,
        "حروف الإظهار الحلقي ستة: الهمزة والهاء والعين والحاء والغين والخاء."
      ),
      TajweedQuestion(
        "كم مقدار المد الطبيعي؟",
        listOf("حركتان", "أربع حركات دائمًا", "ست حركات"),
        0,
        "المد الطبيعي مقداره حركتان، ما لم يأت بعد حرف المد همز أو سكون يغيّر الحكم."
      ),
      TajweedQuestion(
        "ما حروف القلقلة؟",
        listOf("ء هـ ع ح غ خ", "قطب جد", "ينمو"),
        1,
        "حروف القلقلة مجموعة في: قطب جد، وتظهر القلقلة عند سكون أحدها."
      )
    )
  }
  var index by remember { mutableIntStateOf(0) }
  var selected by remember { mutableIntStateOf(-1) }
  var score by remember { mutableIntStateOf(0) }
  var finished by remember { mutableStateOf(false) }
  val q = questions[index]

  Column(
    modifier = Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(18.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    TextButton(onClick = onBack) { Text("رجوع") }
    Text("معلّم التجويد", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
    Text("دروس تأسيسية واختبار قصير — راجع النطق مع معلّم متقن؛ هذا القسم لا يستمع لصوتك ولا يصحح مخارج الحروف آليًا.")
    HorizontalDivider()
    Text("أحكام النون الساكنة والتنوين", style = MaterialTheme.typography.titleLarge)
    Text("١) الإظهار: إخراج النون الساكنة أو التنوين واضحًا إذا جاء بعدها أحد حروف الحلق: ء هـ ع ح غ خ.")
    Text("٢) الإدغام: إدخال النون الساكنة أو التنوين في الحرف التالي. حروفه: يرملون، وينقسم إلى إدغام بغنة (ينمو) وبغير غنة (ل، ر).")
    Text("٣) الإقلاب: قلب النون الساكنة أو التنوين ميمًا مخفاة مع الغنة عند الباء.")
    Text("٤) الإخفاء: النطق بالنون الساكنة أو التنوين بين الإظهار والإدغام مع الغنة عند بقية الحروف الخمسة عشر.")
    HorizontalDivider()
    Text("أحكام الميم الساكنة", style = MaterialTheme.typography.titleLarge)
    Text("الإخفاء الشفوي: عند الباء. الإدغام الشفوي: عند الميم. الإظهار الشفوي: عند بقية الحروف.")
    HorizontalDivider()
    Text("المد الطبيعي", style = MaterialTheme.typography.titleLarge)
    Text("هو المد الذي لا تقوم ذات حرف المد إلا به، ومقداره حركتان. حروف المد: الألف الساكنة المفتوح ما قبلها، والواو الساكنة المضموم ما قبلها، والياء الساكنة المكسور ما قبلها.")
    HorizontalDivider()
    Text("اختبر فهمك", style = MaterialTheme.typography.titleLarge)
    if (!finished) {
      Text("السؤال ${index + 1} من ${questions.size}: ${q.prompt}")
      q.choices.forEachIndexed { i, choice ->
        OutlinedButton(
          onClick = { if (selected == -1) selected = i },
          enabled = selected == -1,
          modifier = Modifier.fillMaxWidth()
        ) { Text(choice) }
      }
      if (selected != -1) {
        val correct = selected == q.answer
        Text(if (correct) "إجابة صحيحة" else "ليست صحيحة", color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        Text(q.note)
        Button(
          onClick = {
            if (correct) score++
            if (index == questions.lastIndex) finished = true else { index++; selected = -1 }
          },
          modifier = Modifier.fillMaxWidth()
        ) { Text(if (index == questions.lastIndex) "عرض النتيجة" else "السؤال التالي") }
      }
    } else {
      Text("نتيجتك: $score من ${questions.size}")
      Button(onClick = { index = 0; selected = -1; score = 0; finished = false }, modifier = Modifier.fillMaxWidth()) { Text("إعادة الاختبار") }
    }
    Text("تنبيه: هذه معلومات تأسيسية مختصرة، وليست بديلًا عن التلقي والمشافهة على يد معلّم مؤهل.")
  }
}
