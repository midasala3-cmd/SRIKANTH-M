package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlin.math.round

@Keep
@IgnoreExtraProperties
data class ScoringRules(
    val marksPerCorrect: Double = 1.0,
    val negativeMarkPerWrong: Double = 0.33,
    val unansweredPenalty: Double = 0.0,
    val negativeMarkingEnabled: Boolean = true,
    val passingPercentage: Double = 35.0
) {
    fun effectiveNegativePenalty(): Double =
        if (negativeMarkingEnabled) negativeMarkPerWrong.coerceIn(0.0, 5.0) else 0.0

    fun toMap(): Map<String, Any> = mapOf(
        "marksPerCorrect" to marksPerCorrect.coerceIn(0.5, 10.0),
        "negativeMarkPerWrong" to negativeMarkPerWrong.coerceIn(0.0, 5.0),
        "unansweredPenalty" to unansweredPenalty.coerceIn(0.0, 2.0),
        "negativeMarkingEnabled" to negativeMarkingEnabled,
        "passingPercentage" to passingPercentage.coerceIn(0.0, 100.0)
    )

    fun evaluate(
        questions: List<MockExamQuestion>,
        selectedAnswers: Map<Int, Int>
    ): ExamScoreBreakdown {
        var correctCount = 0
        var wrongCount = 0
        var unansweredCount = 0
        var positiveMarks = 0.0
        var negativePenalty = 0.0
        var maxScore = 0.0

        val wrongPenalty = effectiveNegativePenalty()
        val skipPenalty = unansweredPenalty.coerceIn(0.0, 2.0)

        questions.forEachIndexed { index, question ->
            val questionMarks = (question.marksOverride ?: marksPerCorrect).coerceIn(0.5, 10.0)
            maxScore += questionMarks
            val chosen = selectedAnswers[index]
            when {
                chosen == null -> {
                    unansweredCount++
                    negativePenalty += skipPenalty
                }
                chosen == question.correctOptionIndex -> {
                    correctCount++
                    positiveMarks += questionMarks
                }
                else -> {
                    wrongCount++
                    negativePenalty += wrongPenalty
                }
            }
        }

        val netScore = roundTwoDecimals(positiveMarks - negativePenalty)
        val roundedMax = roundTwoDecimals(maxScore.coerceAtLeast(1.0))
        val attempted = correctCount + wrongCount
        val accuracy = if (attempted > 0) {
            roundTwoDecimals((correctCount.toDouble() / attempted) * 100.0)
        } else 0.0
        val scorePercent = roundTwoDecimals(((netScore / roundedMax) * 100.0).coerceIn(0.0, 100.0))

        return ExamScoreBreakdown(
            totalQuestions = questions.size,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unansweredCount = unansweredCount,
            positiveMarks = roundTwoDecimals(positiveMarks),
            negativePenalty = roundTwoDecimals(negativePenalty),
            netScore = netScore,
            maxScore = roundedMax,
            accuracyPercent = accuracy,
            scorePercentage = scorePercent,
            passed = scorePercent >= passingPercentage
        )
    }

    companion object {
        val APPSC_STANDARD = ScoringRules(
            marksPerCorrect = 1.0,
            negativeMarkPerWrong = 0.33,
            unansweredPenalty = 0.0,
            negativeMarkingEnabled = true,
            passingPercentage = 35.0
        )

        val AP_POLICE_STANDARD = ScoringRules(
            marksPerCorrect = 1.0,
            negativeMarkPerWrong = 0.25,
            unansweredPenalty = 0.0,
            negativeMarkingEnabled = true,
            passingPercentage = 35.0
        )

        val NO_NEGATIVE = ScoringRules(
            marksPerCorrect = 1.0,
            negativeMarkPerWrong = 0.0,
            unansweredPenalty = 0.0,
            negativeMarkingEnabled = false,
            passingPercentage = 40.0
        )

        fun fromMap(map: Map<*, *>?, fallbackMarks: Double, fallbackNeg: Double): ScoringRules {
            if (map == null) {
                return ScoringRules(
                    marksPerCorrect = fallbackMarks.coerceIn(0.5, 10.0),
                    negativeMarkPerWrong = fallbackNeg.coerceIn(0.0, 5.0),
                    unansweredPenalty = 0.0,
                    negativeMarkingEnabled = fallbackNeg > 0.0,
                    passingPercentage = 35.0
                )
            }
            val marks = ((map["marksPerCorrect"] as? Number)?.toDouble() ?: fallbackMarks).coerceIn(0.5, 10.0)
            val neg = ((map["negativeMarkPerWrong"] as? Number)?.toDouble() ?: fallbackNeg).coerceIn(0.0, 5.0)
            val skip = ((map["unansweredPenalty"] as? Number)?.toDouble() ?: 0.0).coerceIn(0.0, 2.0)
            val enabled = (map["negativeMarkingEnabled"] as? Boolean) ?: (neg > 0.0)
            val passPct = ((map["passingPercentage"] as? Number)?.toDouble() ?: 35.0).coerceIn(0.0, 100.0)
            return ScoringRules(
                marksPerCorrect = marks,
                negativeMarkPerWrong = neg,
                unansweredPenalty = skip,
                negativeMarkingEnabled = enabled,
                passingPercentage = passPct
            )
        }

        private fun roundTwoDecimals(value: Double): Double = round(value * 100.0) / 100.0
    }
}

