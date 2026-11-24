package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.CbtQuestion
import com.example.data.model.CbtSessionState
import com.example.data.model.QuestionPaletteStatus
import com.example.ui.theme.CbtAnsweredBg
import com.example.ui.theme.CbtAnsweredGreen
import com.example.ui.theme.CbtAnsweredMarkedTeal
import com.example.ui.theme.CbtMarkedBg
import com.example.ui.theme.CbtMarkedPurple
import com.example.ui.theme.CbtNotAnsweredBg
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.CbtNotVisitedBg
import com.example.ui.theme.CbtNotVisitedSlate
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.SaffronGoldBright
import com.example.ui.theme.TealLight

/**
 * Visual remaining-time progress indicator for the CBT exam header.
 * Animates smoothly as seconds count down and shifts color from Teal -> Amber -> Crimson
 * as remaining exam time enters warning and critical thresholds.
 */
@Composable
fun ExamTimeProgressIndicator(
    remainingSeconds: Int,
    totalDurationSeconds: Int,
    isTelugu: Boolean,
    modifier: Modifier = Modifier
) {
    val safeTotal = totalDurationSeconds.coerceAtLeast(1)
    val safeRemaining = remainingSeconds.coerceIn(0, safeTotal)
    val remainingFraction = (safeRemaining.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
    val remainingPercent = (remainingFraction * 100f).toInt()

    val isCritical = remainingFraction <= 0.15f || safeRemaining <= 120
    val isWarning = !isCritical && (remainingFraction <= 0.35f || safeRemaining <= 300)

    val animatedProgress by animateFloatAsState(
        targetValue = remainingFraction,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "cbt_remaining_time_progress"
    )

    val progressColor by animateColorAsState(
        targetValue = when {
            isCritical -> CbtNotAnsweredRed
            isWarning -> SaffronGoldBright
            else -> TealLight
        },
        animationSpec = tween(durationMillis = 300),
        label = "cbt_time_progress_color"
    )

    val badgeContainerColor by animateColorAsState(
        targetValue = when {
            isCritical -> CbtNotAnsweredRed
            isWarning -> SaffronGoldBright.copy(alpha = 0.22f)
            else -> RoyalNavy
        },
        animationSpec = tween(durationMillis = 300),
        label = "cbt_timer_badge_bg"
    )

    val pulseTransition = rememberInfiniteTransition(label = "low_time_pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCritical) 0.35f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "low_time_alpha"
    )

    val formattedTime = formatTimerSeconds(safeRemaining)
    val accessibilityDesc = if (isTelugu) {
        "మిగిలిన పరీక్ష సమయం $formattedTime, $remainingPercent శాతం మిగిలి ఉంది"
    } else {
        "Remaining exam time $formattedTime, $remainingPercent percent remaining"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = accessibilityDesc },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(if (isCritical) pulseAlpha else 1f)
                        .clip(CircleShape)
                        .background(progressColor)
                )
                Text(
                    text = when {
                        isCritical -> if (isTelugu) "సమయం ముగుస్తోంది!" else "Hurry! Low Time"
                        else -> if (isTelugu) "మిగిలిన సమయం" else "Time Remaining"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isCritical) Color(0xFFFCA5A5) else Color.White.copy(alpha = 0.88f)
                )
                Text(
                    text = "• $remainingPercent%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Medium
                    ),
                    color = progressColor
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeContainerColor,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isCritical) Color.White.copy(alpha = 0.45f) else progressColor.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("cbt_timer_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = if (isCritical) Icons.Default.WarningAmber else Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isWarning && !isCritical) SaffronGoldBright else Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            color = progressColor,
            trackColor = Color.White.copy(alpha = 0.18f),
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .testTag("cbt_time_progress_indicator")
        )
    }
}

/**
 * Scrollable CBT question viewport combining the question metadata header,
 * bilingual question stem with adjustable typography scale, and the multi-choice answer selector.
 * Automatically resets scroll to the top whenever the user switches to another question.
 */
