package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class AppLanguage(val code: String, val label: String) {
    EN("EN", "English"),
    TE("TE", "తెలుగు");

    companion object {
        fun fromCode(raw: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(raw, ignoreCase = true) } ?: EN
    }
}

enum class ExamCategory(
    val code: String,
    val labelEn: String,
    val labelTe: String,
    val shortBadge: String
) {
    APPSC_GROUP1("APPSC_GROUP1", "APPSC Group 1", "APPSC గ్రూప్ 1", "Group 1"),
    APPSC_GROUP2("APPSC_GROUP2", "APPSC Group 2", "APPSC గ్రూప్ 2", "Group 2"),
    APPSC_GROUP3_4("APPSC_GROUP3_4", "APPSC Group 3 & 4", "APPSC గ్రూప్ 3 & 4", "Group 3/4"),
    AP_POLICE_SI("AP_POLICE_SI", "AP Police SI", "ఏపీ పోలీస్ ఎస్ఐ", "Police SI"),
    AP_POLICE_CONSTABLE("AP_POLICE_CONSTABLE", "AP Police Constable", "ఏపీ పోలీస్ కానిస్టేబుల్", "Constable");

    fun label(lang: AppLanguage): String = if (lang == AppLanguage.TE) labelTe else labelEn

    companion object {
        fun fromCode(raw: String?): ExamCategory =
            entries.firstOrNull { it.code == raw } ?: APPSC_GROUP2
    }
}

enum class BlogCategory(
    val code: String,
    val labelEn: String,
    val labelTe: String
) {
    APPSC("APPSC", "APPSC Guides", "APPSC గైడ్లు"),
    AP_POLICE("AP_POLICE", "AP Police", "ఏపీ పోలీస్"),
    CURRENT_AFFAIRS("CURRENT_AFFAIRS", "AP Current Affairs", "ఏపీ కరెంట్ అఫైర్స్"),
    STRATEGY("STRATEGY", "Topper Strategy", "టాపర్ వ్యూహం");

    fun label(lang: AppLanguage): String = if (lang == AppLanguage.TE) labelTe else labelEn

    companion object {
        fun fromCode(raw: String?): BlogCategory =
            entries.firstOrNull { it.code == raw } ?: APPSC
    }
}

data class CoachingCourse(
    val id: String,
    val examCategory: ExamCategory,
    val titleEn: String,
    val titleTe: String,
    val subtitleEn: String,
    val subtitleTe: String,
    val durationLabelEn: String,
    val durationLabelTe: String,
    val mockTestsCount: Int,
    val videoHours: Int,
    val priceDisplay: String,
    val originalPriceDisplay: String,
    val highlightsEn: List<String>,
    val highlightsTe: List<String>,
    val featuredBadgeEn: String,
    val featuredBadgeTe: String
)

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Aspirant",
    val preferredLanguage: String = "EN",
    val targetExam: String = "APPSC_GROUP2",
    val enrolledCourseIds: List<String> = emptyList(),
    val bookmarkedBlogIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CbtQuestion(
    val questionEn: String = "",
    val questionTe: String = "",
    val optionsEn: List<String> = listOf("", "", "", ""),
    val optionsTe: List<String> = listOf("", "", "", ""),
    val correctIndex: Int = 0,
    val explanationEn: String = "",
    val explanationTe: String = "",
    val section: String = "General Studies"
) {
    fun questionText(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && questionTe.isNotBlank()) questionTe else questionEn

    fun options(lang: AppLanguage): List<String> =
        if (lang == AppLanguage.TE && optionsTe.size == 4 && optionsTe.all { it.isNotBlank() }) optionsTe else optionsEn

    fun explanation(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && explanationTe.isNotBlank()) explanationTe else explanationEn

    fun toMap(): Map<String, Any> = mapOf(
        "questionEn" to questionEn.trim(),
        "questionTe" to questionTe.trim().ifBlank { questionEn.trim() },
        "optionsEn" to optionsEn.map { it.trim() },
        "optionsTe" to optionsTe.mapIndexed { idx, s -> s.trim().ifBlank { optionsEn.getOrElse(idx) { "" }.trim() } },
        "correctIndex" to correctIndex.coerceIn(0, 3),
        "explanationEn" to explanationEn.trim(),
        "explanationTe" to explanationTe.trim().ifBlank { explanationEn.trim() },
        "section" to section.trim().ifBlank { "General Studies" }
    )
}

