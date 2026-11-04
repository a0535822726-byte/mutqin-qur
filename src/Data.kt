package com.mutqin.app

import android.content.Context

val SURAH_NAMES = listOf(
  "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس",
  "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه",
  "الأنبياء", "الحج", "المؤمنون", "النور", "الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت", "الروم",
  "لقمان", "السجدة", "الأحزاب", "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر",
  "فصلت", "الشورى", "الزخرف", "الدخان", "الجاثية", "الأحقاف", "محمد", "الفتح", "الحجرات", "ق",
  "الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة", "الحشر", "الممتحنة",
  "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج",
  "نوح", "الجن", "المزمل", "المدثر", "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس",
  "التكوير", "الانفطار", "المطففين", "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر", "البلد",
  "الشمس", "الليل", "الضحى", "الشرح", "التين", "العلق", "القدر", "البينة", "الزلزلة", "العاديات",
  "القارعة", "التكاثر", "العصر", "الهمزة", "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون", "النصر",
  "المسد", "الإخلاص", "الفلق", "الناس"
)

class QuranRepository(private val ayahs: List<List<String>>) {
  fun surahCount(): Int = ayahs.size
  fun ayahCount(surah: Int): Int = ayahs[surah - 1].size
  fun ayah(surah: Int, number: Int): String = ayahs[surah - 1][number - 1]

  companion object {
    fun load(context: Context): QuranRepository {
      val result = List(114) { mutableListOf<String>() }
      context.assets.open("quran.txt").bufferedReader(Charsets.UTF_8).useLines { lines ->
        for (line in lines) {
          val parts = line.split("|", limit = 3)
          if (parts.size < 3) continue
          val s = parts[0].trim().toIntOrNull() ?: continue
          if (s in 1..114) result[s - 1].add(parts[2].trim())
        }
      }
      return QuranRepository(result)
    }
  }
}

class AppPrefs(context: Context) {
  private val p = context.getSharedPreferences("mutqin", Context.MODE_PRIVATE)

  fun darkOrNull(): Boolean? = if (p.contains("dark")) p.getBoolean("dark", false) else null
  fun setDark(v: Boolean) { p.edit().putBoolean("dark", v).apply() }

  fun fontSize(): Float = p.getFloat("font_size", 28f)
  fun setFontSize(v: Float) { p.edit().putFloat("font_size", v).apply() }

  fun lastPosition(): Triple<Int, Int, Int>? {
    val s = p.getInt("last_s", 0)
    val f = p.getInt("last_f", 0)
    val t = p.getInt("last_t", 0)
    return if (s in 1..114 && f >= 1 && t >= f) Triple(s, f, t) else null
  }
  fun setLastPosition(s: Int, f: Int, t: Int) {
    p.edit().putInt("last_s", s).putInt("last_f", f).putInt("last_t", t).apply()
  }
  fun bookmark(s: Int): Int? = p.getInt("bookmark_$s", 0).takeIf { it > 0 }
  fun setBookmark(s: Int, ayah: Int) { p.edit().putInt("bookmark_$s", ayah).apply() }
  fun toolInt(key: String, fallback: Int = 0): Int = p.getInt("tool_$key", fallback)
  fun setToolInt(key: String, value: Int) { p.edit().putInt("tool_$key", value).apply() }
  fun toolText(key: String, fallback: String = ""): String = p.getString("tool_$key", fallback) ?: fallback
  fun setToolText(key: String, value: String) { p.edit().putString("tool_$key", value).apply() }
  fun toolFlag(key: String): Boolean = p.getBoolean("tool_$key", false)
  fun setToolFlag(key: String, value: Boolean) { p.edit().putBoolean("tool_$key", value).apply() }
}

fun toArabic(n: Int): String = n.toString().map { '٠' + (it - '0') }.joinToString("")

fun normalizeAr(s: String): String = s
  .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
  .replace('ى', 'ي').replace('ة', 'ه')
