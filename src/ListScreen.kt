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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class HomeTool(
    val icon: String,
    val title: String,
    val detail: String,
    val id: String,
    val group: String
)

@Composable
fun SurahListScreen(
    repo: QuranRepository,
    prefs: AppPrefs,
    dark: Boolean,
    onDark: (Boolean) -> Unit,
    onOpenSurah: (Int) -> Unit,
    onResume: (Int, Int, Int) -> Unit,
    onOpenRecorder: () -> Unit,
    onOpenCheck: () -> Unit,
    onOpenTajweed: () -> Unit,
    onOpenStories: () -> Unit,
    onOpenCircle: () -> Unit,
    onOpenReciters: () -> Unit,
    onOpenDailyTools: () -> Unit,
    onOpenTool: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("الكل") }
    val last = prefs.lastPosition()
    val colors = MaterialTheme.colorScheme
    val normalizedQuery = normalizeAr(query.trim())
    val filteredSurahs = remember(normalizedQuery) {
        (1..repo.surahCount()).filter { n ->
            normalizedQuery.isEmpty() ||
                n.toString() == normalizedQuery ||
                normalizeAr(SURAH_NAMES[n - 1]).contains(normalizedQuery)
        }
    }
    val tools = remember {
        listOf(
            HomeTool("🧠", "الحفظ الجديد", "سجّل محفوظك اليوم", "memorization", "الحفظ"),
            HomeTool("🔁", "مراجعة الحفظ", "ثبّت ما سبق حفظه", "review", "الحفظ"),
            HomeTool("📚", "خطة الختمة", "وزّع ورد القراءة", "khatmah", "الحفظ"),
            HomeTool("🎯", "أهداف الأسبوع", "تابع أهدافك", "goals", "الحفظ"),
            HomeTool("📊", "إنجاز اليوم", "سجّل إنجازك", "achievement", "المتابعة"),
            HomeTool("📗", "عداد القراءة", "تابع صفحاتك", "reading", "المتابعة"),
            HomeTool("🕌", "متابعة الصلاة", "سجل المتابعة الشخصية", "prayers", "المتابعة"),
            HomeTool("✓", "مهام قرآنية", "قائمة مهامك", "tasks", "المتابعة"),
            HomeTool("📝", "دفتر التدبر", "دوّن الفوائد", "notes", "المتابعة"),
            HomeTool("🎧", "ورد الاستماع", "تابع استماعك", "listening", "المتابعة"),
            HomeTool("🌅", "أذكار الصباح", "متابعة الورد", "morning", "الأذكار"),
            HomeTool("🌙", "أذكار المساء", "متابعة الورد", "evening", "الأذكار"),
            HomeTool("سُبْحَانَ", "التسبيح", "عداد الذكر", "tasbih", "الأذكار"),
            HomeTool("الْحَمْدُ", "التحميد", "عداد الذكر", "tahmid", "الأذكار"),
            HomeTool("الله أكبر", "التكبير", "عداد الذكر", "takbir", "الأذكار"),
            HomeTool("أستغفر", "الاستغفار", "عداد الذكر", "istighfar", "الأذكار"),
            HomeTool("ﷺ", "الصلاة على النبي", "عداد الصلاة", "salawat", "الأذكار"),
            HomeTool("💡", "فوائد اليوم", "دوّن فائدة", "reflection", "المتابعة")
        )
    }
    val visibleTools = tools.filter {
        (selectedGroup == "الكل" || it.group == selectedGroup) &&
            (normalizedQuery.isEmpty() || normalizeAr(it.title).contains(normalizedQuery) || normalizeAr(it.detail).contains(normalizedQuery))
    }

    LazyColumn(
        Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(),
        contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("مُ", color = colors.onPrimary, fontSize = 25.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("مُتقن", fontSize = 25.sp, fontWeight = FontWeight.Black, color = colors.onBackground)
                    Text("رفيق رحلتك مع القرآن", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
                FilledTonalIconButton(onClick = { onDark(!dark) }) {
                    Text(if (dark) "☀" else "☾", fontSize = 20.sp)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colors.primary),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF073F32), colors.primary, Color(0xFF9A762F))))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Text("وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("ابدأ بخطوة، وواصل بثبات.", fontSize = 13.sp, color = Color.White.copy(alpha = .9f))
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            HeroTag("١١٤ سورة")
                            HeroTag("حفظ • تلاوة • مراجعة")
                        }
                        Button(
                            onClick = { if (last != null) onResume(last.first, last.second, last.third) else onOpenSurah(1) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF8E8), contentColor = colors.primary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (last != null) "متابعة آخر موضع  ←" else "ابدأ من الفاتحة  ←", fontWeight = FontWeight.Bold)
                        }
                        if (last != null) {
                            Text("آخر قراءة: ${SURAH_NAMES[last.first - 1]} • الآية ${toArabic(last.second)}", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            SectionTitle("الوصول السريع", "أهم ثلاث وظائف في مُتقن")
            Spacer(Modifier.height(9.dp))
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                HomeActionCard("🎙", "مختبر التسميع", "تسجيل صوتك ومقارنة الكلمات بالآية", colors.primaryContainer, colors.onPrimaryContainer, onOpenCheck)
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    HomeActionCard("📖", "المصحف", "قراءة الآيات", colors.surface, colors.onSurface, { onOpenSurah(1) }, Modifier.weight(1f))
                    HomeActionCard("✨", "التجويد", "دروس واختبارات", colors.surface, colors.onSurface, onOpenTajweed, Modifier.weight(1f))
                }
            }
        }

        item {
            SectionTitle("مكتبة مُتقن", "أقسام مستقلة للتعلم والاستماع")
            Spacer(Modifier.height(9.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeActionCard("🎧", "مكتبة القرّاء", "استمع إلى التلاوات", colors.surface, colors.onSurface, onOpenReciters)
                HomeActionCard("🎚", "استديو التسجيل", "سجّل صوتك واستمع إليه", colors.surface, colors.onSurface, onOpenRecorder)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HomeActionCard("📚", "قصص قرآنية", "قصص وعبر", colors.surface, colors.onSurface, onOpenStories, Modifier.weight(1f))
                    HomeActionCard("🏫", "حلقة القرآن", "متابعة الحلقة", colors.surface, colors.onSurface, onOpenCircle, Modifier.weight(1f))
                }
                HomeActionCard("🗓", "لوحة إدارة يومك", "خطط وردك وأهدافك ومهامك", colors.secondaryContainer, colors.onSecondaryContainer, onOpenDailyTools)
            }
        }

        item {
            SectionTitle("أدواتك اليومية", "اختر القسم لتظهر أدواته فقط")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                label = { Text("ابحث عن أداة أو سورة") },
                leadingIcon = { Text("⌕", fontSize = 22.sp) }
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("الكل", "الحفظ", "المتابعة", "الأذكار").forEach { group ->
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { selectedGroup = group },
                        label = { Text(group, fontSize = 11.sp) }
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            if (visibleTools.isEmpty()) {
                Text("لا توجد أداة مطابقة لبحثك.", color = colors.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                visibleTools.chunked(2).forEach { pair ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { tool ->
                            ToolTile(tool, colors, Modifier.weight(1f)) { onOpenTool(tool.id) }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        item {
            SectionTitle("مختبرات متخصصة", "أدوات أعمق للحفظ والبحث والتحليل")
            Spacer(Modifier.height(9.dp))
            HomeActionCard("🏅", "مركز الإتقان المتقدم", "إحصاءات السور والأجزاء وخطط القراءة", colors.primaryContainer, colors.onPrimaryContainer, { onOpenTool("unique_master_tools") })
            Spacer(Modifier.height(8.dp))
            HomeActionCard("🧰", "مختبر الدراسة القرآنية", "المتشابهات والقاموس والبطاقات والتقارير", colors.surface, colors.onSurface, { onOpenTool("unique_study_lab") })
            Spacer(Modifier.height(8.dp))
            HomeActionCard("🧩", "تحدي الحفظ", "اختبار الاستدعاء والمراجعة", colors.surface, colors.onSurface, { onOpenTool("unique_memorization") })
            Spacer(Modifier.height(8.dp))
            HomeActionCard("🔎", "باحث ألفاظ القرآن", "ابحث عن كلمة في نص المصحف", colors.surface, colors.onSurface, { onOpenTool("unique_word_search") })
            Spacer(Modifier.height(8.dp))
            HomeActionCard("📈", "مراجعة متباعدة", "جدول تثبيت الحفظ", colors.surface, colors.onSurface, { onOpenTool("unique_spaced_review") })
        }

        item {
            SectionTitle("فهرس المصحف", "ابحث باسم السورة أو رقمها")
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("اسم السورة أو رقمها") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (filteredSurahs.isEmpty()) item {
            Text("لا توجد سورة مطابقة", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = colors.onSurfaceVariant)
        }
        items(filteredSurahs) { n ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth().clickable { onOpenSurah(n) }
            ) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = colors.primaryContainer) {
                        Text(toArabic(n), modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp), color = colors.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(SURAH_NAMES[n - 1], fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text("عدد الآيات: ${toArabic(repo.ayahCount(n))}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                    }
                    Text("فتح ←", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        item {
            Text(
                "تنبيه: مقارنة التسميع نصية وتقريبية، وليست تصحيحًا آليًا للتجويد.",
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                textAlign = TextAlign.Center,
                color = colors.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, fontSize = 19.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
        Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HeroTag(label: String) {
    Surface(color = Color.White.copy(alpha = .15f), shape = RoundedCornerShape(50)) {
        Text(label, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
    }
}

@Composable
private fun HomeActionCard(
    icon: String,
    title: String,
    subtitle: String,
    background: Color,
    foreground: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 76.dp),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(icon, fontSize = 25.sp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, fontWeight = FontWeight.ExtraBold, color = foreground, fontSize = 14.sp)
                Text(subtitle, color = foreground.copy(alpha = .78f), fontSize = 10.sp)
            }
            Text("←", color = foreground, fontSize = 20.sp)
        }
    }
}

@Composable
private fun ToolTile(tool: HomeTool, colors: ColorScheme, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.heightIn(min = 104.dp),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(tool.icon, fontSize = 19.sp, color = colors.primary)
            Text(tool.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(tool.detail, fontSize = 10.sp, color = colors.onSurfaceVariant)
        }
    }
}