data class ExamScoreBreakdown(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val positiveMarks: Double,
    val negativePenalty: Double,
    val netScore: Double,
    val maxScore: Double,
    val accuracyPercent: Double,
    val scorePercentage: Double,
    val passed: Boolean
)

@Keep
@IgnoreExtraProperties
data class MockExamOption(
    val key: String = "A",
    val textEn: String = "",
    val textTe: String = ""
) {
    fun text(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && textTe.isNotBlank()) textTe else textEn

    fun toMap(): Map<String, Any> = mapOf(
        "key" to key.trim().ifBlank { "A" },
        "textEn" to textEn.trim(),
        "textTe" to textTe.trim().ifBlank { textEn.trim() }
    )
}

@Keep
@IgnoreExtraProperties
data class MockExamQuestion(
    val orderIndex: Int = 0,
    val section: String = "General Studies",
    val questionEn: String = "",
    val questionTe: String = "",
    val options: List<MockExamOption> = emptyList(),
    val correctOptionIndex: Int = 0,
    val explanationEn: String = "",
    val explanationTe: String = "",
    val difficulty: String = "MEDIUM",
    val marksOverride: Double? = null
) {
    fun toFirestoreMap(): Map<String, Any> {
        val optionKeys = listOf("A", "B", "C", "D")
        val normalizedOptions = options.take(4).mapIndexed { idx, opt ->
            opt.copy(key = opt.key.ifBlank { optionKeys.getOrElse(idx) { "${idx + 1}" } })
        }
        val optEn = normalizedOptions.map { it.textEn.trim() }
        val optTe = normalizedOptions.map { it.textTe.trim().ifBlank { it.textEn.trim() } }

        val rawMap: Map<String, Any?> = mapOf(
            "orderIndex" to orderIndex.coerceAtLeast(0),
            "section" to section.trim().ifBlank { "General Studies" },
            "questionEn" to questionEn.trim(),
            "questionTe" to questionTe.trim().ifBlank { questionEn.trim() },
            "options" to normalizedOptions.map { it.toMap() },
            "optionsEn" to optEn,
            "optionsTe" to optTe,
            "correctOptionIndex" to correctOptionIndex.coerceIn(0, 3),
            "correctIndex" to correctOptionIndex.coerceIn(0, 3),
            "explanationEn" to explanationEn.trim(),
            "explanationTe" to explanationTe.trim().ifBlank { explanationEn.trim() },
            "difficulty" to difficulty.uppercase().takeIf { it in setOf("EASY", "MEDIUM", "HARD") }.orEmpty().ifBlank { "MEDIUM" },
            "marksOverride" to marksOverride?.coerceIn(0.5, 10.0)
        )
        @Suppress("UNCHECKED_CAST")
        return rawMap.filterValues { it != null } as Map<String, Any>
    }

    fun toCbtQuestion(): CbtQuestion {
        val optEn = options.take(4).map { it.textEn }
        val optTe = options.take(4).map { it.textTe.ifBlank { it.textEn } }
        return CbtQuestion(
            questionEn = questionEn,
            questionTe = questionTe.ifBlank { questionEn },
            optionsEn = optEn,
            optionsTe = optTe,
            correctIndex = correctOptionIndex.coerceIn(0, 3),
            explanationEn = explanationEn,
            explanationTe = explanationTe.ifBlank { explanationEn },
            section = section
        )
    }

    companion object {
        fun fromCbtQuestion(index: Int, q: CbtQuestion): MockExamQuestion {
            val keys = listOf("A", "B", "C", "D")
            val structuredOptions = q.optionsEn.take(4).mapIndexed { idx, enText ->
                MockExamOption(
                    key = keys.getOrElse(idx) { "${idx + 1}" },
                    textEn = enText,
                    textTe = q.optionsTe.getOrElse(idx) { enText }.ifBlank { enText }
                )
            }
            return MockExamQuestion(
                orderIndex = index,
                section = q.section,
                questionEn = q.questionEn,
                questionTe = q.questionTe,
                options = structuredOptions,
                correctOptionIndex = q.correctIndex.coerceIn(0, 3),
                explanationEn = q.explanationEn,
                explanationTe = q.explanationTe
            )
        }
    }
}

@Keep
@IgnoreExtraProperties
data class MockExamStructure(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val titleEn: String = "",
    val titleTe: String = "",
    val descriptionEn: String = "",
    val descriptionTe: String = "",
    val examCategory: String = ExamCategory.APPSC_GROUP2.code,
    val durationMinutes: Int = 30,
    val scoringRules: ScoringRules = ScoringRules.APPSC_STANDARD,
    val freeDemo: Boolean = true,
    val status: String = "published",
    val questions: List<MockExamQuestion> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMockTest(bundledStarter: Boolean = false): MockTest = MockTest(
        id = id,
        authorId = authorId,
        titleEn = titleEn,
        titleTe = titleTe,
        descriptionEn = descriptionEn,
        descriptionTe = descriptionTe,
        examCategory = examCategory,
        durationMinutes = durationMinutes,
        marksPerQuestion = scoringRules.marksPerCorrect,
        negativeMarking = scoringRules.effectiveNegativePenalty(),
        freeDemo = freeDemo,
        status = status,
        questions = questions.map { it.toCbtQuestion() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        bundledStarter = bundledStarter
    )
}
