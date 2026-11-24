package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.firebase.authStateFlow
import com.example.data.firebase.signOutUser
import com.example.data.model.AppLanguage
import com.example.data.model.UiState
import com.example.data.repository.CoachingRepository
import com.example.data.repository.MockExamRepository
import com.example.ui.components.BilingualToggleChip
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BlogReaderScreen
import com.example.ui.screens.BlogsScreen
import com.example.ui.screens.CbtExamScreen
import com.example.ui.screens.LandingHomeScreen
import com.example.ui.screens.TestsHubScreen
import com.example.ui.theme.VijetaApTheme
import com.example.ui.viewmodel.CoachingViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VijetaApTheme {
                AppNavigation()
            }
        }
    }
}

private enum class MainTab(
    val route: String,
    val labelEn: String,
    val labelTe: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", "హోమ్", Icons.Filled.Home, Icons.Outlined.Home),
    TESTS("tests", "CBT Tests", "టెస్టులు", Icons.Filled.Quiz, Icons.Outlined.Quiz),
    BLOGS("blogs", "SEO Blogs", "బ్లాగ్స్", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    ADMIN("admin", "Admin", "అడ్మిన్", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
}

@Composable
private fun AppNavigation() {
    val auth = Firebase.auth
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = { /* authStateFlow emits signed-in user automatically */ })
    } else {
        key(user.uid) {
            val resolvedName = user.displayName?.takeIf { it.isNotBlank() }
                ?: user.phoneNumber?.takeIf { it.isNotBlank() }
                ?: "APPSC Aspirant"
            MainCoachingShell(
                currentUserId = user.uid,
                currentUserName = resolvedName,
                currentUserEmail = user.email.orEmpty()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainCoachingShell(
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String
) {
    val viewModel: CoachingViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                CoachingViewModel(
                    repository = CoachingRepository(db),
                    mockExamRepository = MockExamRepository(db),
                    currentUserId = currentUserId,
                    currentUserName = currentUserName,
                    currentUserEmail = currentUserEmail
                )
            }
        }
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val language by viewModel.language.collectAsStateWithLifecycle()
    val targetExam by viewModel.selectedTargetExam.collectAsStateWithLifecycle()
    val profileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val attemptsState by viewModel.attemptsState.collectAsStateWithLifecycle()
    val testsState by viewModel.mockTestsState.collectAsStateWithLifecycle()
    val blogsState by viewModel.studyBlogsState.collectAsStateWithLifecycle()
    val activeCbtSession by viewModel.activeCbtSession.collectAsStateWithLifecycle()
    val selectedBlog by viewModel.selectedBlog.collectAsStateWithLifecycle()
    val isPublishing by viewModel.isPublishing.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val userProfile = (profileState as? UiState.Success)?.data
    val enrolledIds = userProfile?.enrolledCourseIds ?: emptyList()
    val bookmarkedBlogIds = userProfile?.bookmarkedBlogIds ?: emptyList()
    val attempts = (attemptsState as? UiState.Success)?.data ?: emptyList()
    val allTests = (testsState as? UiState.Success)?.data ?: emptyList()
    val allBlogs = (blogsState as? UiState.Success)?.data ?: emptyList()

    // Sub-screen 1: Full-screen Interactive CBT Exam Portal
    val liveSession = activeCbtSession
    if (liveSession != null) {
        CbtExamScreen(
            session = liveSession,
            language = language,
            onToggleLanguage = viewModel::toggleLanguage,
            onSelectOption = viewModel::selectCbtOption,
            onClearResponse = viewModel::clearCurrentCbtResponse,
            onMarkForReviewAndNext = viewModel::toggleMarkForReviewAndNext,
            onNavigateQuestion = viewModel::navigateToCbtQuestion,
            onSubmitExam = { viewModel.submitCbtExam(autoSubmitted = false) },
            onExitExam = viewModel::exitCbtExam
        )
        return
    }

    // Sub-screen 2: Full-screen Bilingual SEO Study Blog Reader
    val openBlog = selectedBlog
    if (openBlog != null) {
        BlogReaderScreen(
            blog = openBlog,
            language = language,
            bookmarked = bookmarkedBlogIds.contains(openBlog.id),
            onToggleLanguage = viewModel::toggleLanguage,
            onToggleBookmark = { viewModel.toggleBlogBookmark(openBlog.id) },
            onBack = viewModel::closeBlog
        )
        return
    }

    val isTe = language == AppLanguage.TE

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vijeta AP",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    actions = {
                        BilingualToggleChip(
                            language = language,
                            onToggle = viewModel::toggleLanguage
                        )
                        IconButton(
                            onClick = {
                                signOutUser(
                                    credentialManager = credentialManager,
                                    scope = scope
                                )
                            },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = if (isTe) "సైన్ అవుట్" else "Sign out"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar {
                        MainTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = if (isTe) tab.labelTe else tab.labelEn
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (isTe) tab.labelTe else tab.labelEn,
                                        maxLines = 1
                                    )
                                },
                                modifier = Modifier.testTag("nav_tab_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        MainTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = if (isTe) tab.labelTe else tab.labelEn
                                    )
                                },
                                label = { Text(if (isTe) tab.labelTe else tab.labelEn) },
                                modifier = Modifier.testTag("nav_rail_${tab.route}")
                            )
                        }
                    }
                }

                when (currentTab) {
                    MainTab.HOME -> LandingHomeScreen(
                        language = language,
                        aspirantName = userProfile?.displayName ?: currentUserName,
                        targetExam = targetExam,
                        courses = viewModel.courses,
                        enrolledCourseIds = enrolledIds,
                        freeDemoTests = allTests.filter { it.freeDemo }.ifEmpty { allTests },
                        attempts = attempts,
                        onSelectTargetExam = viewModel::selectTargetExam,
                        onStartTest = viewModel::startCbtExam,
                        onToggleCourseEnrollment = viewModel::toggleCourseEnrollment,
                        onOpenAllTests = { currentTab = MainTab.TESTS },
                        onOpenBlogs = { currentTab = MainTab.BLOGS },
                        modifier = Modifier.weight(1f)
                    )
                    MainTab.TESTS -> TestsHubScreen(
                        language = language,
                        tests = allTests,
                        attempts = attempts,
                        onStartTest = viewModel::startCbtExam,
                        modifier = Modifier.weight(1f)
                    )
                    MainTab.BLOGS -> BlogsScreen(
                        language = language,
                        blogs = allBlogs,
                        bookmarkedBlogIds = bookmarkedBlogIds,
                        onToggleBookmark = viewModel::toggleBlogBookmark,
                        onOpenBlog = viewModel::openBlog,
                        modifier = Modifier.weight(1f)
                    )
                    MainTab.ADMIN -> AdminDashboardScreen(
                        language = language,
                        currentUserId = currentUserId,
                        isPublishing = isPublishing,
                        cloudTests = allTests,
                        cloudBlogs = allBlogs,
                        onPublishTest = viewModel::publishNewMockTest,
                        onPublishBlog = viewModel::publishNewStudyBlog,
                        onDeleteTest = viewModel::deleteUploadedTest,
                        onDeleteBlog = viewModel::deleteUploadedBlog,
                        onShowMessage = viewModel::showMessage,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
