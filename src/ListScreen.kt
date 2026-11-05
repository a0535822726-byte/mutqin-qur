package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahListScreen(repo: QuranRepository, prefs: AppPrefs, dark: Boolean, onDark: (Boolean) -> Unit, onOpenSurah: (Int) -> Unit, onResume: (Int, Int, Int) -> Unit, onOpenRecorder: () -> Unit, onOpenCheck: () -> Unit, onOpenTajweed: () -> Unit, onOpenStories: () -> Unit, onOpenCircle: () -> Unit, onOpenReciters: () -> Unit, onOpenDailyTools: () -> Unit, onOpenTool: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val last = prefs.lastPosition()
    val q = normalizeAr(query.trim())
    val filtered = remember(q) { (1..repo.surahCount()).filter { n -> q.isEmpty() || n.toString() == q || normalizeAr(SURAH_NAMES[n - 1]).contains(q) } }
    val colors = MaterialTheme.colorScheme
    val quickTools = listOf(
        Triple("سُبْحَانَ","التسبيح","tasbih"), Triple("الْحَمْدُ","التحميد","tahmid"),
        Triple("الله أكبر","التكبير","takbir"), Triple("أستغفر","الاستغفار","istighfar"),
        Triple("ﷺ","الصلاة على النبي","salawat"), Triple("📊","إنجاز اليوم","achievement"),
        Triple("📗","عداد القراءة","reading"), Triple("🧠","الحفظ الجديد","memorization"),
        Triple("🔁","مراجعة الحفظ","review"), Triple("🕌","متابعة الصلاة","prayers"),
        Triple("✓","مهام قرآنية","tasks"), Triple("📝","دفتر التدبر","notes"),
        Triple("🌅","أذكار الصباح","morning"), Triple("🌙","أذكار المساء","evening"),
        Triple("📚","متابعة الختمة","khatmah"), Triple("🎧","ورد الاستماع","listening"),
        Triple("💡","فوائد اليوم","reflection"), Triple("🎯","أهداف الأسبوع","goals")
    )
    val extraTools = listOf(
        Triple("🔂","تكرار الآية","repeat"), Triple("📄","عداد الصفحات","pages"),
        Triple("📚","متابعة الأجزاء","juz"), Triple("🏁","إنجاز السور","surahfinish"),
        Triple("🔁","جولات المراجعة","revisionrounds"), Triple("🎧","دقائق الاستماع","listenminutes"),
        Triple("🗣","تدريب المخارج","makharij"), Triple("نّ","أحكام النون","noonrules"),
        Triple("مّ","أحكام الميم","meemrules"), Triple("〰","تدريب المدود","maddpractice"),
        Triple("⏸","علامات الوقف","waqf"), Triple("✍","دفتر الأخطاء","mistakes"),
        Triple("❓","أسئلة للمعلم","questions"), Triple("🤲","دفتر الدعاء","dua"),
        Triple("🗓","خطة الأسبوع","weeklyplan"), Triple("☑","مراجعة اليوم","dailyreview"),
        Triple("🎙","سجل الجلسات","sessionlog"), Triple("💡","خواطر قرآنية","reflection2"),
        Triple("🔥","أيام القراءة","readingstreak"), Triple("🧠","أيام الحفظ","memorystreak")
    )
    LazyColumn(
        Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(52.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(colors.primary, colors.secondary))), contentAlignment = Alignment.Center) {
                    Text("مُ", color = colors.onPrimary, fontSize = 27.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("مُتقن", fontSize = 26.sp, fontWeight = FontWeight.Black, color = colors.onBackground)
                    Text("رحلتك اليومية مع كتاب الله", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
                FilledTonalIconButton(onClick = { onDark(!dark) }) {
                    Text(if (dark) "☀" else "☾", fontSize = 20.sp)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = colors.primary), elevation = CardDefaults.cardElevation(0.dp)) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(colors.primary, Color(0xFF16785D), colors.secondary))).padding(21.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("خطوة صغيرة كل يوم… وأثر يبقى.", fontSize = 13.sp, color = Color.White.copy(alpha = .9f))
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            HeroPill("١١٤ سورة", Color.White)
                            HeroPill("قراءة • حفظ • مراجعة", Color.White)
                        }
                        Button(
                            onClick = { if (last != null) onResume(last.first, last.second, last.third) else onOpenSurah(1) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFEFA), contentColor = colors.primary),
                            shape = RoundedCornerShape(15.dp)
                        ) { Text(if (last != null) "تابع من حيث وصلت  ←" else "ابدأ القراءة  ←", fontWeight = FontWeight.Bold) }
                        if (last != null) Text("آخر موضع: ${SURAH_NAMES[last.first - 1]} • الآية ${toArabic(last.second)}", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
        item {
            HomeSectionHeading("مركز القرآن", "أدواتك الأساسية في واجهة واحدة")
            Spacer(Modifier.height(9.dp))
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    FeatureCard("📖","المصحف","قراءة وتدبّر",Modifier.weight(1f),colors){ onOpenSurah(1) }
                    FeatureCard("🎙","التسميع","سجّل وراجع تلاوتك",Modifier.weight(1f),colors,onOpenCheck)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    FeatureCard("✨","مدرسة التجويد","دروس وتدريبات",Modifier.weight(1f),colors,onOpenTajweed)
                    FeatureCard("📚","مكتبة القصص","قصص وعبر",Modifier.weight(1f),colors,onOpenStories)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    FeatureCard("🏫","حلقة القرآن","طلاب ومتابعة",Modifier.weight(1f),colors,onOpenCircle)
                    FeatureCard("🎧","مكتبة القرّاء","استمع للتلاوات",Modifier.weight(1f),colors,onOpenReciters)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    FeatureCard("🎚","استديو التسجيل","سجّل واستمع",Modifier.weight(1f),colors,onOpenRecorder)
                    FeatureCard("🧠","خطة الحفظ","حفظ ومراجعة",Modifier.weight(1f),colors){ onOpenTool("memorization") }
                }
            }
        }
        item {
            HomeSectionHeading("عبادتك ومتابعتك", "كل أداة مستقلة وتحفظ بياناتها على الجهاز")
            Spacer(Modifier.height(9.dp))
            quickTools.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    pair.forEach { tool -> FeatureCard(tool.first, tool.second, "افتح المتابعة", Modifier.weight(1f), colors) { onOpenTool(tool.third) } }
                }
                Spacer(Modifier.height(8.dp))
            }
            Card(onClick = onOpenDailyTools, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.primaryContainer), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("لوحة إدارة يومك", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = colors.onPrimaryContainer)
                        Text("اجمع أهدافك وعداداتك في مكان واحد", fontSize = 11.sp, color = colors.onPrimaryContainer)
                    }
                    Text("←", fontSize = 24.sp, color = colors.onPrimaryContainer)
                }
            }
        }
        item {
            HomeSectionHeading("أدوات التعلّم المتقدمة", "تدريبات إضافية للحفظ والتجويد والمتابعة")
            Spacer(Modifier.height(9.dp))
            extraTools.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    pair.forEach { tool -> FeatureCard(tool.first, tool.second, "أداة مستقلة", Modifier.weight(1f), colors) { onOpenTool(tool.third) } }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        item {
            HomeSectionHeading("فهرس المصحف", "ابحث باسم السورة أو رقمها")
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                label = { Text("اسم السورة أو رقمها") },
                leadingIcon = { Text("⌕", fontSize = 22.sp) },
                singleLine = true, shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (filtered.isEmpty()) item {
            Text("لا توجد سورة مطابقة لبحثك", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = colors.onSurfaceVariant)
        }
        items(filtered) { n ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) }
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(43.dp).clip(RoundedCornerShape(14.dp)).background(colors.primaryContainer), contentAlignment = Alignment.Center) {
                        Text(toArabic(n), color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(SURAH_NAMES[n - 1], fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text("عدد الآيات: ${toArabic(repo.ayahCount(n))}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                    }
                    Text("فتح  ←", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Text(
                "ملاحظة: التسميع يتيح التسجيل والمراجعة ومقارنة نصية تقريبية عند توفر التعرف على الكلام، ولا يصدر حكمًا آليًا موثوقًا على أحكام التجويد.",
                fontSize = 10.sp, textAlign = TextAlign.Center, color = colors.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)
            )
        }
    }
}

@Composable private fun HomeSectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, fontSize = 19.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
        Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun HeroPill(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(color = color.copy(alpha = .16f), shape = CircleShape) {
        Text(label, color = color, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
    }
}

@Composable private fun FeatureCard(icon: String, title: String, subtitle: String, modifier: Modifier = Modifier, colors: ColorScheme = MaterialTheme.colorScheme, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.heightIn(min = 116.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)).background(colors.primaryContainer), contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 19.sp, color = colors.primary)
            }
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = colors.onSurface)
            Text(subtitle, fontSize = 10.sp, color = colors.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("افتح  ←", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
            }
        }
    }
}
