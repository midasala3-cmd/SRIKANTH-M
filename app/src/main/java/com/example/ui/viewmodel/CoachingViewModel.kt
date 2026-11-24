package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppLanguage
import com.example.data.model.BlogCategory
import com.example.data.model.CbtQuestion
import com.example.data.model.CbtSessionState
import com.example.data.model.CoachingCourse
import com.example.data.model.ExamAttempt
import com.example.data.model.ExamCategory
import com.example.data.model.MockExamQuestion
import com.example.data.model.MockExamStructure
import com.example.data.model.MockTest
import com.example.data.model.ScoringRules
import com.example.data.model.StudyBlog
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.data.repository.CoachingRepository
import com.example.data.repository.MockExamRepository
import com.example.data.seed.StarterCoachingContent
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoachingViewModel(
    private val repository: CoachingRepository,
    private val mockExamRepository: MockExamRepository,
    val currentUserId: String,
    val currentUserName: String,
    val currentUserEmail: String
) : ViewModel() {

    val courses: List<CoachingCourse> = StarterCoachingContent.flagshipCourses

    private val _language = MutableStateFlow(AppLanguage.EN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _selectedTargetExam = MutableStateFlow(ExamCategory.APPSC_GROUP2)
    val selectedTargetExam: StateFlow<ExamCategory> = _selectedTargetExam.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    private val _activeCbtSession = MutableStateFlow<CbtSessionState?>(null)
    val activeCbtSession: StateFlow<CbtSessionState?> = _activeCbtSession.asStateFlow()

    private val _selectedBlog = MutableStateFlow<StudyBlog?>(null)
    val selectedBlog: StateFlow<StudyBlog?> = _selectedBlog.asStateFlow()

    private var cbtTimerJob: Job? = null

    val userProfileState: StateFlow<UiState<UserProfile?>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile?>> { profile ->
            if (profile != null) {
                _language.value = AppLanguage.fromCode(profile.preferredLanguage)
                _selectedTargetExam.value = ExamCategory.fromCode(profile.targetExam)
            }
            UiState.Success(profile)
        }
        .catch { error ->
            val firestoreError = unwrapFirestoreException(error)
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing profile", firestoreError ?: error)
            emit(UiState.Error((firestoreError ?: error).message ?: "Failed to load profile"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val attemptsState: StateFlow<UiState<List<ExamAttempt>>> = repository.observeExamAttempts(currentUserId)
        .map<List<ExamAttempt>, UiState<List<ExamAttempt>>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = unwrapFirestoreException(error)
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing exam attempts", firestoreError ?: error)
            emit(UiState.Error((firestoreError ?: error).message ?: "Failed to load exam attempts"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val mockTestsState: StateFlow<UiState<List<MockTest>>> = mockExamRepository.observePublishedExams(currentUserId)
        .map<List<MockExamStructure>, UiState<List<MockTest>>> { cloudStructures ->
            val mappedCloud = cloudStructures.map { it.toMockTest(bundledStarter = false) }
            UiState.Success(mappedCloud + StarterCoachingContent.starterMockTests)
        }
        .catch { error ->
            val firestoreError = unwrapFirestoreException(error)
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing mock tests", firestoreError ?: error)
            emit(UiState.Success(StarterCoachingContent.starterMockTests))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            UiState.Success(StarterCoachingContent.starterMockTests)
        )

    val studyBlogsState: StateFlow<UiState<List<StudyBlog>>> = repository.observePublishedBlogs(currentUserId)
        .map<List<StudyBlog>, UiState<List<StudyBlog>>> { cloudBlogs ->
            UiState.Success(cloudBlogs + StarterCoachingContent.starterBlogs)
        }
        .catch { error ->
            val firestoreError = unwrapFirestoreException(error)
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing study blogs", firestoreError ?: error)
            emit(UiState.Success(StarterCoachingContent.starterBlogs))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            UiState.Success(StarterCoachingContent.starterBlogs)
        )

    init {
        viewModelScope.launch {
            repository.ensureUserProfile(currentUserId, currentUserName)
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun toggleLanguage() {
        val next = if (_language.value == AppLanguage.EN) AppLanguage.TE else AppLanguage.EN
        _language.value = next
        viewModelScope.launch {
            repository.updateUserPreferences(currentUserId, preferredLanguage = next.code)
        }
    }

    fun selectTargetExam(exam: ExamCategory) {
        _selectedTargetExam.value = exam
        viewModelScope.launch {
            repository.updateUserPreferences(currentUserId, targetExam = exam.code)
        }
    }

    fun toggleCourseEnrollment(courseId: String) {
        val current = (userProfileState.value as? UiState.Success)?.data?.enrolledCourseIds ?: emptyList()
        val updated = if (current.contains(courseId)) current - courseId else current + courseId
        val enrolling = !current.contains(courseId)
        viewModelScope.launch {
            val res = repository.updateUserPreferences(currentUserId, enrolledCourseIds = updated)
            if (res.isSuccess) {
                _snackbarMessage.value = if (enrolling) {
                    if (_language.value == AppLanguage.TE) "కోర్సులో విజయవంతంగా చేరారు!" else "Enrolled in course successfully!"
                } else {
                    if (_language.value == AppLanguage.TE) "కోర్సు జాబితా నుండి తొలగించబడింది" else "Removed from enrolled courses"
                }
            } else {
                _snackbarMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to update enrollment"
            }
        }
    }

    fun toggleBlogBookmark(blogId: String) {
        val current = (userProfileState.value as? UiState.Success)?.data?.bookmarkedBlogIds ?: emptyList()
        val updated = if (current.contains(blogId)) current - blogId else current + blogId
        viewModelScope.launch {
            val res = repository.updateUserPreferences(currentUserId, bookmarkedBlogIds = updated)
            if (res.isFailure) {
                _snackbarMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Unable to update bookmark"
            }
        }
    }

    fun openBlog(blog: StudyBlog) {
        _selectedBlog.value = blog
    }

    fun closeBlog() {
        _selectedBlog.value = null
    }

    // --- CBT EXAM PORTAL ENGINE ---

    fun startCbtExam(test: MockTest) {
        cbtTimerJob?.cancel()
        val totalSeconds = (test.durationMinutes.coerceAtLeast(1)) * 60
        _activeCbtSession.value = CbtSessionState(
            test = test,
            currentQuestionIndex = 0,
            selectedAnswers = emptyMap(),
            markedForReview = emptySet(),
            visitedIndices = setOf(0),
            remainingSeconds = totalSeconds,
            totalDurationSeconds = totalSeconds,
            submitted = false
        )
        cbtTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val session = _activeCbtSession.value ?: break
                if (session.submitted) break
                val nextRemaining = session.remainingSeconds - 1
                if (nextRemaining <= 0) {
                    _activeCbtSession.update { it?.copy(remainingSeconds = 0) }
                    submitCbtExam(autoSubmitted = true)
                    break
                } else {
                    _activeCbtSession.update { it?.copy(remainingSeconds = nextRemaining) }
                }
            }
        }
    }

    fun selectCbtOption(optionIndex: Int) {
        _activeCbtSession.update { session ->
            if (session == null || session.submitted) return@update session
            val updatedAnswers = session.selectedAnswers.toMutableMap()
            updatedAnswers[session.currentQuestionIndex] = optionIndex
            session.copy(selectedAnswers = updatedAnswers)
        }
    }

    fun clearCurrentCbtResponse() {
        _activeCbtSession.update { session ->
            if (session == null || session.submitted) return@update session
            val updatedAnswers = session.selectedAnswers.toMutableMap()
            updatedAnswers.remove(session.currentQuestionIndex)
            session.copy(selectedAnswers = updatedAnswers)
        }
    }

    fun toggleMarkForReviewAndNext() {
        _activeCbtSession.update { session ->
            if (session == null || session.submitted) return@update session
            val idx = session.currentQuestionIndex
            val updatedMarked = session.markedForReview.toMutableSet()
            if (updatedMarked.contains(idx)) updatedMarked.remove(idx) else updatedMarked.add(idx)
            val nextIdx = if (idx < session.test.questions.lastIndex) idx + 1 else idx
            session.copy(
                markedForReview = updatedMarked,
                currentQuestionIndex = nextIdx,
                visitedIndices = session.visitedIndices + nextIdx
            )
        }
    }

    fun navigateToCbtQuestion(index: Int) {
        _activeCbtSession.update { session ->
            if (session == null) return@update null
            val safeIndex = index.coerceIn(0, session.test.questions.lastIndex.coerceAtLeast(0))
            session.copy(
                currentQuestionIndex = safeIndex,
                visitedIndices = session.visitedIndices + safeIndex
            )
        }
    }

    fun submitCbtExam(autoSubmitted: Boolean = false) {
        cbtTimerJob?.cancel()
        val session = _activeCbtSession.value ?: return
        if (session.submitted) return

        val scoringRules = ScoringRules(
            marksPerCorrect = session.test.marksPerQuestion,
            negativeMarkPerWrong = session.test.negativeMarking,
            negativeMarkingEnabled = session.test.negativeMarking > 0.0
        )
        val structuredQuestions = session.test.questions.mapIndexed { idx, q ->
            MockExamQuestion.fromCbtQuestion(idx, q)
        }
        val breakdown = scoringRules.evaluate(structuredQuestions, session.selectedAnswers)
        val timeTaken = (session.totalDurationSeconds - session.remainingSeconds).coerceAtLeast(1)

        val attempt = ExamAttempt(
            userId = currentUserId,
            testId = session.test.id,
            testTitleEn = session.test.titleEn,
            testTitleTe = session.test.titleTe,
            examCategory = session.test.examCategory,
            totalQuestions = breakdown.totalQuestions,
            correctCount = breakdown.correctCount,
            wrongCount = breakdown.wrongCount,
            unansweredCount = breakdown.unansweredCount,
            score = breakdown.netScore,
            maxScore = breakdown.maxScore,
            negativeMarking = scoringRules.effectiveNegativePenalty(),
            timeTakenSeconds = timeTaken,
            accuracyPercent = breakdown.accuracyPercent
        )

        _activeCbtSession.value = session.copy(
            submitted = true,
            lastAttemptSummary = attempt
        )

        viewModelScope.launch {
            val res = repository.saveExamAttempt(currentUserId, attempt)
            if (res.isSuccess) {
                _snackbarMessage.value = if (autoSubmitted) {
                    if (_language.value == AppLanguage.TE) "సమయం ముగిసింది! పరీక్ష ఆటో-సబ్మిట్ అయింది." else "Time's up! Exam auto-submitted & scorecard saved."
                } else {
                    if (_language.value == AppLanguage.TE) "స్కోర్‌కార్డ్ క్లౌడ్‌లో సేవ్ చేయబడింది!" else "Exam submitted & scorecard saved to cloud!"
                }
            } else {
                _snackbarMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Submitted locally (sync failed)"
            }
        }
    }

    fun exitCbtExam() {
        cbtTimerJob?.cancel()
        _activeCbtSession.value = null
    }

    // --- ADMIN CMS OPERATIONS ---

    fun publishNewMockTest(
        titleEn: String,
        titleTe: String,
        descriptionEn: String,
        descriptionTe: String,
        examCategory: ExamCategory,
        durationMinutes: Int,
        marksPerQuestion: Double,
        negativeMarking: Double,
        freeDemo: Boolean,
        questions: List<CbtQuestion>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isPublishing.value = true
            val examStructure = MockExamStructure(
                authorId = currentUserId,
                titleEn = titleEn,
                titleTe = titleTe,
                descriptionEn = descriptionEn,
                descriptionTe = descriptionTe,
                examCategory = examCategory.code,
                durationMinutes = durationMinutes,
                scoringRules = ScoringRules(
                    marksPerCorrect = marksPerQuestion,
                    negativeMarkPerWrong = negativeMarking,
                    unansweredPenalty = 0.0,
                    negativeMarkingEnabled = negativeMarking > 0.0,
                    passingPercentage = 35.0
                ),
                freeDemo = freeDemo,
                status = MockExamRepository.STATUS_PUBLISHED,
                questions = questions.mapIndexed { idx, q -> MockExamQuestion.fromCbtQuestion(idx, q) }
            )
            val result = mockExamRepository.createExam(examStructure)
            _isPublishing.value = false
            if (result.isSuccess) {
                _snackbarMessage.value = if (_language.value == AppLanguage.TE) {
                    "కొత్త మాక్ టెస్ట్ CBT పోర్టల్‌లో ప్రచురించబడింది!"
                } else {
                    "Mock test published live to the CBT Portal!"
                }
                onSuccess()
            } else {
                _snackbarMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to publish mock test"
            }
        }
    }

    fun deleteUploadedTest(testId: String) {
        viewModelScope.launch {
            val res = mockExamRepository.deleteExam(testId)
            _snackbarMessage.value = if (res.isSuccess) "Mock test deleted." else (res.exceptionOrNull()?.localizedMessage ?: "Delete failed")
        }
    }

    fun publishNewStudyBlog(
        titleEn: String,
        titleTe: String,
        slug: String,
        metaDescription: String,
        keywordsRaw: String,
        category: BlogCategory,
        summaryEn: String,
        summaryTe: String,
        contentEn: String,
        contentTe: String,
        readTimeMinutes: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isPublishing.value = true
            val keywords = keywordsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }.take(10)
            val result = repository.publishStudyBlog(
                titleEn = titleEn,
                titleTe = titleTe,
                slug = slug,
                metaDescription = metaDescription,
                seoKeywords = keywords,
                category = category.code,
                summaryEn = summaryEn,
                summaryTe = summaryTe,
                contentEn = contentEn,
                contentTe = contentTe,
                readTimeMinutes = readTimeMinutes
            )
            _isPublishing.value = false
            if (result.isSuccess) {
                _snackbarMessage.value = if (_language.value == AppLanguage.TE) {
                    "SEO స్టడీ బ్లాగ్ విజయవంతంగా ప్రచురించబడింది!"
                } else {
                    "SEO study blog published live!"
                }
                onSuccess()
            } else {
                _snackbarMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to publish blog"
            }
        }
    }

    fun deleteUploadedBlog(blogId: String) {
        viewModelScope.launch {
            val res = repository.deleteStudyBlog(blogId)
            _snackbarMessage.value = if (res.isSuccess) "Study blog deleted." else (res.exceptionOrNull()?.localizedMessage ?: "Delete failed")
        }
    }

    private fun unwrapFirestoreException(error: Throwable): FirebaseFirestoreException? =
        generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()

    override fun onCleared() {
        super.onCleared()
        cbtTimerJob?.cancel()
    }

    private companion object {
        const val TAG = "CoachingVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
