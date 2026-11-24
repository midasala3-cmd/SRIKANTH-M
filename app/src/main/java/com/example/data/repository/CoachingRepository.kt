package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.AppLanguage
import com.example.data.model.CbtQuestion
import com.example.data.model.ExamAttempt
import com.example.data.model.ExamCategory
import com.example.data.model.MockTest
import com.example.data.model.StudyBlog
import com.example.data.model.UserProfile
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

class CoachingRepository(
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
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureUserProfile(userId: String, displayName: String): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user changed; refusing cross-user write." }
            val docRef = db.collection("users").document(userId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val cleanName = displayName.trim().take(80).ifBlank { "AP Aspirant" }
                val payload = mapOf(
                    "userId" to userId,
                    "displayName" to cleanName,
                    "preferredLanguage" to AppLanguage.EN.code,
                    "targetExam" to ExamCategory.APPSC_GROUP2.code,
                    "enrolledCourseIds" to emptyList<String>(),
                    "bookmarkedBlogIds" to emptyList<String>(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateUserPreferences(
        userId: String,
        preferredLanguage: String? = null,
        targetExam: String? = null,
        enrolledCourseIds: List<String>? = null,
        bookmarkedBlogIds: List<String>? = null
    ): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user changed; refusing cross-user write." }
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (preferredLanguage != null) updates["preferredLanguage"] = preferredLanguage
            if (targetExam != null) updates["targetExam"] = targetExam
            if (enrolledCourseIds != null) updates["enrolledCourseIds"] = enrolledCourseIds.take(20)
            if (bookmarkedBlogIds != null) updates["bookmarkedBlogIds"] = bookmarkedBlogIds.take(50)
            db.collection("users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    fun observeExamAttempts(userId: String): Flow<List<ExamAttempt>> = flow {
        val path = "users/$userId/attempts"
        emitAll(
            db.collection("users").document(userId).collection("attempts")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(ExamAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveExamAttempt(userId: String, attempt: ExamAttempt): Result<Unit> {
        var docPath = "users/$userId/attempts"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user changed; refusing cross-user write." }
            val docRef = db.collection("users").document(userId).collection("attempts").document()
            docPath = docRef.path
            val payload = mapOf(
                "userId" to userId,
                "testId" to attempt.testId.take(128).ifBlank { "custom_test" },
                "testTitleEn" to attempt.testTitleEn.take(140).ifBlank { "CBT Mock Test" },
                "testTitleTe" to attempt.testTitleTe.take(200).ifBlank { attempt.testTitleEn.take(140).ifBlank { "మాక్ టెస్ట్" } },
                "examCategory" to ExamCategory.fromCode(attempt.examCategory).code,
                "totalQuestions" to attempt.totalQuestions.coerceIn(1, 200),
                "correctCount" to attempt.correctCount.coerceIn(0, attempt.totalQuestions),
                "wrongCount" to attempt.wrongCount.coerceIn(0, attempt.totalQuestions),
                "unansweredCount" to attempt.unansweredCount.coerceIn(0, attempt.totalQuestions),
                "score" to attempt.score.coerceIn(-200.0, 1000.0),
                "maxScore" to attempt.maxScore.coerceIn(1.0, 1000.0),
                "negativeMarking" to attempt.negativeMarking.coerceIn(0.0, 5.0),
                "timeTakenSeconds" to attempt.timeTakenSeconds.coerceIn(0, 36000),
                "accuracyPercent" to attempt.accuracyPercent.coerceIn(0.0, 100.0),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    fun observePublishedMockTests(userId: String): Flow<List<MockTest>> = flow {
        val path = "mock_tests"
        emitAll(
            db.collection("mock_tests")
                .whereEqualTo("status", "published")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc -> parseMockTestDocument(doc) }
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    private fun parseMockTestDocument(doc: DocumentSnapshot): MockTest? {
        return try {
            val data = doc.data ?: return null
            val rawQuestions = data["questions"] as? List<*> ?: emptyList<Any>()
            val parsedQuestions = rawQuestions.mapNotNull { entry ->
                val map = entry as? Map<*, *> ?: return@mapNotNull null
                val optEn = (map["optionsEn"] as? List<*>)?.map { it?.toString().orEmpty() } ?: emptyList()
                val optTe = (map["optionsTe"] as? List<*>)?.map { it?.toString().orEmpty() } ?: emptyList()
                if (optEn.size < 4) return@mapNotNull null
                CbtQuestion(
                    questionEn = map["questionEn"]?.toString().orEmpty(),
                    questionTe = map["questionTe"]?.toString().orEmpty(),
                    optionsEn = optEn.take(4),
                    optionsTe = if (optTe.size >= 4) optTe.take(4) else optEn.take(4),
                    correctIndex = ((map["correctIndex"] as? Number)?.toInt() ?: 0).coerceIn(0, 3),
                    explanationEn = map["explanationEn"]?.toString().orEmpty(),
                    explanationTe = map["explanationTe"]?.toString().orEmpty(),
                    section = map["section"]?.toString().orEmpty().ifBlank { "General Studies" }
                )
            }
            if (parsedQuestions.isEmpty()) return null
            MockTest(
                id = doc.id,
                authorId = data["authorId"]?.toString().orEmpty(),
                titleEn = data["titleEn"]?.toString().orEmpty(),
                titleTe = data["titleTe"]?.toString().orEmpty(),
                descriptionEn = data["descriptionEn"]?.toString().orEmpty(),
                descriptionTe = data["descriptionTe"]?.toString().orEmpty(),
                examCategory = data["examCategory"]?.toString() ?: ExamCategory.APPSC_GROUP2.code,
                durationMinutes = ((data["durationMinutes"] as? Number)?.toInt() ?: 15).coerceIn(1, 240),
                marksPerQuestion = ((data["marksPerQuestion"] as? Number)?.toDouble() ?: 1.0).coerceIn(0.5, 10.0),
                negativeMarking = ((data["negativeMarking"] as? Number)?.toDouble() ?: 0.33).coerceIn(0.0, 5.0),
                freeDemo = (data["freeDemo"] as? Boolean) ?: true,
                status = data["status"]?.toString() ?: "published",
                questions = parsedQuestions,
                createdAt = doc.getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
                updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
                bundledStarter = false
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun publishMockTest(
        titleEn: String,
        titleTe: String,
        descriptionEn: String,
        descriptionTe: String,
        examCategory: ExamCategory,
        durationMinutes: Int,
        marksPerQuestion: Double,
        negativeMarking: Double,
        freeDemo: Boolean,
        questions: List<CbtQuestion>
    ): Result<String> {
        var docPath = "mock_tests"
        return try {
            val uid = requireUserId()
            require(titleEn.trim().length in 3..140) { "English title must be 3–140 characters." }
            require(questions.isNotEmpty() && questions.size <= 50) { "Add between 1 and 50 questions." }

            val docRef = db.collection("mock_tests").document()
            docPath = docRef.path
            val cleanTitleEn = titleEn.trim().take(140)
            val cleanTitleTe = titleTe.trim().take(200).ifBlank { cleanTitleEn }
            val payload = mapOf(
                "authorId" to uid,
                "titleEn" to cleanTitleEn,
                "titleTe" to cleanTitleTe,
                "descriptionEn" to descriptionEn.trim().take(500),
                "descriptionTe" to descriptionTe.trim().take(600),
                "examCategory" to examCategory.code,
                "durationMinutes" to durationMinutes.coerceIn(1, 240),
                "marksPerQuestion" to marksPerQuestion.coerceIn(0.5, 10.0),
                "negativeMarking" to negativeMarking.coerceIn(0.0, 5.0),
                "freeDemo" to freeDemo,
                "status" to "published",
                "questions" to questions.take(50).map { it.toMap() },
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

    suspend fun deleteMockTest(testId: String): Result<Unit> {
        val docPath = "mock_tests/$testId"
        return try {
            requireUserId()
            require(testId.isNotBlank()) { "Test ID cannot be blank" }
            db.collection("mock_tests").document(testId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    fun observePublishedBlogs(userId: String): Flow<List<StudyBlog>> = flow {
        val path = "study_blogs"
        emitAll(
            db.collection("study_blogs")
                .whereEqualTo("status", "published")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(StudyBlog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun publishStudyBlog(
        titleEn: String,
        titleTe: String,
        slug: String,
        metaDescription: String,
        seoKeywords: List<String>,
        category: String,
        summaryEn: String,
        summaryTe: String,
        contentEn: String,
        contentTe: String,
        readTimeMinutes: Int
    ): Result<String> {
        var docPath = "study_blogs"
        return try {
            val uid = requireUserId()
            val cleanTitleEn = titleEn.trim().take(160)
            val cleanTitleTe = titleTe.trim().take(220).ifBlank { cleanTitleEn }
            val cleanSlug = slug.lowercase()
                .replace(Regex("[^a-z0-9\\-]"), "-")
                .replace(Regex("-+"), "-")
                .trim('-')
                .take(120)
            val cleanMeta = metaDescription.trim().take(220)
            val cleanSummaryEn = summaryEn.trim().take(400)
            val cleanSummaryTe = summaryTe.trim().take(500).ifBlank { cleanSummaryEn }
            val cleanContentEn = contentEn.trim().take(15000)
            val cleanContentTe = contentTe.trim().take(15000).ifBlank { cleanContentEn }

            require(cleanTitleEn.length >= 5) { "English headline must be at least 5 characters." }
            require(cleanSlug.length >= 3) { "SEO slug must be at least 3 characters (a-z, 0-9, -)." }
            require(cleanMeta.length >= 10) { "SEO meta description must be at least 10 characters." }
            require(cleanSummaryEn.length >= 10) { "English summary must be at least 10 characters." }
            require(cleanContentEn.length >= 20) { "Article content must be at least 20 characters." }

            val docRef = db.collection("study_blogs").document()
            docPath = docRef.path
            val payload = mapOf(
                "authorId" to uid,
                "titleEn" to cleanTitleEn,
                "titleTe" to cleanTitleTe,
                "slug" to cleanSlug,
                "metaDescription" to cleanMeta,
                "seoKeywords" to seoKeywords.map { it.trim() }.filter { it.isNotBlank() }.take(10),
                "category" to category,
                "summaryEn" to cleanSummaryEn,
                "summaryTe" to cleanSummaryTe,
                "contentEn" to cleanContentEn,
                "contentTe" to cleanContentTe,
                "readTimeMinutes" to readTimeMinutes.coerceIn(1, 120),
                "status" to "published",
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

    suspend fun deleteStudyBlog(blogId: String): Result<Unit> {
        val docPath = "study_blogs/$blogId"
        return try {
            requireUserId()
            require(blogId.isNotBlank()) { "Blog ID cannot be blank" }
            db.collection("study_blogs").document(blogId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
