package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.ExamCategory
import com.example.data.model.MockExamOption
import com.example.data.model.MockExamQuestion
import com.example.data.model.MockExamStructure
import com.example.data.model.ScoringRules
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Dedicated Firestore Repository for Mock Exam Structures, bilingual questions, options,
 * correct answers, and negative marking [ScoringRules].
 */
class MockExamRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before accessing mock exams.")
    }

    /**
     * Real-time stream of published mock exams (aligned 1:1 with `allow list` status == 'published').
     */
    fun observePublishedExams(userId: String): Flow<List<MockExamStructure>> = flow {
        val path = COLLECTION_MOCK_TESTS
        emitAll(
            db.collection(COLLECTION_MOCK_TESTS)
                .whereEqualTo("status", STATUS_PUBLISHED)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents
                        .mapNotNull { doc -> parseExamDocument(doc) }
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = unwrapFirestoreError(error)
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    /**
     * Real-time stream of exams authored by [userId] (includes both drafts and published exams).
     */
    fun observeAuthorExams(userId: String): Flow<List<MockExamStructure>> = flow {
        val path = COLLECTION_MOCK_TESTS
        emitAll(
            db.collection(COLLECTION_MOCK_TESTS)
                .whereEqualTo("authorId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents
                        .mapNotNull { doc -> parseExamDocument(doc) }
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = unwrapFirestoreError(error)
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    /**
     * Fetches a single mock exam structure by document ID.
     */
    suspend fun getExamById(examId: String): Result<MockExamStructure?> {
        val docPath = "$COLLECTION_MOCK_TESTS/$examId"
        return try {
            requireUserId()
            require(examId.isNotBlank()) { "Exam ID must not be blank." }
            val snapshot = db.collection(COLLECTION_MOCK_TESTS).document(examId).get().await()
            val parsed = if (snapshot.exists()) parseExamDocument(snapshot) else null
            Result.success(parsed)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docPath)
            Result.failure(e)
        }
    }

    /**
     * Creates a new mock exam document with structured questions, options, correct answers,
     * and [ScoringRules]. Excludes `@DocumentId` `"id"` from the write payload.
     */
    suspend fun createExam(exam: MockExamStructure): Result<String> {
        var docPath = COLLECTION_MOCK_TESTS
        return try {
            val uid = requireUserId()
            validateExamStructure(exam)

            val docRef = db.collection(COLLECTION_MOCK_TESTS).document()
            docPath = docRef.path

            val cleanTitleEn = exam.titleEn.trim().take(140)
            val cleanTitleTe = exam.titleTe.trim().take(200).ifBlank { cleanTitleEn }
            val rules = exam.scoringRules
            val normalizedStatus = if (exam.status == STATUS_DRAFT) STATUS_DRAFT else STATUS_PUBLISHED

            val payload = mapOf(
                "authorId" to uid,
                "titleEn" to cleanTitleEn,
                "titleTe" to cleanTitleTe,
                "descriptionEn" to exam.descriptionEn.trim().take(500),
                "descriptionTe" to exam.descriptionTe.trim().take(600),
                "examCategory" to ExamCategory.fromCode(exam.examCategory).code,
                "durationMinutes" to exam.durationMinutes.coerceIn(1, 240),
                "marksPerQuestion" to rules.marksPerCorrect.coerceIn(0.5, 10.0),
                "negativeMarking" to rules.effectiveNegativePenalty(),
                "scoringRules" to rules.toMap(),
                "freeDemo" to exam.freeDemo,
                "status" to normalizedStatus,
                "questions" to exam.questions.take(50).mapIndexed { index, q ->
                    q.copy(orderIndex = index).toFirestoreMap()
                },
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    /**
     * Partial update of an existing mock exam preserving immutable `authorId` and `createdAt`.
     */
    suspend fun updateExam(examId: String, exam: MockExamStructure): Result<Unit> {
        val docPath = "$COLLECTION_MOCK_TESTS/$examId"
        return try {
            requireUserId()
            require(examId.isNotBlank()) { "Exam ID must not be blank for update." }
            validateExamStructure(exam)

            val cleanTitleEn = exam.titleEn.trim().take(140)
            val cleanTitleTe = exam.titleTe.trim().take(200).ifBlank { cleanTitleEn }
            val rules = exam.scoringRules
            val normalizedStatus = if (exam.status == STATUS_DRAFT) STATUS_DRAFT else STATUS_PUBLISHED

            val updates = mapOf(
                "titleEn" to cleanTitleEn,
                "titleTe" to cleanTitleTe,
                "descriptionEn" to exam.descriptionEn.trim().take(500),
                "descriptionTe" to exam.descriptionTe.trim().take(600),
                "examCategory" to ExamCategory.fromCode(exam.examCategory).code,
                "durationMinutes" to exam.durationMinutes.coerceIn(1, 240),
                "marksPerQuestion" to rules.marksPerCorrect.coerceIn(0.5, 10.0),
                "negativeMarking" to rules.effectiveNegativePenalty(),
                "scoringRules" to rules.toMap(),
                "freeDemo" to exam.freeDemo,
                "status" to normalizedStatus,
                "questions" to exam.questions.take(50).mapIndexed { index, q ->
                    q.copy(orderIndex = index).toFirestoreMap()
                },
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection(COLLECTION_MOCK_TESTS).document(examId).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    /**
     * Targeted partial update for negative marking and scoring rules on an existing mock exam.
     */
    suspend fun updateScoringRules(examId: String, scoringRules: ScoringRules): Result<Unit> {
        val docPath = "$COLLECTION_MOCK_TESTS/$examId"
        return try {
            requireUserId()
            require(examId.isNotBlank()) { "Exam ID must not be blank." }
            validateScoringRules(scoringRules)

            val updates = mapOf(
                "marksPerQuestion" to scoringRules.marksPerCorrect.coerceIn(0.5, 10.0),
                "negativeMarking" to scoringRules.effectiveNegativePenalty(),
                "scoringRules" to scoringRules.toMap(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection(COLLECTION_MOCK_TESTS).document(examId).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    /**
     * Deletes a mock exam document (permitted for owner or admin by security rules).
     */
    suspend fun deleteExam(examId: String): Result<Unit> {
        val docPath = "$COLLECTION_MOCK_TESTS/$examId"
        return try {
            requireUserId()
            require(examId.isNotBlank()) { "Exam ID must not be blank." }
            db.collection(COLLECTION_MOCK_TESTS).document(examId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    private fun validateExamStructure(exam: MockExamStructure) {
        require(exam.titleEn.trim().length in 3..140) { "English title must be 3–140 characters." }
        require(exam.durationMinutes in 1..240) { "Duration must be between 1 and 240 minutes." }
        require(exam.questions.isNotEmpty() && exam.questions.size <= 50) {
            "Mock exam must contain between 1 and 50 questions."
        }
        validateScoringRules(exam.scoringRules)
        exam.questions.forEachIndexed { idx, q ->
            require(q.questionEn.isNotBlank()) { "Question #${idx + 1} text cannot be blank." }
            require(q.options.size == 4 && q.options.all { it.textEn.isNotBlank() }) {
                "Question #${idx + 1} must have 4 non-empty options."
            }
            require(q.correctOptionIndex in 0..3) {
                "Question #${idx + 1} correct option index must be between 0 and 3."
            }
        }
    }

    private fun validateScoringRules(rules: ScoringRules) {
        require(rules.marksPerCorrect in 0.5..10.0) { "Marks per correct answer must be 0.5–10.0." }
        require(rules.negativeMarkPerWrong in 0.0..5.0) { "Negative mark per wrong answer must be 0.0–5.0." }
        require(rules.unansweredPenalty in 0.0..2.0) { "Unanswered penalty must be 0.0–2.0." }
        require(rules.passingPercentage in 0.0..100.0) { "Passing percentage must be 0–100." }
    }

    private fun parseExamDocument(doc: DocumentSnapshot): MockExamStructure? {
        return try {
            val data = doc.data ?: return null
            val fallbackMarks = ((data["marksPerQuestion"] as? Number)?.toDouble() ?: 1.0).coerceIn(0.5, 10.0)
            val fallbackNeg = ((data["negativeMarking"] as? Number)?.toDouble() ?: 0.33).coerceIn(0.0, 5.0)
            val scoringRules = ScoringRules.fromMap(
                map = data["scoringRules"] as? Map<*, *>,
                fallbackMarks = fallbackMarks,
                fallbackNeg = fallbackNeg
            )

            val rawQuestions = data["questions"] as? List<*> ?: emptyList<Any>()
            val parsedQuestions = rawQuestions.mapIndexedNotNull { idx, entry ->
                val map = entry as? Map<*, *> ?: return@mapIndexedNotNull null
                parseQuestionMap(idx, map)
            }
            if (parsedQuestions.isEmpty()) return null

            MockExamStructure(
                id = doc.id,
                authorId = data["authorId"]?.toString().orEmpty(),
                titleEn = data["titleEn"]?.toString().orEmpty(),
                titleTe = data["titleTe"]?.toString().orEmpty(),
                descriptionEn = data["descriptionEn"]?.toString().orEmpty(),
                descriptionTe = data["descriptionTe"]?.toString().orEmpty(),
                examCategory = data["examCategory"]?.toString() ?: ExamCategory.APPSC_GROUP2.code,
                durationMinutes = ((data["durationMinutes"] as? Number)?.toInt() ?: 15).coerceIn(1, 240),
                scoringRules = scoringRules,
                freeDemo = (data["freeDemo"] as? Boolean) ?: true,
                status = data["status"]?.toString() ?: STATUS_PUBLISHED,
                questions = parsedQuestions,
                createdAt = doc.getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
                updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseQuestionMap(defaultIndex: Int, map: Map<*, *>): MockExamQuestion? {
        val optionKeys = listOf("A", "B", "C", "D")
        val structuredRaw = map["options"] as? List<*>
        val options: List<MockExamOption> = if (!structuredRaw.isNullOrEmpty()) {
            structuredRaw.mapIndexedNotNull { idx, rawOpt ->
                val optMap = rawOpt as? Map<*, *> ?: return@mapIndexedNotNull null
                val textEn = optMap["textEn"]?.toString().orEmpty()
                val textTe = optMap["textTe"]?.toString().orEmpty().ifBlank { textEn }
                MockExamOption(
                    key = optMap["key"]?.toString().orEmpty().ifBlank { optionKeys.getOrElse(idx) { "${idx + 1}" } },
                    textEn = textEn,
                    textTe = textTe
                )
            }
        } else {
            val optEn = (map["optionsEn"] as? List<*>)?.map { it?.toString().orEmpty() } ?: emptyList()
            val optTe = (map["optionsTe"] as? List<*>)?.map { it?.toString().orEmpty() } ?: emptyList()
            optEn.take(4).mapIndexed { idx, enText ->
                MockExamOption(
                    key = optionKeys.getOrElse(idx) { "${idx + 1}" },
                    textEn = enText,
                    textTe = optTe.getOrElse(idx) { enText }.ifBlank { enText }
                )
            }
        }
        if (options.size < 4) return null

        val correctIdx = ((map["correctOptionIndex"] as? Number) ?: (map["correctIndex"] as? Number))
            ?.toInt()?.coerceIn(0, 3) ?: 0

        return MockExamQuestion(
            orderIndex = (map["orderIndex"] as? Number)?.toInt() ?: defaultIndex,
            section = map["section"]?.toString().orEmpty().ifBlank { "General Studies" },
            questionEn = map["questionEn"]?.toString().orEmpty(),
            questionTe = map["questionTe"]?.toString().orEmpty(),
            options = options.take(4),
            correctOptionIndex = correctIdx,
            explanationEn = map["explanationEn"]?.toString().orEmpty(),
            explanationTe = map["explanationTe"]?.toString().orEmpty(),
            difficulty = map["difficulty"]?.toString().orEmpty().ifBlank { "MEDIUM" },
            marksOverride = (map["marksOverride"] as? Number)?.toDouble()
        )
    }

    private fun unwrapFirestoreError(error: Throwable): FirebaseFirestoreException? =
        generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()

    companion object {
        const val COLLECTION_MOCK_TESTS = "mock_tests"
        const val STATUS_PUBLISHED = "published"
        const val STATUS_DRAFT = "draft"
    }
}
