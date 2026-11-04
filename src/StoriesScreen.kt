package com.mutqin.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class QuranStory(val title: String, val summary: String, val lesson: String, val source: String)

@Composable
fun StoriesScreen(onBack: () -> Unit) {
  val stories = remember {
    listOf(
      QuranStory(
        "قصة نوح عليه السلام",
        "دعا نوح قومه إلى عبادة الله زمنًا طويلًا، وصبر على دعوتهم، ثم أمره الله بصنع السفينة، فنجّى الله المؤمنين وأغرق المكذبين.",
        "الصبر والثبات في الدعوة، وأن النجاة تكون بالإيمان وطاعة الله.",
        "سورة هود: ٢٥–٤٩، وسورة نوح."
      ),
      QuranStory(
        "قصة إبراهيم عليه السلام",
        "تأمل إبراهيم عليه السلام في بطلان عبادة الكواكب، ودعا أباه وقومه إلى التوحيد، وابتلاه الله فصبر، وجعل له ذكرًا حسنًا.",
        "إخلاص العبادة لله، والتفكر، والثبات على الحق.",
        "سورة الأنعام: ٧٤–٧٩، وسورة الأنبياء: ٥١–٧٠."
      ),
      QuranStory(
        "قصة يوسف عليه السلام",
        "رأى يوسف رؤيا، ثم مر بابتلاءات متتابعة، فصبر وحفظ نفسه، ومكّنه الله في الأرض، واجتمع بأهله في نهاية القصة.",
        "العفة والصبر والعفو وحسن التوكل على الله.",
        "سورة يوسف: ٤–١٠١."
      ),
      QuranStory(
        "قصة موسى عليه السلام",
        "أوحى الله إلى أم موسى أن تلقيه في اليم، فرده الله إليها، ثم أرسله إلى فرعون يدعوه إلى عبادة الله، وأيّده بالآيات.",
        "الثقة بوعد الله، والشجاعة في قول الحق.",
        "سورة القصص: ٧–٣٥، وسورة طه: ٩–٩٨."
      ),
      QuranStory(
        "أصحاب الكهف",
        "كانوا فتية آمنوا بربهم واعتزلوا قومهم حفاظًا على دينهم، فأنامهم الله سنين طويلة ثم بعثهم.",
        "الثبات على الإيمان، وصحبة أهل الخير، واللجوء إلى الله.",
        "سورة الكهف: ٩–٢٦."
      )
    )
  }
  var expanded by remember { mutableIntStateOf(-1) }

  Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
    TextButton(onClick = onBack) { Text("رجوع") }
    Text("قصص من القرآن", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp))
    Text("ملخصات قصيرة مستندة إلى الآيات المذكورة، وليست نقلًا حرفيًا.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      items(stories.size) { i ->
        val story = stories[i]
        Card(modifier = Modifier.fillMaxWidth().clickable { expanded = if (expanded == i) -1 else i }) {
          Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(story.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            if (expanded == i) {
              Text(story.summary)
              Text("الفائدة: ${story.lesson}", style = MaterialTheme.typography.bodyMedium)
              Text("المصدر: ${story.source}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            } else {
              Text("اضغط لقراءة الملخص والفائدة", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }
  }
}
