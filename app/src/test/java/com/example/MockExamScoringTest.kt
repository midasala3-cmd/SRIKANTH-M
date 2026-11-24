package com.example

import com.example.data.model.MockExamOption
import com.example.data.model.MockExamQuestion
import com.example.data.model.ScoringRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockExamScoringTest {

    private val sampleQuestions = listOf(
        MockExamQuestion(
            orderIndex = 0,
            section = "AP History",
            questionEn = "Q1",
            options = List(4) { MockExamOption(key = "${'A' + it}", textEn = "Opt $it") },
            correctOptionIndex = 1
        ),
        MockExamQuestion(
            orderIndex = 1,
            section = "Polity",
            questionEn = "Q2",
            options = List(4) { MockExamOption(key = "${'A' + it}", textEn = "Opt $it") },
            correctOptionIndex = 2
        ),
        MockExamQuestion(
            orderIndex = 2,
            section = "Economy",
            questionEn = "Q3",
            options = List(4) { MockExamOption(key = "${'A' + it}", textEn = "Opt $it") },
            correctOptionIndex = 0
        ),
        MockExamQuestion(
            orderIndex = 3,
            section = "Reasoning",
            questionEn = "Q4",
            options = List(4) { MockExamOption(key = "${'A' + it}", textEn = "Opt $it") },
            correctOptionIndex = 3
        )
    )

    @Test
    fun appscStandardNegativeMarkingCalculatesCorrectNetScore() {
        // 2 correct (+2.0), 1 wrong (-0.33), 1 skipped (0.0) -> 1.67 / 4.0
        val answers = mapOf(0 to 1, 1 to 2, 2 to 3)
        val result = ScoringRules.APPSC_STANDARD.evaluate(sampleQuestions, answers)

        assertEquals(2, result.correctCount)
        assertEquals(1, result.wrongCount)
        assertEquals(1, result.unansweredCount)
        assertEquals(2.0, result.positiveMarks, 0.001)
        assertEquals(0.33, result.negativePenalty, 0.001)
        assertEquals(1.67, result.netScore, 0.001)
        assertEquals(4.0, result.maxScore, 0.001)
        assertEquals(66.67, result.accuracyPercent, 0.01)
        assertTrue(result.passed)
    }

    @Test
    fun apPoliceStandardNegativeMarkingCalculatesQuarterPenalty() {
        // 1 correct (+1.0), 2 wrong (-0.50), 1 skipped -> 0.50 / 4.0 (12.5% < 35% pass)
        val answers = mapOf(0 to 1, 1 to 0, 2 to 1)
        val result = ScoringRules.AP_POLICE_STANDARD.evaluate(sampleQuestions, answers)

        assertEquals(1, result.correctCount)
        assertEquals(2, result.wrongCount)
        assertEquals(1, result.unansweredCount)
        assertEquals(0.50, result.negativePenalty, 0.001)
        assertEquals(0.50, result.netScore, 0.001)
        assertFalse(result.passed)
    }

    @Test
    fun disabledNegativeMarkingIgnoresWrongAnswerPenalty() {
        val rules = ScoringRules(
            marksPerCorrect = 2.0,
            negativeMarkPerWrong = 0.5,
            negativeMarkingEnabled = false,
            passingPercentage = 40.0
        )
        val answers = mapOf(0 to 1, 1 to 0, 2 to 1, 3 to 3)
        val result = rules.evaluate(sampleQuestions, answers)

        assertEquals(2, result.correctCount)
        assertEquals(2, result.wrongCount)
        assertEquals(0.0, result.negativePenalty, 0.001)
        assertEquals(4.0, result.netScore, 0.001)
        assertEquals(8.0, result.maxScore, 0.001)
        assertTrue(result.passed)
    }
}
