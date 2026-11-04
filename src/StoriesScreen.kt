package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class QuranStory(val title: String, val group: String, val summary: String, val lesson: String, val source: String)

@Composable
fun StoriesScreen(onBack: () -> Unit) {
  val stories = remember {
    listOf(
      QuranStory("آدم عليه السلام","الأنبياء","خلق الله آدم وعلّمه الأسماء، وأسجد له الملائكة، ثم وقع الابتلاء بالأكل من الشجرة، فتاب آدم إلى ربه وقبل الله توبته.","التوبة والرجوع إلى الله والحذر من وسوسة الشيطان.","البقرة: ٣٠–٣٩، الأعراف: ١١–٢٧، طه: ١١٥–١٢٣"),
      QuranStory("نوح عليه السلام","الأنبياء","دعا نوح قومه إلى التوحيد ليلًا ونهارًا، وصبر على إعراضهم، ثم أمره الله بصنع الفلك، فكانت النجاة للمؤمنين.","الصبر والثبات في الدعوة.","هود: ٢٥–٤٩، سورة نوح"),
      QuranStory("هود عليه السلام","الأنبياء","أرسل الله هودًا إلى عاد، فدعاهم إلى عبادة الله وترك الكبر، فكذبوه، فنجّى الله نبيه والذين آمنوا معه.","لا تغتر بالقوة؛ فهي لا تنفع مع الكفر والظلم.","الأعراف: ٦٥–٧٢، هود: ٥٠–٦٠"),
      QuranStory("صالح عليه السلام","الأنبياء","أرسل الله صالحًا إلى ثمود، وأيّده بالناقة آية، وأمرهم ألا يمسوها بسوء، لكن المعتدين عقروا الناقة فأخذتهم الرجفة.","احترام آيات الله وترك الفساد.","الأعراف: ٧٣–٧٩، هود: ٦١–٦٨"),
      QuranStory("إبراهيم عليه السلام","الأنبياء","حاجّ إبراهيم قومه في عبادة الأصنام، وأظهر عجزها، وابتلاه الله فصبر، ورفع قواعد البيت مع إسماعيل.","التوحيد والشجاعة في الحق والتوكل.","الأنبياء: ٥١–٧٠، البقرة: ١٢٤–١٢٩"),
      QuranStory("إسماعيل عليه السلام","الأنبياء","ذكر القرآن إسماعيل بالصدق والوفاء بالوعد، وشارك أباه إبراهيم في رفع قواعد الكعبة.","الوفاء والصبر والطاعة.","مريم: ٥٤–٥٥، البقرة: ١٢٥–١٢٩"),
      QuranStory("لوط عليه السلام","الأنبياء","دعا لوط قومه إلى ترك الفاحشة، فلما أصروا على الفساد نجّى الله لوطًا وأهله المؤمنين إلا امرأته.","العفة والابتعاد عن الفساد والاستجابة للنصح.","هود: ٧٧–٨٣، الحجر: ٥٧–٧٧"),
      QuranStory("يوسف عليه السلام","الأنبياء","رأى يوسف رؤيا، ثم مر بابتلاءات متتابعة، فصبر وحفظ نفسه، ثم مكّنه الله واجتمع بأهله.","العفة والصبر والعفو وحسن الظن بالله.","سورة يوسف: ٤–١٠١"),
      QuranStory("أيوب عليه السلام","الأنبياء","ابتلى الله أيوب، فصبر ولجأ إلى ربه بالدعاء، فكشف الله ضره وآتاه أهله ومثلهم معهم.","الصبر مع الدعاء وعدم اليأس من رحمة الله.","الأنبياء: ٨٣–٨٤، ص: ٤١–٤٤"),
      QuranStory("شعيب عليه السلام","الأنبياء","دعا شعيب أهل مدين إلى توحيد الله وإيفاء الكيل والميزان وترك بخس الناس حقوقهم، لكن المتكبرين كذبوه.","الأمانة والعدل في التعامل.","الأعراف: ٨٥–٩٣، هود: ٨٤–٩٥"),
      QuranStory("موسى عليه السلام","الأنبياء","أوحى الله إلى أم موسى أن تلقيه في اليم، فرده إليها، ثم أرسله إلى فرعون وأيده بالآيات، ونجّى بني إسرائيل.","الثقة بوعد الله والثبات أمام الظلم.","القصص: ٧–٣٥، طه: ٩–٩٨، الشعراء: ٥٢–٦٨"),
      QuranStory("هارون عليه السلام","الأنبياء","كان هارون نبيًا ووزيرًا لأخيه موسى، يعينه في تبليغ الرسالة، وذكّر قومه حين فتنوا بالعجل.","التعاون على الخير والرفق في النصح.","طه: ٢٩–٩٤، الأعراف: ١٤٢–١٥١"),
      QuranStory("داود عليه السلام","الأنبياء","آتَى الله داود الملك والحكمة، وسخّر معه الجبال والطير يسبحن، وعلّمه الفصل بين الناس.","العدل وشكر النعمة وكثرة الذكر.","ص: ١٧–٢٦، سبأ: ١٠–١١"),
      QuranStory("سليمان عليه السلام","الأنبياء","آتَى الله سليمان ملكًا عظيمًا وعلّمه منطق الطير، وتفقد الهدهد، ودعا ملكة سبأ إلى عبادة الله.","الشكر والتواضع واستعمال النعمة في الخير.","النمل: ١٥–٤٤، ص: ٣٠–٤٠"),
      QuranStory("يونس عليه السلام","الأنبياء","ذهب يونس مغاضبًا، فكان في الظلمات ودعا ربه بكلمات التوحيد والتنزيه، فاستجاب الله له ونجّاه.","الإنابة إلى الله والإكثار من الدعاء.","الأنبياء: ٨٧–٨٨، الصافات: ١٣٩–١٤٨"),
      QuranStory("زكريا عليه السلام","الأنبياء","دعا زكريا ربه سرًا أن يهب له ولدًا، فبشره الله بيحيى على كبر سنه.","حسن الدعاء والرجاء وعدم القنوط.","آل عمران: ٣٧–٤١، مريم: ٢–١١"),
      QuranStory("يحيى عليه السلام","الأنبياء","آتَى الله يحيى الحكم صبيًا، وجعله بارًا بوالديه، وأثنى عليه بالزكاة والتقوى والسلام.","البر بالوالدين والعفة والتقوى.","مريم: ١٢–١٥، آل عمران: ٣٩"),
      QuranStory("عيسى عليه السلام","الأنبياء","ولد عيسى بآية من الله، وتكلم في المهد بإذن الله، وأيده الله بالبينات، ودعا بني إسرائيل إلى عبادة الله.","الإيمان بقدرة الله وعبادته وحده.","آل عمران: ٤٥–٥١، مريم: ١٦–٣٦"),
      QuranStory("مريم عليها السلام","نساء صالحات","اصطفى الله مريم وطهرها، ورزقها من فضله، وبشرها بعيسى عليه السلام، فكانت آية للعالمين.","العبادة والعفة والثقة بالله.","آل عمران: ٣٥–٤٧، مريم: ١٦–٣٦"),
      QuranStory("أصحاب الكهف","قصص القرآن","كانوا فتية آمنوا بربهم وفروا بدينهم، فآواهم الله إلى الكهف وأنامهم سنين طويلة ثم بعثهم.","الثبات على الإيمان وصحبة أهل الخير.","الكهف: ٩–٢٦"),
      QuranStory("ذو القرنين","قصص القرآن","مكّن الله ذا القرنين في الأرض، فسار فيها بالعدل، وأعان قومًا على بناء ردم يحجز يأجوج ومأجوج.","القوة أمانة، والنجاح يكون بالعدل ونسبة الفضل لله.","الكهف: ٨٣–٩٨"),
      QuranStory("طالوت وجالوت","قصص القرآن","اختار الله طالوت ملكًا لبني إسرائيل، فاختبر جنوده بالنهر، وثبتت فئة مؤمنة فواجهت جالوت وانتصروا بإذن الله.","الثبات وقلة العدد لا تمنع النصر بإذن الله.","البقرة: ٢٤٦–٢٥١"),
      QuranStory("قارون","قصص القرآن","آتَى الله قارون كنوزًا عظيمة، فبغى على قومه ونسب الفضل إلى نفسه، فخسف الله به وبداره الأرض.","ترك الكبر وعدم الاغترار بالمال.","القصص: ٧٦–٨٢"),
      QuranStory("أصحاب الجنة","قصص القرآن","عزم أصحاب بستان على منع المساكين من ثمره، فذهبوا إليه فوجدوه قد صار كالصريم، فعرفوا خطأهم.","شكر النعمة وإعطاء المحتاج حقه.","القلم: ١٧–٣٣"),
      QuranStory("أصحاب الأخدود","قصص القرآن","ذكر القرآن قومًا عذبوا المؤمنين بسبب إيمانهم، وتوعد الظالمين الذين فتنوا المؤمنين ولم يتوبوا.","الثبات على الإيمان والتحذير من ظلم الناس.","البروج: ٤–١٠"),
      QuranStory("أصحاب الفيل","قصص القرآن","ذكر الله كيف رد كيد أصحاب الفيل الذين قصدوا البيت الحرام، وأرسل عليهم طيرًا أبابيل.","قدرة الله فوق كل قوة.","سورة الفيل كاملة"),
      QuranStory("سبأ وملكة سبأ","قصص القرآن","أخبر الهدهد سليمان عن قوم تملكهم امرأة يعبدون الشمس، فأرسل إليها كتابًا يدعوها إلى الله، فكان في القصة تدبر وحوار.","التثبت من الأخبار والحكمة في الدعوة والشكر.","النمل: ٢٠–٤٤")
    )
  }
  var query by remember { mutableStateOf("") }
  var expanded by remember { mutableIntStateOf(-1) }
  val filtered = remember(query) { stories.withIndex().filter { it.value.title.contains(query.trim(), true) || it.value.group.contains(query.trim(), true) } }
  val colors = MaterialTheme.colorScheme
  Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
      TextButton(onClick = onBack) { Text("رجوع") }
      Column(Modifier.weight(1f)) {
        Text("مكتبة القصص", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = colors.primary)
        Text("${stories.size} قصة ووقفة تدبر", fontSize = 11.sp, color = colors.onSurfaceVariant)
      }
      Text("📚", fontSize = 25.sp)
    }
    Card(Modifier.fillMaxWidth().padding(horizontal = 14.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.primaryContainer)) {
      Column(Modifier.padding(15.dp)) {
        Text("اقرأ القصة من مصدرها", fontWeight = FontWeight.Bold, color = colors.onPrimaryContainer)
        Text("كل ملخص مرتبط بمواضع الآيات لتعود إلى القرآن وتتدبر السياق بنفسك.", fontSize = 11.sp, color = colors.onPrimaryContainer)
      }
    }
    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), singleLine = true, shape = RoundedCornerShape(15.dp), label = { Text("ابحث باسم القصة أو القسم") })
    LazyColumn(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
      items(filtered, key = { it.index }) { indexed ->
        val i = indexed.index
        val story = indexed.value
        Card(Modifier.fillMaxWidth().clickable { expanded = if (expanded == i) -1 else i }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
          Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Column(Modifier.weight(1f)) { Text(story.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.primary); Text(story.group, fontSize = 10.sp, color = colors.onSurfaceVariant) }
              Text(if (expanded == i) "−" else "+", fontSize = 23.sp, color = colors.secondary)
            }
            if (expanded == i) {
              HorizontalDivider()
              Text(story.summary, fontSize = 13.sp, lineHeight = 22.sp)
              Text("🌱 العبرة", fontWeight = FontWeight.Bold, color = colors.primary, fontSize = 12.sp)
              Text(story.lesson, fontSize = 12.sp, lineHeight = 20.sp)
              Text("مواضع القصة في القرآن", fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text(story.source, fontSize = 11.sp, color = colors.secondary)
            } else Text("اضغط لعرض الملخص والعبرة والآيات", fontSize = 10.sp, color = colors.onSurfaceVariant)
          }
        }
      }
    }
    Text("هذه ملخصات تعليمية موجزة وليست تفسيرًا تفصيليًا؛ العمدة على الآيات والتفسير الموثوق.", modifier = Modifier.fillMaxWidth().padding(10.dp), fontSize = 10.sp, color = colors.onSurfaceVariant)
  }
}
