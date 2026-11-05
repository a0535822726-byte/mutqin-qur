package com.mutqin.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecitationTextTest {
    @Test fun exactWordsMatchFully() {
        val result = compareRecitation("بسم الله الرحمن الرحيم", "بسم الله الرحمن الرحيم")
        assertEquals(100, result.percentage)
        assertEquals(4, result.matchedReferenceIndices.size)
    }

    @Test fun diacriticsAndHamzaFormsDoNotCreateFalseDifferences() {
        val result = compareRecitation("إِنَّ اللَّهَ", "ان الله")
        assertEquals(100, result.percentage)
    }

    @Test fun missingWordIsReportedByReferenceIndex() {
        val result = compareRecitation("بسم الله الرحمن الرحيم", "بسم الرحمن الرحيم")
        assertEquals(75, result.percentage)
        assertEquals(listOf(1), result.unmatchedReferenceIndices)
    }

    @Test fun reorderedWordsAreNotCountedAsExactSequence() {
        val result = compareRecitation("الله الرحمن", "الرحمن الله")
        assertEquals(50, result.percentage)
    }

    @Test fun emptyRecognitionHasNoPercentage() {
        val result = compareRecitation("الحمد لله", "")
        assertNull(result.percentage)
        assertEquals(listOf(0, 1), result.unmatchedReferenceIndices)
    }

    @Test fun partialSpeechDoesNotMarkUnreadWordsAsErrors() {
        val result = compareRecitation("بسم الله الرحمن الرحيم", "بسم الله")
        assertEquals(emptyList<Int>(), result.suspectedErrorReferenceIndices)
    }

    @Test fun recognizedWrongWordHighlightsOnlyItsExpectedPosition() {
        val result = compareRecitation("بسم الله الرحمن الرحيم", "بسم الملك")
        assertEquals(listOf(1), result.suspectedErrorReferenceIndices)
    }

    @Test fun skippedWordBetweenMatchedWordsIsFlagged() {
        val result = compareRecitation("بسم الله الرحمن الرحيم", "بسم الرحمن الرحيم")
        assertEquals(listOf(1), result.suspectedErrorReferenceIndices)
    }

    @Test fun punctuationAndTatweelAreIgnored() {
        val result = compareRecitation("الرَّحْمَٰنُ", "الرَّحْمَانُ،")
        assertEquals(100, result.percentage)
    }
}