@Keep
@IgnoreExtraProperties
data class MockTest(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val titleEn: String = "",
    val titleTe: String = "",
    val descriptionEn: String = "",
    val descriptionTe: String = "",
    val examCategory: String = "APPSC_GROUP2",
    val durationMinutes: Int = 15,
    val marksPerQuestion: Double = 1.0,
    val negativeMarking: Double = 0.33,
    val freeDemo: Boolean = true,
    val status: String = "published",
    val questions: List<CbtQuestion> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    val bundledStarter: Boolean = false
) {
    fun title(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && titleTe.isNotBlank()) titleTe else titleEn

    fun description(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && descriptionTe.isNotBlank()) descriptionTe else descriptionEn

    fun categoryEnum(): ExamCategory = ExamCategory.fromCode(examCategory)
}

@Keep
@IgnoreExtraProperties
data class ExamAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val testId: String = "",
    val testTitleEn: String = "",
    val testTitleTe: String = "",
    val examCategory: String = "APPSC_GROUP2",
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unansweredCount: Int = 0,
    val score: Double = 0.0,
    val maxScore: Double = 1.0,
    val negativeMarking: Double = 0.33,
    val timeTakenSeconds: Int = 0,
    val accuracyPercent: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun title(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && testTitleTe.isNotBlank()) testTitleTe else testTitleEn
}

@Keep
@IgnoreExtraProperties
data class StudyBlog(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val titleEn: String = "",
    val titleTe: String = "",
    val slug: String = "",
    val metaDescription: String = "",
    val seoKeywords: List<String> = emptyList(),
    val category: String = "APPSC",
    val summaryEn: String = "",
    val summaryTe: String = "",
    val contentEn: String = "",
    val contentTe: String = "",
    val readTimeMinutes: Int = 5,
    val status: String = "published",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    val bundledStarter: Boolean = false
) {
    fun title(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && titleTe.isNotBlank()) titleTe else titleEn

    fun summary(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && summaryTe.isNotBlank()) summaryTe else summaryEn

    fun content(lang: AppLanguage): String =
        if (lang == AppLanguage.TE && contentTe.isNotBlank()) contentTe else contentEn

    fun categoryEnum(): BlogCategory = BlogCategory.fromCode(category)
}

enum class QuestionPaletteStatus {
    NOT_VISITED,
    NOT_ANSWERED,
    ANSWERED,
    MARKED_FOR_REVIEW,
    ANSWERED_AND_MARKED
}

data class CbtSessionState(
    val test: MockTest,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val markedForReview: Set<Int> = emptySet(),
    val visitedIndices: Set<Int> = setOf(0),
    val remainingSeconds: Int = test.durationMinutes * 60,
    val totalDurationSeconds: Int = test.durationMinutes * 60,
    val submitted: Boolean = false,
    val lastAttemptSummary: ExamAttempt? = null
) {
    fun statusFor(index: Int): QuestionPaletteStatus {
        val answered = selectedAnswers.containsKey(index)
        val marked = markedForReview.contains(index)
        val visited = visitedIndices.contains(index)
        return when {
            answered && marked -> QuestionPaletteStatus.ANSWERED_AND_MARKED
            marked -> QuestionPaletteStatus.MARKED_FOR_REVIEW
            answered -> QuestionPaletteStatus.ANSWERED
            visited -> QuestionPaletteStatus.NOT_ANSWERED
            else -> QuestionPaletteStatus.NOT_VISITED
        }
    }
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
