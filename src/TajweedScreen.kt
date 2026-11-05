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
      ),
      TajweedQuestion(
        "ما حروف الإدغام بغنة؟",
        listOf("يرملون", "ينمو", "ل، ر"),
        1,
        "حروف الإدغام بغنة أربعة: الياء والنون والميم والواو، مجموعة في كلمة ينمو."
      ),
      TajweedQuestion(
        "ما حرف الإقلاب؟",
        listOf("الباء", "الميم", "اللام"),
        0,
        "يكون الإقلاب عند الباء؛ فتقلب النون الساكنة أو التنوين ميمًا مخفاة مع الغنة."
      ),
      TajweedQuestion(
        "متى يكون الإخفاء الشفوي؟",
        listOf("عند الباء بعد الميم الساكنة", "عند الميم بعد الميم الساكنة", "عند جميع الحروف"),
        0,
        "الإخفاء الشفوي يكون إذا جاء بعد الميم الساكنة حرف الباء."
      ),
      TajweedQuestion(
        "ما حروف الإدغام الشفوي؟",
        listOf("الباء", "الميم", "جميع حروف الحلق"),
        1,
        "الإدغام الشفوي يكون عند مجيء الميم بعد الميم الساكنة."
      ),
      TajweedQuestion(
        "كم مقدار غنة النون والميم المشددتين؟",
        listOf("حركتان", "ست حركات", "لا غنة فيهما"),
        0,
        "النون والميم المشددتان فيهما غنة مقدارها حركتان في الأداء المعروف."
      ),
      TajweedQuestion(
        "ما المقصود بالمد المتصل؟",
        listOf("أن يأتي الهمز بعد حرف المد في الكلمة نفسها", "أن يأتي السكون قبل حرف المد", "أن يأتي حرف المد في آخر السورة فقط"),
        0,
        "المد الواجب المتصل يكون فيه حرف المد وبعده همز في كلمة واحدة."
      ),
      TajweedQuestion(
        "ما حروف المد؟",
        listOf("ا، و، ي بشروطها", "قطب جد", "ء، هـ، ع"),
        0,
        "حروف المد الألف والواو والياء، ولكل حرف شرط في حركة ما قبله."
      ),
      TajweedQuestion(
        "ما المقصود بالوقف في التلاوة؟",
        listOf("قطع الصوت على كلمة زمناً يتنفس فيه القارئ عادة", "زيادة حركة على آخر الكلمة", "إسقاط حرف من الكلمة"),
        0,
        "الوقف هو قطع الصوت على الكلمة زمناً يتنفس فيه القارئ عادة بنية استئناف القراءة، مع مراعاة المعنى والمواضع."
      )
    )
  }
  var index by remember { mutableIntStateOf(0) }
  var selected by remember { mutableIntStateOf(-1) }
  var score by remember { mutableIntStateOf(0) }
  var finished by remember { mutableStateOf(false) }
  val q = questions[index]
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = Modifier.fillMaxSize().background(colors.background).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(13.dp)
  ) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Spacer(Modifier.weight(1f))
      Text("مُتقن • التعلّم", color = colors.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
    }
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = colors.primary)) {
      Column(Modifier.fillMaxWidth().padding(19.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("مدرسة التجويد", color = colors.onPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text("تعلّم القواعد على مراحل، ثم اختبر فهمك.", color = colors.onPrimary.copy(alpha = .9f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(color = colors.onPrimary.copy(alpha = .15f), shape = CircleShape) { Text("١٢ سؤالًا", color = colors.onPrimary, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium) }
          Surface(color = colors.onPrimary.copy(alpha = .15f), shape = CircleShape) { Text("مراجعة ذاتية", color = colors.onPrimary, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium) }
        }
      }
    }
    Text("الدروس الأساسية", style = MaterialTheme.typography.titleLarge, color = colors.primary, fontWeight = FontWeight.Bold)
    val lessons = listOf(
      "أحكام النون الساكنة والتنوين" to "الإظهار: إخراج النون الساكنة أو التنوين واضحًا إذا جاء بعدها أحد حروف الحلق: ء هـ ع ح غ خ. الإدغام: إدخال النون الساكنة أو التنوين في الحرف التالي؛ حروفه يرملون، وبغنة (ينمو) وبغير غنة (ل، ر). الإقلاب: قلبها ميمًا مخفاة مع الغنة عند الباء. الإخفاء: النطق بها بين الإظهار والإدغام مع الغنة عند بقية الحروف الخمسة عشر.",
      "أحكام الميم الساكنة" to "الإخفاء الشفوي عند الباء، والإدغام الشفوي عند الميم، والإظهار الشفوي عند بقية الحروف.",
      "المد الطبيعي" to "مقداره حركتان. حروف المد الألف الساكنة المفتوح ما قبلها، والواو الساكنة المضموم ما قبلها، والياء الساكنة المكسور ما قبلها.",
      "القلقلة والوقف" to "حروف القلقلة مجموعة في (قطب جد)، وتظهر عند سكون الحرف. وعند الوقف يُراعى موضع الوقف والمعنى، ويُتلقى الأداء من معلّم متقن."
    )
    lessons.forEachIndexed { i, lesson ->
      Card(shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
          Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Surface(color = colors.primaryContainer, shape = RoundedCornerShape(11.dp)) {
              Text("${i + 1}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = colors.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Text(lesson.first, style = MaterialTheme.typography.titleMedium, color = colors.primary, fontWeight = FontWeight.Bold)
          }
          Text(lesson.second, style = MaterialTheme.typography.bodyMedium, lineHeight = 23.sp, color = colors.onSurface)
        }
      }
    }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
      Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        Text("اختبر فهمك", style = MaterialTheme.typography.titleLarge, color = colors.primary, fontWeight = FontWeight.Black)
        if (!finished) {
          Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("السؤال ${index + 1} من ${questions.size}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("النتيجة: $score", color = colors.primary, fontWeight = FontWeight.Bold)
          }
          LinearProgressIndicator(progress = { (index + 1).toFloat() / questions.size }, modifier = Modifier.fillMaxWidth().height(7.dp))
          Text(q.prompt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
          q.choices.forEachIndexed { i, choice ->
            val isChosen = selected == i
            val isAnswer = i == q.answer
            OutlinedButton(
              onClick = { if (selected == -1) selected = i },
              enabled = selected == -1,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (selected != -1 && isAnswer) colors.primaryContainer else if (isChosen && !isAnswer) colors.errorContainer else colors.surface,
                contentColor = colors.onSurface
              )
            ) { Text(choice, modifier = Modifier.fillMaxWidth()) }
          }
          if (selected != -1) {
            val correct = selected == q.answer
            Text(if (correct) "أحسنت، إجابة صحيحة" else "الإجابة الصحيحة موضحة أعلاه", color = if (correct) colors.primary else colors.error, fontWeight = FontWeight.Bold)
            Text(q.note, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
            Button(
              onClick = {
                if (correct) score++
                if (index == questions.lastIndex) finished = true else { index++; selected = -1 }
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp)
            ) { Text(if (index == questions.lastIndex) "إنهاء وعرض النتيجة" else "السؤال التالي") }
          }
        } else {
          Text("اكتمل الاختبار", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
          Text("$score من ${questions.size}", style = MaterialTheme.typography.headlineMedium, color = colors.primary, fontWeight = FontWeight.Black)
          LinearProgressIndicator(progress = { score.toFloat() / questions.size }, modifier = Modifier.fillMaxWidth().height(9.dp))
          Text(if (score >= 10) "ممتاز! واصل المراجعة والتطبيق." else "بداية طيبة. راجع الدروس ثم أعد الاختبار.", color = colors.onSurfaceVariant)
          Button(onClick = { index = 0; selected = -1; score = 0; finished = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("إعادة الاختبار") }
        }
      }
    }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.errorContainer)) {
      Text("تنبيه علمي: هذا القسم للتعلّم النظري والاختبار فقط. لا يستمع إلى تلاوتك ولا يصحح المخارج أو الغنة أو المد آليًا. خذ الأداء بالمشافهة من معلّم متقن.", modifier = Modifier.padding(14.dp), color = colors.onErrorContainer, style = MaterialTheme.typography.bodySmall, lineHeight = 20.sp)
    }
  }
}