@Composable
fun ScrollableQuestionDisplay(
    questionNumber: Int,
    totalQuestions: Int,
    question: CbtQuestion,
    language: AppLanguage,
    marksPerQuestion: Double,
    negativeMarking: Double,
    isMarkedForReview: Boolean,
    selectedOptionIndex: Int?,
    fontScaleLevel: Int,
    onCycleFontSize: () -> Unit,
    onSelectOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTe = language == AppLanguage.TE
    val scrollState = rememberScrollState()

    LaunchedEffect(questionNumber) {
        scrollState.scrollTo(0)
    }

    val stemFontSize = when (fontScaleLevel) {
        1 -> 19.sp
        2 -> 21.sp
        else -> 17.sp
    }
    val stemLineHeight = when (fontScaleLevel) {
        1 -> 29.sp
        2 -> 32.sp
        else -> 26.sp
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("cbt_scrollable_question_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Question Stem Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cbt_question_card")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Crisp top accent bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            if (isMarkedForReview) CbtMarkedPurple else MaterialTheme.colorScheme.primary
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Metadata row: Question number, section, marks & font scale button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Q$questionNumber / $totalQuestions",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            if (question.section.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = question.section,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onCycleFontSize,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("cbt_font_size_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = if (isTe) "అక్షరాల పరిమాణం మార్చండి" else "Adjust question text size",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Marking scheme & review badge row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CbtAnsweredBg
                        ) {
                            Text(
                                text = "+$marksPerQuestion",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = CbtAnsweredGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (negativeMarking > 0.0) CbtNotAnsweredBg else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (negativeMarking > 0.0) "-$negativeMarking" else if (isTe) "నెగటివ్ లేదు" else "No Neg",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (negativeMarking > 0.0) CbtNotAnsweredRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (isMarkedForReview) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CbtMarkedBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = CbtMarkedPurple,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (isTe) "రివ్యూ" else "Review",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = CbtMarkedPurple
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

                    // Question Stem Text
                    Text(
                        text = question.questionText(language),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = stemFontSize,
                            lineHeight = stemLineHeight,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("cbt_question_stem_text")
                    )
                }
            }
        }

        // Answer Selection Header
        val optionLabels = listOf("A", "B", "C", "D")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isTe) "సరైన సమాధానాన్ని ఎంచుకోండి" else "Choose the correct answer",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (selectedOptionIndex != null) {
                val chosenLetter = optionLabels.getOrElse(selectedOptionIndex) { "${selectedOptionIndex + 1}" }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = CbtAnsweredBg
                ) {
                    Text(
                        text = if (isTe) "ఎంచుకున్నది: $chosenLetter" else "Selected: $chosenLetter",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CbtAnsweredGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Multi-Choice Answer Selection Component
        MultiChoiceAnswerSelector(
            options = question.options(language),
            selectedOptionIndex = selectedOptionIndex,
            onSelectOption = onSelectOption,
            fontScaleLevel = fontScaleLevel
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Accessible multi-choice answer selection component using RadioButton semantics,
 * animated selection borders/fills, A-D badges, and >= 48.dp touch targets.
 */
@Composable
fun MultiChoiceAnswerSelector(
    options: List<String>,
    selectedOptionIndex: Int?,
    onSelectOption: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fontScaleLevel: Int = 0,
    enabled: Boolean = true
) {
    val optionLabels = listOf("A", "B", "C", "D", "E")
    val optionFontSize = when (fontScaleLevel) {
        1 -> 17.sp
        2 -> 19.sp
        else -> 15.5.sp
    }
    val optionLineHeight = when (fontScaleLevel) {
        1 -> 25.sp
        2 -> 28.sp
        else -> 22.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
            .testTag("cbt_options_group"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { idx, optionText ->
            val isSelected = selectedOptionIndex == idx
            val label = optionLabels.getOrElse(idx) { "${idx + 1}" }

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                animationSpec = tween(durationMillis = 180),
                label = "option_container_$idx"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                animationSpec = tween(durationMillis = 180),
                label = "option_border_$idx"
            )
            val badgeBg by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                animationSpec = tween(durationMillis = 180),
                label = "option_badge_$idx"
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = containerColor,
                tonalElevation = if (isSelected) 3.dp else 0.dp,
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .selectable(
                        selected = isSelected,
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { onSelectOption(idx) }
                    )
                    .semantics {
                        contentDescription = "Option $label: $optionText"
                    }
                    .testTag("cbt_option_$idx")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = badgeBg,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = optionFontSize,
                            lineHeight = optionLineHeight,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    RadioButton(
                        selected = isSelected,
                        onClick = null, // Handled by the parent selectable card for unified ripple & TalkBack
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
    }
}

/**
 * Compact horizontal question number strip for quick single-tap jumping on phones.
 */
@Composable
fun CbtQuestionStrip(
    session: CbtSessionState,
    currentIndex: Int,
    onNavigateQuestion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val total = session.test.questions.size

    LaunchedEffect(currentIndex) {
        if (currentIndex in 0 until total) {
            listState.animateScrollToItem((currentIndex - 2).coerceAtLeast(0))
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth()
    ) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("cbt_question_strip")
        ) {
            items(total) { idx ->
                val status = session.statusFor(idx)
                val isCurrent = idx == currentIndex
                val (bg, fg) = paletteColorsForStatus(status)

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bg,
                    border = if (isCurrent) BorderStroke(2.dp, SaffronGoldBright) else null,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 42.dp, minHeight = 40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onNavigateQuestion(idx) }
                        .testTag("cbt_strip_tile_$idx")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = fg
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable Question Palette legend + grid, displayed inside a ModalBottomSheet on phones
 * and as a persistent supporting side pane on tablets/foldables.
 */
@Composable
fun CbtQuestionPaletteContent(
    session: CbtSessionState,
    currentIndex: Int,
    isTelugu: Boolean,
    onSelectQuestion: (Int) -> Unit,
    modifier: Modifier = Modifier,
    gridHeightDp: Int = 220
) {
    val total = session.test.questions.size
    val answeredCount = (0 until total).count { session.statusFor(it) == QuestionPaletteStatus.ANSWERED }
    val notAnsweredCount = (0 until total).count { session.statusFor(it) == QuestionPaletteStatus.NOT_ANSWERED }
    val markedCount = (0 until total).count { session.statusFor(it) == QuestionPaletteStatus.MARKED_FOR_REVIEW }
    val ansMarkedCount = (0 until total).count { session.statusFor(it) == QuestionPaletteStatus.ANSWERED_AND_MARKED }
    val notVisitedCount = (0 until total).count { session.statusFor(it) == QuestionPaletteStatus.NOT_VISITED }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isTelugu) "ప్రశ్నల పాలెట్" else "Question Palette",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PaletteLegendItem(
                color = CbtAnsweredGreen,
                label = if (isTelugu) "సమాధానం ($answeredCount)" else "Answered ($answeredCount)",
                modifier = Modifier.weight(1f)
            )
            PaletteLegendItem(
                color = CbtNotAnsweredRed,
                label = if (isTelugu) "వదిలినవి ($notAnsweredCount)" else "Not Ans ($notAnsweredCount)",
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PaletteLegendItem(
                color = CbtMarkedPurple,
                label = if (isTelugu) "రివ్యూ ($markedCount)" else "Marked ($markedCount)",
                modifier = Modifier.weight(1f)
            )
            PaletteLegendItem(
                color = CbtAnsweredMarkedTeal,
                label = if (isTelugu) "సమా+రివ్యూ ($ansMarkedCount)" else "Ans+Mark ($ansMarkedCount)",
                modifier = Modifier.weight(1f)
            )
        }
        PaletteLegendItem(
            color = CbtNotVisitedSlate,
            label = if (isTelugu) "చూడనివి ($notVisitedCount)" else "Not Visited ($notVisitedCount)",
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeightDp.dp)
        ) {
            items(total) { idx ->
                val status = session.statusFor(idx)
                val (bg, fg) = paletteColorsForStatus(status)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bg,
                    border = if (idx == currentIndex) BorderStroke(2.5.dp, SaffronGoldBright) else null,
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectQuestion(idx) }
                        .testTag("palette_tile_$idx")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = fg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun paletteColorsForStatus(status: QuestionPaletteStatus): Pair<Color, Color> = when (status) {
    QuestionPaletteStatus.ANSWERED -> CbtAnsweredGreen to Color.White
    QuestionPaletteStatus.NOT_ANSWERED -> CbtNotAnsweredRed to Color.White
    QuestionPaletteStatus.MARKED_FOR_REVIEW -> CbtMarkedPurple to Color.White
    QuestionPaletteStatus.ANSWERED_AND_MARKED -> CbtAnsweredMarkedTeal to Color.White
    QuestionPaletteStatus.NOT_VISITED -> CbtNotVisitedBg to MaterialTheme.colorScheme.onSurface
}

@Composable
private fun PaletteLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
