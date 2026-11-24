package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.CbtQuestion
import com.example.data.model.CbtSessionState
import com.example.data.model.MockTest
import com.example.ui.components.BilingualToggleChip
import com.example.ui.components.CbtQuestionPaletteContent
import com.example.ui.components.CbtQuestionStrip
import com.example.ui.components.ExamTimeProgressIndicator
import com.example.ui.components.ScrollableQuestionDisplay
import com.example.ui.theme.CbtMarkedPurple
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.SaffronGoldBright
import com.example.ui.theme.VijetaApTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CbtExamScreen(
    session: CbtSessionState,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onClearResponse: () -> Unit,
    onMarkForReviewAndNext: () -> Unit,
    onNavigateQuestion: (Int) -> Unit,
    onSubmitExam: () -> Unit,
    onExitExam: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTe = language == AppLanguage.TE
    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var fontScaleLevel by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = true) {
        if (session.submitted) {
            onExitExam()
        } else {
            showExitDialog = true
        }
    }

    if (session.submitted) {
        CbtResultAndSolutionsScreen(
            session = session,
            language = language,
            onToggleLanguage = onToggleLanguage,
            onDone = onExitExam
        )
        return
    }

    val questions = session.test.questions
    val currentIndex = session.currentQuestionIndex.coerceIn(0, questions.lastIndex.coerceAtLeast(0))
    val currentQuestion = questions.getOrNull(currentIndex)
    val selectedOption = session.selectedAnswers[currentIndex]

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val showPersistentSidebar = maxWidth >= 840.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Surface(
                    color = RoyalNavyDark,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(RoyalNavyDark, RoyalNavy)
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = { showExitDialog = true },
                                    modifier = Modifier.testTag("cbt_exit_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = if (isTe) "పరీక్ష నుండి బయటకు వెళ్ళండి" else "Exit exam",
                                        tint = Color.White
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.test.title(language),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isTe) {
                                            "సమాధానం ఇచ్చినవి: ${session.selectedAnswers.size}/${questions.size} • +${session.test.marksPerQuestion} / -${session.test.negativeMarking}"
                                        } else {
                                            "Answered: ${session.selectedAnswers.size}/${questions.size} • +${session.test.marksPerQuestion} / -${session.test.negativeMarking}"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SaffronGoldBright,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BilingualToggleChip(
                                    language = language,
                                    onToggle = onToggleLanguage
                                )
                                if (!showPersistentSidebar) {
                                    IconButton(
                                        onClick = { showPaletteSheet = true },
                                        modifier = Modifier.testTag("cbt_palette_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = if (isTe) "ప్రశ్నల పాలెట్ తెరవండి" else "Open question palette",
                                            tint = SaffronGoldBright
                                        )
                                    }
                                }
                            }
                        }

                        // Remaining-time progress bar & live countdown
                        ExamTimeProgressIndicator(
                            remainingSeconds = session.remainingSeconds,
                            totalDurationSeconds = session.totalDurationSeconds,
                            isTelugu = isTe
                        )
                    }
                }
            },
            bottomBar = {
                if (currentQuestion != null) {
                    CbtExamActionDock(
                        isTelugu = isTe,
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        hasSelection = selectedOption != null,
                        onClearResponse = onClearResponse,
                        onMarkForReviewAndNext = onMarkForReviewAndNext,
                        onNavigatePrevious = { onNavigateQuestion(currentIndex - 1) },
                        onNavigateNext = { onNavigateQuestion(currentIndex + 1) },
                        onRequestSubmit = { showSubmitDialog = true }
                    )
                }
            }
        ) { innerPadding ->
            if (currentQuestion == null) {
                EmptyCbtExamState(
                    isTelugu = isTe,
                    onExitExam = onExitExam,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else if (showPersistentSidebar) {
                // Canonical 2-pane CBT layout for tablets & foldables
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ScrollableQuestionDisplay(
                            questionNumber = currentIndex + 1,
                            totalQuestions = questions.size,
                            question = currentQuestion,
                            language = language,
                            marksPerQuestion = session.test.marksPerQuestion,
                            negativeMarking = session.test.negativeMarking,
                            isMarkedForReview = session.markedForReview.contains(currentIndex),
                            selectedOptionIndex = selectedOption,
                            fontScaleLevel = fontScaleLevel,
                            onCycleFontSize = { fontScaleLevel = (fontScaleLevel + 1) % 3 },
                            onSelectOption = onSelectOption,
                            modifier = Modifier
                                .widthIn(max = 760.dp)
                                .fillMaxSize()
                        )
                    }

                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .width(320.dp)
                            .fillMaxHeight()
                    ) {
                        CbtQuestionPaletteContent(
                            session = session,
                            currentIndex = currentIndex,
                            isTelugu = isTe,
                            onSelectQuestion = onNavigateQuestion,
                            gridHeightDp = 360,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                        )
                    }
                }
            } else {
                // Mobile single-column layout with quick-jump strip + scrollable question & options
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CbtQuestionStrip(
                        session = session,
                        currentIndex = currentIndex,
                        onNavigateQuestion = onNavigateQuestion
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ScrollableQuestionDisplay(
                            questionNumber = currentIndex + 1,
                            totalQuestions = questions.size,
                            question = currentQuestion,
                            language = language,
                            marksPerQuestion = session.test.marksPerQuestion,
                            negativeMarking = session.test.negativeMarking,
                            isMarkedForReview = session.markedForReview.contains(currentIndex),
                            selectedOptionIndex = selectedOption,
                            fontScaleLevel = fontScaleLevel,
                            onCycleFontSize = { fontScaleLevel = (fontScaleLevel + 1) % 3 },
                            onSelectOption = onSelectOption,
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet (compact/medium widths)
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false }
        ) {
            CbtQuestionPaletteContent(
                session = session,
                currentIndex = currentIndex,
                isTelugu = isTe,
                onSelectQuestion = { idx ->
                    onNavigateQuestion(idx)
                    showPaletteSheet = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showSubmitDialog) {
        val answered = session.selectedAnswers.size
        val marked = session.markedForReview.size
        val unanswered = (questions.size - answered).coerceAtLeast(0)
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text(if (isTe) "పరీక్ష సబ్మిట్ చేయాలా?" else "Submit CBT Exam?") },
            text = {
                Text(
                    if (isTe) {
                        "సమాధానం ఇచ్చినవి: $answered • రివ్యూ: $marked • వదిలినవి: $unanswered\nనెగటివ్ మార్కింగ్ (-${session.test.negativeMarking}) తో మీ స్కోర్‌కార్డ్ & వివరణలు తక్షణమే లభిస్తాయి."
                    } else {
                        "Answered: $answered • Marked: $marked • Unanswered: $unanswered\nNegative marking (-${session.test.negativeMarking}) will be applied and step-by-step solutions will unlock immediately."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        onSubmitExam()
                    },
                    modifier = Modifier.testTag("cbt_confirm_submit_button")
                ) {
                    Text(if (isTe) "సబ్మిట్ చేయండి" else "Submit Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text(if (isTe) "రద్దు" else "Continue Exam")
                }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(if (isTe) "పరీక్ష నుండి బయటకు వెళ్లాలా?" else "Exit Active Exam?") },
            text = {
                Text(
                    if (isTe) {
                        "మీరు సబ్మిట్ చేయకుండా వెళ్తే ప్రస్తుత పురోగతి సేవ్ కాదు."
                    } else {
                        "Leaving without submitting will discard your current test session."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onExitExam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (isTe) "బయటకు వెళ్ళు" else "Exit Exam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(if (isTe) "కొనసాగించు" else "Stay")
                }
            }
        )
    }
}

@Composable
private fun CbtExamActionDock(
    isTelugu: Boolean,
    currentIndex: Int,
    totalQuestions: Int,
    hasSelection: Boolean,
    onClearResponse: () -> Unit,
    onMarkForReviewAndNext: () -> Unit,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit,
    onRequestSubmit: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearResponse,
                    enabled = hasSelection,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("cbt_clear_response_button")
                ) {
                    Text(
                        text = if (isTelugu) "క్లియర్" else "Clear",
                        maxLines = 1
                    )
                }
                OutlinedButton(
                    onClick = onMarkForReviewAndNext,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CbtMarkedPurple),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CbtMarkedPurple),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                        .testTag("cbt_mark_review_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTelugu) "రివ్యూ & నెక్స్ట్" else "Mark & Next",
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigatePrevious,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("cbt_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isTelugu) "మునుపటి ప్రశ్న" else "Previous question"
                    )
                }
                if (currentIndex < totalQuestions - 1) {
                    Button(
                        onClick = onNavigateNext,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cbt_save_next_button")
                    ) {
                        Text(if (isTelugu) "సేవ్ & తదుపరి" else "Save & Next")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Button(
                    onClick = onRequestSubmit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldTeal,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(if (currentIndex >= totalQuestions - 1) 1f else 0.75f)
                        .height(48.dp)
                        .testTag("cbt_submit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTelugu) "సబ్మిట్" else "Submit")
                }
            }
        }
    }
}

