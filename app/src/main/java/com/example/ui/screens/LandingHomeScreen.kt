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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.CoachingCourse
import com.example.data.model.ExamAttempt
import com.example.data.model.ExamCategory
import com.example.data.model.MockTest
import com.example.ui.components.SectionHeading
import com.example.ui.components.sharePortalContent
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronGoldBright

@Composable
fun LandingHomeScreen(
    language: AppLanguage,
    aspirantName: String,
    targetExam: ExamCategory,
    courses: List<CoachingCourse>,
    enrolledCourseIds: List<String>,
    freeDemoTests: List<MockTest>,
    attempts: List<ExamAttempt>,
    onSelectTargetExam: (ExamCategory) -> Unit,
    onStartTest: (MockTest) -> Unit,
    onToggleCourseEnrollment: (String) -> Unit,
    onOpenAllTests: () -> Unit,
    onOpenBlogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTe = language == AppLanguage.TE
    val context = LocalContext.current
    val featuredDemo = freeDemoTests.firstOrNull { it.examCategory == targetExam.code }
        ?: freeDemoTests.firstOrNull()

    val avgAccuracy = if (attempts.isNotEmpty()) {
        attempts.map { it.accuracyPercent }.average()
    } else 0.0
    val bestScore = attempts.maxOfOrNull { it.score }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. High-Converting Hero Landing Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalNavyDark)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(185.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_coaching_1795427352670),
                                contentDescription = "Vijeta AP coaching banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                RoyalNavyDark.copy(alpha = 0.25f),
                                                RoyalNavyDark.copy(alpha = 0.92f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(18.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SaffronGoldBright
                                ) {
                                    Text(
                                        text = if (isTe) "నమస్కారం, $aspirantName • 2026 బ్యాచ్" else "WELCOME, $aspirantName • 2026 TARGET",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RoyalNavyDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isTe) {
                                        "APPSC & ఏపీ పోలీస్ ర్యాంక్ మీ లక్ష్యమైతే — ఇప్పుడే ఉచిత CBT టెస్ట్ రాయండి"
                                    } else {
                                        "Andhra Pradesh's Bilingual APPSC & AP Police Exam Portal"
                                    },
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RoyalNavyDark)
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isTe) {
                                    "ఇంగ్లీష్ & తెలుగులో రియల్ CBT టైమర్, క్వశ్చన్ పాలెట్, నెగటివ్ మార్కింగ్ & దశలవారీ వివరణలు."
                                } else {
                                    "Master APPSC Group 1–4 & AP Police SI/Constable with bilingual CBT mocks, negative marking analytics, and step-by-step solutions."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (featuredDemo != null) onStartTest(featuredDemo) else onOpenAllTests()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SaffronGoldBright,
                                        contentColor = RoyalNavyDark
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("hero_start_demo_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTe) "ఉచిత డెమో టెస్ట్" else "Free Demo Test",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                }
                                OutlinedButton(
                                    onClick = onOpenBlogs,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("hero_study_notes_button")
                                ) {
                                    Text(
                                        text = if (isTe) "స్టడీ నోట్స్" else "Study Blogs",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Target Exam Selector Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isTe) "మీ లక్ష్య పరీక్షను ఎంచుకోండి" else "Select Your Target Exam",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ExamCategory.entries) { category ->
                            val selected = category == targetExam
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectTargetExam(category) },
                                label = {
                                    Text(
                                        text = category.label(language),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("target_exam_chip_${category.code}")
                            )
                        }
                    }
                }
            }

            // 3. Live Aspirant Scorecard Snapshot
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        label = if (isTe) "రాసిన టెస్టులు" else "Mocks Taken",
                        value = "${attempts.size}",
                        accent = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        label = if (isTe) "ఖచ్చితత్వం" else "Avg Accuracy",
                        value = if (attempts.isEmpty()) "—" else "${avgAccuracy.toInt()}%",
                        accent = EmeraldTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        label = if (isTe) "బెస్ట్ స్కోర్" else "Best Score",
                        value = if (bestScore == null) "—" else "%.2f".format(bestScore),
                        accent = SaffronGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Free Demo Mock Tests Carousel
            item {
                SectionHeading(
                    title = if (isTe) "ఉచిత డెమో CBT మాక్ టెస్టులు" else "Free Demo CBT Mock Tests",
                    subtitle = if (isTe) "APPSC & SLPRB అసలు పరీక్ష ప్యాటర్న్" else "Real APPSC & SLPRB exam pattern with instant rank analysis",
                    trailingActionLabel = if (isTe) "అన్నీ చూడండి" else "View All",
                    onTrailingClick = onOpenAllTests,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(freeDemoTests.take(6), key = { it.id }) { test ->
                        FreeDemoTestCard(
                            test = test,
                            language = language,
                            onStart = { onStartTest(test) }
                        )
                    }
                }
            }

            // 5. Flagship Bilingual Coaching Courses
            item {
                SectionHeading(
                    title = if (isTe) "ప్రధాన కోచింగ్ బ్యాచ్‌లు" else "Flagship Coaching Courses",
                    subtitle = if (isTe) "ఇంగ్లీష్ & తెలుగు మీడియం • సిలబస్ & టెస్ట్ సిరీస్" else "Bilingual English & Telugu batches with syllabus notes & CBT series",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(courses, key = { it.id }) { course ->
                val enrolled = enrolledCourseIds.contains(course.id)
                CourseDetailCard(
                    course = course,
                    language = language,
                    enrolled = enrolled,
                    onToggleEnroll = { onToggleCourseEnrollment(course.id) }
                )
            }

            // 6. App Download & SEO Web/PWA Share Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = EmeraldTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isTe) "వెబ్ PWA & ఆండ్రాయిడ్ యాప్ షేర్ చేయండి" else "Share Vijeta AP Web Portal & App Link",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Text(
                            text = if (isTe) {
                                "మీ స్నేహితులతో ఉచిత APPSC & ఏపీ పోలీస్ డెమో మాక్ టెస్టులు మరియు Google SEO స్టడీ బ్లాగ్స్ లింక్ షేర్ చేయండి."
                            } else {
                                "Invite fellow APPSC & AP Police aspirants to attempt free bilingual CBT mock tests and read SEO study notes anytime on mobile or web."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    sharePortalContent(
                                        context = context,
                                        subject = "Vijeta AP — Bilingual APPSC & AP Police CBT Mock Portal",
                                        body = "Prepare for APPSC Group 1, 2 & AP Police SI/Constable in English & Telugu! Free CBT Demo Mock Tests with -1/3 & -0.25 negative marking and step-by-step solutions on Vijeta AP."
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("share_app_link_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTe) "యాప్ లింక్ షేర్" else "Share App Link",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            OutlinedButton(
                                onClick = onOpenAllTests,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, EmeraldTeal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldTeal),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("open_cbt_portal_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTe) "CBT పోర్టల్" else "CBT Portal",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun MetricStatCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FreeDemoTestCard(
    test: MockTest,
    language: AppLanguage,
    onStart: () -> Unit
) {
    val isTe = language == AppLanguage.TE
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(275.dp)
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = test.categoryEnum().shortBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = if (isTe) "ఉచిత డెమో" else "FREE DEMO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = test.title(language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${test.durationMinutes}m • ${test.questions.size} Qs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "-${test.negativeMarking} Neg",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            }
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("demo_test_start_${test.id}")
            ) {
                Text(
                    text = if (isTe) "పరీక్ష ప్రారంభించండి" else "Attempt Now",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun CourseDetailCard(
    course: CoachingCourse,
    language: AppLanguage,
    enrolled: Boolean,
    onToggleEnroll: () -> Unit
) {
    val isTe = language == AppLanguage.TE
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (enrolled) 1.5.dp else 1.dp,
            color = if (enrolled) EmeraldTeal else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalNavy.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (isTe) course.featuredBadgeTe else course.featuredBadgeEn,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = if (isTe) course.durationLabelTe else course.durationLabelEn,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = if (isTe) course.titleTe else course.titleEn,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isTe) course.subtitleTe else course.subtitleEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val highlights = if (isTe) course.highlightsTe else course.highlightsEn
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                highlights.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = course.priceDisplay,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = course.originalPriceDisplay,
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${course.mockTestsCount} Mocks • ${course.videoHours}h Classes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onToggleEnroll,
                    shape = RoundedCornerShape(12.dp),
                    colors = if (enrolled) {
                        ButtonDefaults.buttonColors(containerColor = EmeraldTeal, contentColor = Color.White)
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("course_enroll_button_${course.id}")
                ) {
                    if (enrolled) {
                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isTe) "చేరారు" else "Enrolled")
                    } else {
                        Text(if (isTe) "బ్యాచ్‌లో చేరండి" else "Enroll Batch")
                    }
                }
            }
        }
    }
}
