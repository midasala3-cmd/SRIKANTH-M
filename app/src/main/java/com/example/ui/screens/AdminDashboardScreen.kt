package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.BlogCategory
import com.example.data.model.CbtQuestion
import com.example.data.model.ExamCategory
import com.example.data.model.MockTest
import com.example.data.model.StudyBlog
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.JetBrainsMonoFontFamily

private enum class AdminTab { UPLOAD_TEST, PUBLISH_BLOG, MANAGE }

@Composable
fun AdminDashboardScreen(
    language: AppLanguage,
    currentUserId: String,
    isPublishing: Boolean,
    cloudTests: List<MockTest>,
    cloudBlogs: List<StudyBlog>,
    onPublishTest: (
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
    ) -> Unit,
    onPublishBlog: (
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
    ) -> Unit,
    onDeleteTest: (String) -> Unit,
    onDeleteBlog: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTe = language == AppLanguage.TE
    var activeTab by remember { mutableStateOf(AdminTab.UPLOAD_TEST) }

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
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isTe) "అడ్మిన్ CMS డ్యాష్‌బోర్డ్" else "Admin CMS Dashboard",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isTe) {
                            "కొత్త ద్విభాషా CBT మాక్ టెస్టులు & Google SEO స్టడీ బ్లాగ్స్ ప్రచురించండి"
                        } else {
                            "Upload bilingual CBT mock tests & publish Google SEO study blogs live to Firestore"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        val tabs = listOf(
                            AdminTab.UPLOAD_TEST to (if (isTe) "టెస్ట్ అప్‌లోడ్" else "Upload Test"),
                            AdminTab.PUBLISH_BLOG to (if (isTe) "SEO బ్లాగ్" else "SEO Blog"),
                            AdminTab.MANAGE to (if (isTe) "నిర్వహణ" else "Manage")
                        )
                        tabs.forEachIndexed { idx, (tab, label) ->
                            SegmentedButton(
                                selected = activeTab == tab,
                                onClick = { activeTab = tab },
                                shape = SegmentedButtonDefaults.itemShape(index = idx, count = tabs.size),
                                modifier = Modifier.testTag("admin_tab_${tab.name.lowercase()}")
                            ) {
                                Text(label, maxLines = 1)
                            }
                        }
                    }
                }
            }

            when (activeTab) {
                AdminTab.UPLOAD_TEST -> {
                    item {
                        UploadMockTestForm(
                            language = language,
                            isPublishing = isPublishing,
                            onPublishTest = onPublishTest,
                            onShowMessage = onShowMessage
                        )
                    }
                }
                AdminTab.PUBLISH_BLOG -> {
                    item {
                        PublishSeoBlogForm(
                            language = language,
                            isPublishing = isPublishing,
                            onPublishBlog = onPublishBlog,
                            onShowMessage = onShowMessage
                        )
                    }
                }
                AdminTab.MANAGE -> {
                    item {
                        ManageUploadsSection(
                            language = language,
                            currentUserId = currentUserId,
                            cloudTests = cloudTests,
                            cloudBlogs = cloudBlogs,
                            onDeleteTest = onDeleteTest,
                            onDeleteBlog = onDeleteBlog
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun UploadMockTestForm(
    language: AppLanguage,
    isPublishing: Boolean,
    onPublishTest: (
        String, String, String, String, ExamCategory, Int, Double, Double, Boolean, List<CbtQuestion>, () -> Unit
    ) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val isTe = language == AppLanguage.TE
    var titleEn by remember { mutableStateOf("") }
    var titleTe by remember { mutableStateOf("") }
    var descEn by remember { mutableStateOf("") }
    var descTe by remember { mutableStateOf("") }
    var selectedExam by remember { mutableStateOf(ExamCategory.APPSC_GROUP2) }
    var durationText by remember { mutableStateOf("15") }
    var negativeMarking by remember { mutableDoubleStateOf(0.33) }
    var freeDemo by remember { mutableStateOf(true) }

    // Draft Question inputs
    var qEn by remember { mutableStateOf("") }
    var qTe by remember { mutableStateOf("") }
    var optAEn by remember { mutableStateOf("") }
    var optBEn by remember { mutableStateOf("") }
    var optCEn by remember { mutableStateOf("") }
    var optDEn by remember { mutableStateOf("") }
    var expEn by remember { mutableStateOf("") }
    var expTe by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("AP History") }
    var correctIdx by remember { mutableIntStateOf(0) }

    val addedQuestions = remember { mutableStateListOf<CbtQuestion>() }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isTe) "1. పరీక్ష వివరాలు (Test Configuration)" else "1. Exam Metadata & Marking Scheme",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Mock Test Title (English) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_test_title_en")
                )
                OutlinedTextField(
                    value = titleTe,
                    onValueChange = { titleTe = it },
                    label = { Text("మాక్ టెస్ట్ పేరు (తెలుగు)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descEn,
                    onValueChange = { descEn = it },
                    label = { Text("Short Description (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descTe,
                    onValueChange = { descTe = it },
                    label = { Text("వివరణ (తెలుగు)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = if (isTe) "పరీక్ష విభాగం" else "Target Exam Category", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ExamCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedExam == cat,
                            onClick = { selectedExam = cat },
                            label = { Text(cat.shortBadge) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { if (it.all(Char::isDigit) && it.length <= 3) durationText = it },
                        label = { Text("Minutes") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Column(modifier = Modifier.weight(1.6f)) {
                        Text(text = "Negative Marking", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0.0, 0.25, 0.33).forEach { neg ->
                                FilterChip(
                                    selected = negativeMarking == neg,
                                    onClick = { negativeMarking = neg },
                                    label = { Text(if (neg == 0.0) "None" else "-$neg") }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTe) "ఉచిత డెమో మాక్ టెస్ట్ గా ఫీచర్ చేయండి" else "Feature as Free Demo Mock Test",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(checked = freeDemo, onCheckedChange = { freeDemo = it })
                }
            }
        }

        // Question Builder Card
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
                    Text(
                        text = "2. Bilingual Questions (${addedQuestions.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedButton(
                        onClick = {
                            qEn = "Which city was designated as the capital of Andhra State when it was formed on October 1, 1953?"
                            qTe = "1953 అక్టోబర్ 1న ఆంధ్ర రాష్ట్రం ఏర్పడినప్పుడు ఏ నగరాన్ని రాజధానిగా ప్రకటించారు?"
                            optAEn = "Vijayawada"
                            optBEn = "Kurnool"
                            optCEn = "Guntur"
                            optDEn = "Visakhapatnam"
                            correctIdx = 1
                            section = "AP History"
                            expEn = "Step 1: Andhra State was carved out of Madras Presidency on Oct 1, 1953.\nStep 2: Kurnool became its first capital and Guntur hosted the High Court."
                            expTe = "దశ 1: 1953 అక్టోబర్ 1న మద్రాసు నుండి ఆంధ్ర రాష్ట్రం ఏర్పడింది.\nదశ 2: కర్నూలు తొలి రాజధానిగా మరియు గుంటూరు హైకోర్టు కేంద్రంగా నిలిచాయి."
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_autofill_sample_q")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sample Q")
                    }
                }

                OutlinedTextField(
                    value = qEn,
                    onValueChange = { qEn = it },
                    label = { Text("Question (English) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = qTe,
                    onValueChange = { qTe = it },
                    label = { Text("ప్రశ్న (తెలుగు)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = optAEn, onValueChange = { optAEn = it }, label = { Text("Option A *") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = optBEn, onValueChange = { optBEn = it }, label = { Text("Option B *") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = optCEn, onValueChange = { optCEn = it }, label = { Text("Option C *") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = optDEn, onValueChange = { optDEn = it }, label = { Text("Option D *") }, modifier = Modifier.weight(1f), singleLine = true)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Correct Answer:", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("A", "B", "C", "D").forEachIndexed { idx, letter ->
                            FilterChip(
                                selected = correctIdx == idx,
                                onClick = { correctIdx = idx },
                                label = { Text(letter) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("Subject / Section (e.g. AP History, Polity)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expEn,
                    onValueChange = { expEn = it },
                    label = { Text("Step-by-Step Solution (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expTe,
                    onValueChange = { expTe = it },
                    label = { Text("వివరణాత్మక పరిష్కారం (తెలుగు)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = {
                        if (qEn.isBlank() || optAEn.isBlank() || optBEn.isBlank() || optCEn.isBlank() || optDEn.isBlank()) {
                            onShowMessage("Enter the question and all 4 options first.")
                            return@OutlinedButton
                        }
                        addedQuestions.add(
                            CbtQuestion(
                                questionEn = qEn.trim(),
                                questionTe = qTe.trim().ifBlank { qEn.trim() },
                                optionsEn = listOf(optAEn.trim(), optBEn.trim(), optCEn.trim(), optDEn.trim()),
                                optionsTe = listOf(optAEn.trim(), optBEn.trim(), optCEn.trim(), optDEn.trim()),
                                correctIndex = correctIdx,
                                explanationEn = expEn.trim().ifBlank { "Correct answer is Option ${listOf("A", "B", "C", "D")[correctIdx]}." },
                                explanationTe = expTe.trim().ifBlank { expEn.trim() },
                                section = section.trim().ifBlank { "General Studies" }
                            )
                        )
                        qEn = ""; qTe = ""; optAEn = ""; optBEn = ""; optCEn = ""; optDEn = ""; expEn = ""; expTe = ""
                        onShowMessage("Question #${addedQuestions.size} added to draft!")
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_add_question_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Question to Test (${addedQuestions.size})")
                }

                if (addedQuestions.isNotEmpty()) {
                    addedQuestions.forEachIndexed { idx, q ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Q${idx + 1}: ${q.questionEn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { addedQuestions.removeAt(idx) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove question", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (titleEn.trim().length < 3) {
                    onShowMessage("English test title must be at least 3 characters.")
                    return@Button
                }
                if (addedQuestions.isEmpty()) {
                    onShowMessage("Add at least 1 question before publishing.")
                    return@Button
                }
                onPublishTest(
                    titleEn,
                    titleTe,
                    descEn,
                    descTe,
                    selectedExam,
                    durationText.toIntOrNull()?.coerceIn(1, 240) ?: 15,
                    1.0,
                    negativeMarking,
                    freeDemo,
                    addedQuestions.toList()
                ) {
                    titleEn = ""; titleTe = ""; descEn = ""; descTe = ""
                    addedQuestions.clear()
                }
            },
            enabled = !isPublishing,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("admin_publish_test_button")
        ) {
            if (isPublishing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTe) "CBT పోర్టల్‌లో ప్రచురించండి" else "Publish Mock Test to CBT Portal")
            }
        }
    }
}

@Composable
private fun PublishSeoBlogForm(
    language: AppLanguage,
    isPublishing: Boolean,
    onPublishBlog: (
        String, String, String, String, String, BlogCategory, String, String, String, String, Int, () -> Unit
    ) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val isTe = language == AppLanguage.TE
    var titleEn by remember { mutableStateOf("") }
    var titleTe by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var metaDesc by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("appsc, ap police, telugu notes") }
    var category by remember { mutableStateOf(BlogCategory.APPSC) }
    var summaryEn by remember { mutableStateOf("") }
    var summaryTe by remember { mutableStateOf("") }
    var contentEn by remember { mutableStateOf("") }
    var contentTe by remember { mutableStateOf("") }
    var readMins by remember { mutableStateOf("5") }

    val cleanSlugPreview = remember(slug, titleEn) {
        val base = slug.ifBlank { titleEn }
        base.lowercase()
            .replace(Regex("[^a-z0-9\\-]+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
            .ifBlank { "your-seo-article-slug" }
    }

    val seoReadinessScore = remember(titleEn, cleanSlugPreview, metaDesc, keywords, contentEn) {
        var score = 0
        if (titleEn.trim().length in 15..160) score += 25
        if (cleanSlugPreview != "your-seo-article-slug" && cleanSlugPreview.length >= 6) score += 20
        if (metaDesc.trim().length in 40..160) score += 25
        if (keywords.split(",").count { it.isNotBlank() } >= 3) score += 15
        if (contentEn.trim().length >= 80) score += 15
        score
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Live Google SERP Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = EmeraldTeal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Search Preview",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldTeal
                        )
                    }
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.tertiaryContainer) {
                        Text(
                            text = "SEO Score: $seoReadinessScore/100",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldTeal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Text(
                    text = "https://vijetaap.in/blogs/$cleanSlugPreview",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = titleEn.ifBlank { "Your SEO-Optimized APPSC / AP Police Blog Headline" },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = metaDesc.ifBlank { "Write a 40–160 character meta description so aspirants discover your article on Google Search." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = titleEn,
                    onValueChange = {
                        titleEn = it
                        if (slug.isBlank()) {
                            slug = it.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').take(80)
                        }
                    },
                    label = { Text("SEO Headline (English) *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_blog_title_en")
                )
                OutlinedTextField(
                    value = titleTe,
                    onValueChange = { titleTe = it },
                    label = { Text("బ్లాగ్ శీర్షిక (తెలుగు)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it.lowercase().replace(Regex("[^a-z0-9\\-]"), "") },
                    label = { Text("URL Slug (lowercase, hyphens) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = metaDesc,
                    onValueChange = { if (it.length <= 220) metaDesc = it },
                    label = { Text("Google Meta Description (${metaDesc.length}/160 ideal) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("SEO Keywords (comma-separated, max 10)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(BlogCategory.entries) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label(language)) }
                        )
                    }
                }

                OutlinedTextField(
                    value = summaryEn,
                    onValueChange = { summaryEn = it },
                    label = { Text("Short Summary (English) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summaryTe,
                    onValueChange = { summaryTe = it },
                    label = { Text("సారాంశం (తెలుగు)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contentEn,
                    onValueChange = { contentEn = it },
                    label = { Text("Full Study Article Body (English) *") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contentTe,
                    onValueChange = { contentTe = it },
                    label = { Text("పూర్తి స్టడీ ఆర్టికల్ (తెలుగు)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = readMins,
                    onValueChange = { if (it.all(Char::isDigit) && it.length <= 3) readMins = it },
                    label = { Text("Read Time (Minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                val finalSlug = slug.ifBlank { cleanSlugPreview }
                if (titleEn.trim().length < 5 || metaDesc.trim().length < 10 || summaryEn.trim().length < 10 || contentEn.trim().length < 20) {
                    onShowMessage("Please complete Headline (5+ chars), Meta Description (10+), Summary (10+), and Body (20+).")
                    return@Button
                }
                onPublishBlog(
                    titleEn,
                    titleTe,
                    finalSlug,
                    metaDesc,
                    keywords,
                    category,
                    summaryEn,
                    summaryTe,
                    contentEn,
                    contentTe,
                    readMins.toIntOrNull()?.coerceIn(1, 120) ?: 5
                ) {
                    titleEn = ""; titleTe = ""; slug = ""; metaDesc = ""
                    summaryEn = ""; summaryTe = ""; contentEn = ""; contentTe = ""
                }
            },
            enabled = !isPublishing,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("admin_publish_blog_button")
        ) {
            if (isPublishing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTe) "SEO బ్లాగ్ ప్రచురించండి" else "Publish SEO Study Blog")
            }
        }
    }
}

@Composable
private fun ManageUploadsSection(
    language: AppLanguage,
    currentUserId: String,
    cloudTests: List<MockTest>,
    cloudBlogs: List<StudyBlog>,
    onDeleteTest: (String) -> Unit,
    onDeleteBlog: (String) -> Unit
) {
    val isTe = language == AppLanguage.TE
    val myTests = cloudTests.filter { !it.bundledStarter }
    val myBlogs = cloudBlogs.filter { !it.bundledStarter }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (isTe) "క్లౌడ్ మాక్ టెస్టులు (${myTests.size})" else "Cloud Uploaded Mock Tests (${myTests.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (myTests.isEmpty()) {
            Text(
                text = if (isTe) "ఇంకా అప్‌లోడ్ చేసిన మాక్ టెస్టులు లేవు." else "No custom mock tests uploaded yet. Use the Upload Test tab above.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            myTests.forEach { test ->
                Card(
                    shape = RoundedCornerShape(14.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(test.title(language), style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${test.questions.size} Qs • ${test.durationMinutes}m • -${test.negativeMarking}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (test.authorId == currentUserId) {
                            IconButton(onClick = { onDeleteTest(test.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete test", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isTe) "క్లౌడ్ SEO బ్లాగ్స్ (${myBlogs.size})" else "Cloud Published SEO Blogs (${myBlogs.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (myBlogs.isEmpty()) {
            Text(
                text = if (isTe) "ఇంకా ప్రచురించిన SEO బ్లాగ్స్ లేవు." else "No custom SEO blogs published yet. Use the SEO Blog tab above.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            myBlogs.forEach { blog ->
                Card(
                    shape = RoundedCornerShape(14.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(blog.title(language), style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "/blogs/${blog.slug}",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                                color = EmeraldTeal
                            )
                        }
                        if (blog.authorId == currentUserId) {
                            IconButton(onClick = { onDeleteBlog(blog.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete blog", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
