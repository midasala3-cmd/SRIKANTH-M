package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.CbtSessionState
import com.example.ui.components.BilingualToggleChip
import com.example.ui.components.formatTimerSeconds
import com.example.ui.theme.CbtAnsweredBg
import com.example.ui.theme.CbtAnsweredGreen
import com.example.ui.theme.CbtNotAnsweredBg
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.CbtNotVisitedBg
import com.example.ui.theme.CbtNotVisitedSlate
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.SaffronGoldBright

@Composable
internal fun CbtResultAndSolutionsScreen(
    session: CbtSessionState,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onDone: () -> Unit
) {
    val isTe = language == AppLanguage.TE
    val attempt = session.lastAttemptSummary
    val optionLabels = listOf("A", "B", "C", "D")

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Surface(color = RoyalNavyDark, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(
                            onClick = onDone,
                            modifier = Modifier.testTag("result_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (isTe) "పోర్టల్‌కు తిరిగి వెళ్ళండి" else "Back to portal",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = if (isTe) "స్కోర్‌కార్డ్ & వివరణలు" else "Scorecard & Solutions",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BilingualToggleChip(language = language, onToggle = onToggleLanguage)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (attempt != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = session.test.title(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = if (isTe) "నెట్ స్కోర్" else "NET SCORE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SaffronGoldBright
                                        )
                                        Text(
                                            text = "%.2f / %.0f".format(attempt.score, attempt.maxScore),
                                            style = MaterialTheme.typography.displaySmall.copy(
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${attempt.accuracyPercent.toInt()}%",
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                            color = SaffronGoldBright
                                        )
                                        Text(
                                            text = if (isTe) {
                                                "ఖచ్చితత్వం • ${formatTimerSeconds(attempt.timeTakenSeconds)}"
                                            } else {
                                                "Accuracy • ${formatTimerSeconds(attempt.timeTakenSeconds)}"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val penalty = attempt.wrongCount * attempt.negativeMarking
                                    ResultChipStat(
                                        label = if (isTe) "సరైనవి" else "Correct",
                                        value = "${attempt.correctCount}",
                                        bg = CbtAnsweredBg,
                                        fg = CbtAnsweredGreen
                                    )
                                    ResultChipStat(
                                        label = if (isTe) "తప్పులు" else "Wrong",
                                        value = "${attempt.wrongCount} (-%.2f)".format(penalty),
                                        bg = CbtNotAnsweredBg,
                                        fg = CbtNotAnsweredRed
                                    )
                                    ResultChipStat(
                                        label = if (isTe) "వదిలినవి" else "Skipped",
                                        value = "${attempt.unansweredCount}",
                                        bg = CbtNotVisitedBg,
                                        fg = RoyalNavyDark
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = if (isTe) "దశలవారీ ద్విభాషా పరిష్కారాలు" else "Step-by-Step Bilingual Solutions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                itemsIndexed(session.test.questions) { idx, question ->
                    val chosen = session.selectedAnswers[idx]
                    val isCorrect = chosen == question.correctIndex
                    val options = question.options(language)

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            when {
                                chosen == null -> MaterialTheme.colorScheme.outlineVariant
                                isCorrect -> CbtAnsweredGreen.copy(alpha = 0.5f)
                                else -> CbtNotAnsweredRed.copy(alpha = 0.5f)
                            }
                        )
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
                                Text(
                                    text = "Q${idx + 1} • ${question.section}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when {
                                            chosen == null -> Icons.Default.Bookmark
                                            isCorrect -> Icons.Default.CheckCircle
                                            else -> Icons.Default.Cancel
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            chosen == null -> CbtNotVisitedSlate
                                            isCorrect -> CbtAnsweredGreen
                                            else -> CbtNotAnsweredRed
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when {
                                            chosen == null -> if (isTe) "వదిలివేయబడింది" else "Skipped (0)"
                                            isCorrect -> "+${session.test.marksPerQuestion}"
                                            else -> "-${session.test.negativeMarking}"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when {
                                            chosen == null -> CbtNotVisitedSlate
                                            isCorrect -> CbtAnsweredGreen
                                            else -> CbtNotAnsweredRed
                                        }
                                    )
                                }
                            }

                            Text(
                                text = question.questionText(language),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (chosen != null && !isCorrect) {
                                Text(
                                    text = if (isTe) {
                                        "మీ సమాధానం: ${optionLabels.getOrElse(chosen) { "" }}. ${options.getOrElse(chosen) { "" }}"
                                    } else {
                                        "Your Answer: ${optionLabels.getOrElse(chosen) { "" }}. ${options.getOrElse(chosen) { "" }}"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = CbtNotAnsweredRed
                                )
                            }

                            Text(
                                text = if (isTe) {
                                    "సరైన సమాధానం: ${optionLabels.getOrElse(question.correctIndex) { "" }}. ${options.getOrElse(question.correctIndex) { "" }}"
                                } else {
                                    "Correct Answer: ${optionLabels.getOrElse(question.correctIndex) { "" }}. ${options.getOrElse(question.correctIndex) { "" }}"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = CbtAnsweredGreen
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isTe) "దశలవారీ వివరణ" else "Step-by-Step Explanation",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = question.explanation(language),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onDone,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("result_done_button")
                    ) {
                        Text(if (isTe) "CBT పోర్టల్‌కు తిరిగి వెళ్ళండి" else "Return to CBT Portal")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultChipStat(label: String, value: String, bg: Color, fg: Color) {
    Surface(shape = RoundedCornerShape(10.dp), color = bg) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = fg
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = fg.copy(alpha = 0.85f)
            )
        }
    }
}
