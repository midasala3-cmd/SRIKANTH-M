package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.ExamAttempt
import com.example.data.model.ExamCategory
import com.example.data.model.MockTest
import com.example.ui.components.SectionHeading
import com.example.ui.components.formatTimerSeconds
import com.example.ui.theme.CbtAnsweredGreen
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.SaffronGoldBright

@Composable
fun TestsHubScreen(
    language: AppLanguage,
    tests: List<MockTest>,
    attempts: List<ExamAttempt>,
    onStartTest: (MockTest) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTe = language == AppLanguage.TE
    var selectedCategory by remember { mutableStateOf<ExamCategory?>(null) }

    val filteredTests = remember(tests, selectedCategory) {
        if (selectedCategory == null) tests else tests.filter { it.examCategory == selectedCategory!!.code }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CBT Hero Header
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_cbt_banner_1795427366176),
                            contentDescription = "CBT Exam Portal Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            RoyalNavyDark.copy(alpha = 0.92f),
                                            RoyalNavyDark.copy(alpha = 0.45f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SaffronGoldBright
                            ) {
                                Text(
                                    text = if (isTe) "CBT ఎగ్జామ్ పోర్టల్" else "INTERACTIVE CBT PORTAL",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalNavyDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = if (isTe) "టైమర్ • క్వశ్చన్ పాలెట్ • నెగటివ్ మార్కింగ్" else "Timer • Palette • Negative Marking",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = if (isTe) {
                                    "పరీక్ష మధ్యలో ఎప్పుడైనా EN / తెలుగు భాష మార్చుకోండి"
                                } else {
                                    "Switch EN / తెలుగు mid-exam & review step-by-step solutions"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text(if (isTe) "అన్నీ (${tests.size})" else "All (${tests.size})") },
                            modifier = Modifier.testTag("filter_tests_all")
                        )
                    }
                    items(ExamCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label(language)) },
                            modifier = Modifier.testTag("filter_tests_${cat.code}")
                        )
                    }
                }
            }

            // Test List
            if (filteredTests.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isTe) "ఈ విభాగంలో మాక్ టెస్టులు లేవు" else "No mock tests in this category yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isTe) "అడ్మిన్ డ్యాష్‌బోర్డ్ నుండి కొత్త మాక్ టెస్ట్ అప్‌లోడ్ చేయండి." else "Upload a new bilingual mock test from the Admin Dashboard tab.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredTests, key = { it.id }) { test ->
                    MockTestRowCard(
                        test = test,
                        language = language,
                        onStart = { onStartTest(test) }
                    )
                }
            }

            // My Recent Scorecards (Firestore Synced)
            item {
                Spacer(modifier = Modifier.height(6.dp))
                SectionHeading(
                    title = if (isTe) "నా స్కోర్‌కార్డ్ చరిత్ర" else "My Saved Scorecards",
                    subtitle = if (isTe) "క్లౌడ్‌లో సేవ్ చేయబడిన CBT ఫలితాలు" else "Your recent CBT attempts synced to Firestore",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (attempts.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isTe) {
                                    "ఇంకా ఏ టెస్ట్ పూర్తి కాలేదు. పై నున్న ఉచిత డెమో CBT టెస్ట్ ప్రయత్నించండి!"
                                } else {
                                    "No attempts yet. Start any free demo CBT test above to save your first scorecard!"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(attempts.take(8), key = { it.id.ifBlank { "${it.testId}_${it.timeTakenSeconds}_${it.score}" } }) { attempt ->
                    AttemptHistoryCard(attempt = attempt, language = language)
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun MockTestRowCard(
    test: MockTest,
    language: AppLanguage,
    onStart: () -> Unit
) {
    val isTe = language == AppLanguage.TE
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = test.categoryEnum().label(language),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (!test.bundledStarter) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = EmeraldTeal,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTe) "కొత్త అప్‌లోడ్" else "LIVE CMS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldTeal
                                )
                            }
                        }
                    }
                }
                if (test.freeDemo) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (isTe) "ఉచితం" else "FREE DEMO",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = test.title(language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (test.description(language).isNotBlank()) {
                Text(
                    text = test.description(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${test.questions.size} Qs",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${test.durationMinutes} min",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Text(
                        text = "+${test.marksPerQuestion} / -${test.negativeMarking}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CbtNotAnsweredRed
                    )
                }

                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("start_cbt_test_${test.id}")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isTe) "స్టార్ట్" else "Start CBT")
                }
            }
        }
    }
}

@Composable
private fun AttemptHistoryCard(
    attempt: ExamAttempt,
    language: AppLanguage
) {
    val isTe = language == AppLanguage.TE
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = attempt.title(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isTe) {
                        "సరైనవి: ${attempt.correctCount} • తప్పులు: ${attempt.wrongCount} • సమయం: ${formatTimerSeconds(attempt.timeTakenSeconds)}"
                    } else {
                        "Correct: ${attempt.correctCount} • Wrong: ${attempt.wrongCount} • Time: ${formatTimerSeconds(attempt.timeTakenSeconds)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%.2f / %.0f".format(attempt.score, attempt.maxScore),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (attempt.score >= attempt.maxScore * 0.5) CbtAnsweredGreen else CbtNotAnsweredRed
                )
                Text(
                    text = "${attempt.accuracyPercent.toInt()}% acc",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
