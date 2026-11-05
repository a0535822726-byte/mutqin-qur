package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuranWordSearchScreen(repo: QuranRepository, onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var searched by remember { mutableStateOf(false) }
    val results = remember(query, searched) {
        if (!searched || query.trim().length < 2) emptyList()
        else buildList {
            for (s in 1..repo.surahCount()) {
                for (a in 1..repo.ayahCount(s)) {
                    val text = repo.ayah(s, a)
                    if (normalizeAr(text).contains(normalizeAr(query.trim()))) {
                        add(Triple(s, a, text))
                        if (size >= 100) return@buildList
                    }
                }
            }
        }
    }
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("رجوع") }
        Text("باحث ألفاظ القرآن", fontSize = 25.sp, fontWeight = FontWeight.Black, color = colors.primary)
        Text("ابحث عن كلمة أو جزء من كلمة داخل نص المصحف الموجود في التطبيق.", color = colors.onSurfaceVariant)
        OutlinedTextField(query, { query = it; searched = false }, Modifier.fillMaxWidth(), label = { Text("اكتب كلمة للبحث") }, singleLine = true)
        Button(onClick = { searched = true }, enabled = query.trim().length >= 2, modifier = Modifier.fillMaxWidth()) { Text("ابحث في القرآن") }
        if (searched) {
            Text(if (results.isEmpty()) "لم يُعثر على نتائج ضمن النص المتاح." else "عدد النتائج المعروضة: ${results.size}", fontWeight = FontWeight.Bold, color = colors.primary)
            results.forEach { (s, a, verse) ->
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
                    Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("${SURAH_NAMES[s-1]} • الآية ${toArabic(a)}", color = colors.primary, fontWeight = FontWeight.Bold)
                        Text(verse, fontSize = 19.sp, lineHeight = 32.sp)
                    }
                }
            }
        }
        Text("ملاحظة: البحث نصّي، وقد لا يشمل اختلافات الرسم أو التشكيل في كل صور الكلمة.", fontSize = 11.sp, color = colors.onSurfaceVariant)
    }
}

@Composable
fun AyahContextScreen(repo: QuranRepository, onBack: () -> Unit) {
    var surahText by remember { mutableStateOf("1") }
    var ayahText by remember { mutableStateOf("1") }
    val s = (surahText.toIntOrNull() ?: 1).coerceIn(1, 114)
    val a = (ayahText.toIntOrNull() ?: 1).coerceIn(1, repo.ayahCount(s))
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("رجوع") }
        Text("نافذة سياق الآية", fontSize = 25.sp, fontWeight = FontWeight.Black, color = colors.primary)
        Text("اعرض الآية مع ما قبلها وما بعدها لتسهيل الربط أثناء القراءة والمراجعة.", color = colors.onSurfaceVariant)
        OutlinedTextField(surahText, { surahText = it.filter(Char::isDigit).take(3) }, Modifier.fillMaxWidth(), label = { Text("رقم السورة من 1 إلى 114") }, singleLine = true)
        Text("${SURAH_NAMES[s-1]} • ${toArabic(repo.ayahCount(s))} آية", color = colors.primary, fontWeight = FontWeight.Bold)
        OutlinedTextField(ayahText, { ayahText = it.filter(Char::isDigit).take(4) }, Modifier.fillMaxWidth(), label = { Text("رقم الآية") }, singleLine = true)
        for (n in (a-1).coerceAtLeast(1)..(a+1).coerceAtMost(repo.ayahCount(s))) {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (n == a) colors.primaryContainer else colors.surface)) {
                Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (n == a) "الآية المحددة • ${toArabic(n)}" else "الآية ${toArabic(n)}", fontWeight = FontWeight.Bold, color = colors.primary)
                    Text(repo.ayah(s,n), fontSize = 21.sp, lineHeight = 38.sp, color = colors.onSurface)
                }
            }
        }
        Text("هذه نافذة عرض للنص المتجاور، وليست تفسيرًا لمعنى الآيات.", fontSize = 11.sp, color = colors.onSurfaceVariant)
    }
}

@Composable
fun SurahStatisticsScreen(repo: QuranRepository, onBack: () -> Unit) {
    var surahText by remember { mutableStateOf("1") }
    val s = (surahText.toIntOrNull() ?: 1).coerceIn(1,114)
    val verses = (1..repo.ayahCount(s)).map { repo.ayah(s,it) }
    val allText = verses.joinToString(" ")
    val letters = allText.count { it in 'ء'..'ي' }
    val words = allText.trim().split(Regex("\\s+")).count { it.isNotBlank() }
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        TextButton(onClick = onBack) { Text("رجوع") }
        Text("إحصاءات السورة", fontSize = 25.sp, fontWeight = FontWeight.Black, color = colors.primary)
        Text("أرقام محسوبة مباشرة من نص السورة داخل التطبيق.", color = colors.onSurfaceVariant)
        OutlinedTextField(surahText, { surahText = it.filter(Char::isDigit).take(3) }, Modifier.fillMaxWidth(), label = { Text("رقم السورة") }, singleLine = true)
        Text(SURAH_NAMES[s-1], fontSize = 20.sp, fontWeight = FontWeight.Bold)
        listOf("عدد الآيات" to repo.ayahCount(s).toString(), "عدد الكلمات التقريبي" to words.toString(), "عدد الحروف العربية التقريبي" to letters.toString()).forEach { (label,value) ->
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, fontWeight = FontWeight.SemiBold)
                    Text(value, color = colors.primary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Text("طريقة العد تقريبية: تُحسب الحروف العربية الظاهرة في النص، ولا تُعدّ هذه الأرقام إحصاءً رسمياً للرسم العثماني.", fontSize = 11.sp, color = colors.onSurfaceVariant)
    }
}
