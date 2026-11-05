package com.mutqin.app

data class RecitationComparison(
    val referenceWords: List<String>,
    val heardWords: List<String>,
    val matchedReferenceIndices: List<Int>,
    val matchedHeardIndices: List<Int>,
    val percentage: Int?
) {
    val unmatchedReferenceIndices: List<Int>
        get() = referenceWords.indices.filterNot(matchedReferenceIndices.toSet()::contains)
    val unmatchedHeardIndices: List<Int>
        get() = heardWords.indices.filterNot(matchedHeardIndices.toSet()::contains)
}

fun normalizeRecitationWords(value: String): List<String> {
    val withoutMarks = value
        .replace('\u0640'.toString(), "")
        .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"), "")
    return withoutMarks
        .replace(Regex("[^\\u0621-\\u064A\\s]"), " ")
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ٱ', 'ا')
        .replace('ى', 'ي')
        .trim()
        .split(Regex("\\s+"))
        .filter(String::isNotBlank)
}

fun compareRecitation(reference: String, heard: String): RecitationComparison {
    val expected = normalizeRecitationWords(reference)
    val actual = normalizeRecitationWords(heard)
    if (expected.isEmpty()) return RecitationComparison(expected, actual, emptyList(), emptyList(), null)

    val table = Array(expected.size + 1) { IntArray(actual.size + 1) }
    for (i in expected.indices.reversed()) {
        for (j in actual.indices.reversed()) {
            table[i][j] = if (expected[i] == actual[j]) {
                table[i + 1][j + 1] + 1
            } else {
                maxOf(table[i + 1][j], table[i][j + 1])
            }
        }
    }

    val expectedMatches = mutableListOf<Int>()
    val actualMatches = mutableListOf<Int>()
    var i = 0
    var j = 0
    while (i < expected.size && j < actual.size) {
        if (expected[i] == actual[j]) {
            expectedMatches += i
            actualMatches += j
            i++
            j++
        } else if (table[i + 1][j] >= table[i][j + 1]) {
            i++
        } else {
            j++
        }
    }
    val percent = if (actual.isEmpty()) null
        else ((expectedMatches.size * 100f) / expected.size).toInt().coerceIn(0, 100)
    return RecitationComparison(expected, actual, expectedMatches, actualMatches, percent)
}
