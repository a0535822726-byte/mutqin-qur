package com.mutqin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun SpacedReviewScreen(repo: QuranRepository, prefs: AppPrefs, onBack: () -> Unit) {
    var surahText by remember { mutableStateOf(prefs.toolText("spaced_surah", "1")) }
    var fromText by remember { mutableStateOf(prefs.toolText("spaced_from", "1")) }
    var toText by remember { mutableStateOf(prefs.toolText("spaced_to", "7")) }
    var message by remember { mutableStateOf("") }
    val surah = surahText.toIntOrNull()?.coerceIn(1, repo.surahCount()) ?: 1
    val from = fromText.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val to = toText.toIntOrNull()?.coerceAtLeast(from) ?: from
    val saved = prefs.toolFlag("spaced_plan_saved")
    val planDate = runCatching { LocalDate.parse(prefs.toolText("spaced_plan_date")) }.getOrNull()
    val colors = MaterialTheme.colorScheme

    Column(Modifier.fillMaxSize().background(colors.background).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("رجوع") }
            Column(Modifier.weight(1f)) {
                Text("مراجعة الحفظ المتباعدة", fontSize = 21.sp, fontWeight = FontWeight.Black, color = colors.primary)
                Text("جدول مراجعة يساعدك على تثبيت محفوظك", fontSize = 11.sp, color = colors.onSurfaceVariant)
            }
            Text("🧠", fontSize = 26.sp)
        }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("حدد موضع الحفظ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(value = surahText, onValueChange = { surahText = it.filter(Char::isDigit).take(3); prefs.setToolText("spaced_surah", surahText) }, label = { Text("رقم السورة (1–114)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                Text(SURAH_NAMES[surah - 1], color = colors.primary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fromText, onValueChange = { fromText = it.filter(Char::isDigit).take(4); prefs.setToolText("spaced_from", fromText) }, label = { Text("من آية") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = toText, onValueChange = { toText = it.filter(Char::isDigit).take(4); prefs.setToolText("spaced_to", toText) }, label = { Text("إلى آية") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true)
                }
                Text("عدد آيات السورة: ${toArabic(repo.ayahCount(surah))}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                Button(onClick = {
                    val max = repo.ayahCount(surah)
                    if (from !in 1..max || to !in from..max) message = "تأكد من أرقام الآيات؛ النطاق غير صحيح."
                    else {
                        prefs.setToolText("spaced_surah", surah.toString())
                        prefs.setToolText("spaced_from", from.toString())
                        prefs.setToolText("spaced_to", to.toString())
                        prefs.setToolText("spaced_plan_date", LocalDate.now().toString())
                        prefs.setToolFlag("spaced_plan_saved", true)
                        listOf(0, 1, 3, 7, 14).forEach { prefs.setToolFlag("spaced_done_$it", false) }
                        message = "تم إنشاء جدول المراجعة وحفظه على جهازك."
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("إنشاء جدول المراجعة") }
                if (message.isNotBlank()) Text(message, color = colors.primary, fontSize = 12.sp)
            }
        }
        if (saved && planDate != null) {
            val max = repo.ayahCount(surah)
            if (from in 1..max && to in from..max) {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.primaryContainer)) {
                    Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("جدولك الحالي", fontSize = 17.sp, fontWeight = FontWeight.Black, color = colors.onPrimaryContainer)
                        Text("${SURAH_NAMES[surah - 1]} • الآيات ${toArabic(from)}–${toArabic(to)}", color = colors.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        Text("ضع علامة بعد إتمام المراجعة، وستبقى محفوظة على جهازك.", fontSize = 11.sp, color = colors.onPrimaryContainer)
                        listOf(0, 1, 3, 7, 14).forEach { day ->
                            val date = planDate.plusDays(day.toLong())
                            val done = prefs.toolFlag("spaced_done_$day")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = done, onCheckedChange = { prefs.setToolFlag("spaced_done_$day", it); message = if (it) "تم تسجيل المراجعة." else "أُزيلت علامة الإنجاز." })
                                Column(Modifier.weight(1f)) {
                                    Text(if (day == 0) "مراجعة اليوم" else "بعد $day أيام", fontWeight = FontWeight.SemiBold, color = colors.onPrimaryContainer)
                                    Text(date.toString(), fontSize = 10.sp, color = colors.onPrimaryContainer.copy(alpha = .75f))
                                }
                                if (done) Text("مكتملة", color = colors.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        Text("هذا جدول تذكيري عام للمراجعة، وليس تقييماً علمياً لمستوى الحفظ. راجع مع معلّمك وصحّح التلاوة لديه.", fontSize = 10.sp, color = colors.onSurfaceVariant)
    }
}