@Composable
private fun EmptyCbtExamState(
    isTelugu: Boolean,
    onExitExam: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Quiz,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isTelugu) "ఈ టెస్ట్‌లో ప్రశ్నలు అందుబాటులో లేవు" else "No questions available in this test",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExitExam,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Text(if (isTelugu) "వెనక్కి వెళ్ళండి" else "Return to Tests")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CbtExamScreenPreview() {
    VijetaApTheme {
        val sampleTest = MockTest(
            id = "preview-1",
            titleEn = "APPSC Group 2 Prelims Mock",
            titleTe = "APPSC గ్రూప్ 2 ప్రిలిమ్స్ మాక్",
            durationMinutes = 15,
            marksPerQuestion = 1.0,
            negativeMarking = 0.33,
            questions = listOf(
                CbtQuestion(
                    questionEn = "Which Article of the Indian Constitution provides for the establishment of State Public Service Commissions such as APPSC?",
                    questionTe = "APPSC వంటి రాష్ట్ర పబ్లిక్ సర్వీస్ కమిషన్ల ఏర్పాటును భారత రాజ్యాంగంలోని ఏ ఆర్టికల్ నిర్దేశిస్తుంది?",
                    optionsEn = listOf("Article 312", "Article 315", "Article 320", "Article 324"),
                    optionsTe = listOf("ఆర్టికల్ 312", "ఆర్టికల్ 315", "ఆర్టికల్ 320", "ఆర్టికల్ 324"),
                    correctIndex = 1,
                    section = "Indian Polity"
                )
            )
        )
        CbtExamScreen(
            session = CbtSessionState(
                test = sampleTest,
                currentQuestionIndex = 0,
                selectedAnswers = mapOf(0 to 1),
                remainingSeconds = 640,
                totalDurationSeconds = 900
            ),
            language = AppLanguage.EN,
            onToggleLanguage = {},
            onSelectOption = {},
            onClearResponse = {},
            onMarkForReviewAndNext = {},
            onNavigateQuestion = {},
            onSubmitExam = {},
            onExitExam = {}
        )
    }
}
