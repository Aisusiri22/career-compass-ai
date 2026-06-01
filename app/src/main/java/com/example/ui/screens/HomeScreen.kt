package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.ProfileEntity
import com.example.data.db.SkillProgressEntity
import com.example.data.model.CareerKnowledgeBase
import com.example.data.model.CareerPathDetail
import com.example.ui.theme.*
import com.example.ui.viewmodel.CareerCompassViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: CareerCompassViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val activePath by viewModel.activeCareerPath.collectAsStateWithLifecycle()
    val progressPercentage by viewModel.activePathProgressPercentage.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.currentTab.value = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SlateDark, SlateDark.copy(alpha = 0.9f))
                    )
                )
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "TabTransition"
            ) { tab ->
                when (tab) {
                    0 -> DashboardTab(viewModel = viewModel, profile = profile, activePath = activePath, progress = progressPercentage)
                    1 -> ExploreTab(viewModel = viewModel, activePath = activePath)
                    2 -> RecommendationTab(viewModel = viewModel)
                    3 -> RoadmapTab(viewModel = viewModel, activePath = activePath, progress = progressPercentage)
                    4 -> MentorTab(viewModel = viewModel)
                }
            }
        }
    }
}

// --- Custom Bottom Navigation Bar with safe drawing guidelines ---
@Composable
fun CustomBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("app_navigation_bar"),
        containerColor = SlateSurface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem("Profile", Icons.Default.Person, Icons.Outlined.Person, "profile_tab"),
            NavigationItem("Explore", Icons.Default.Map, Icons.Outlined.Map, "explore_tab"),
            NavigationItem("Compass", Icons.Default.Explore, Icons.Outlined.Explore, "compass_tab"),
            NavigationItem("Roadmap", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth, "roadmap_tab"),
            NavigationItem("AI Mentor", Icons.Default.QuestionAnswer, Icons.Outlined.QuestionAnswer, "mentor_tab")
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (selectedTab == index) TechCyan else SlateBody
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selectedTab == index) TechCyanSecondary else SlateBody,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SlateSurfaceLight
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

// --- TAB 0: DASHBOARD TAB ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardTab(
    viewModel: CareerCompassViewModel,
    profile: ProfileEntity?,
    activePath: String,
    progress: Int
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val studyPlan by viewModel.activeStudyPlan.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val subtitleText = if (profile != null) {
                        "${profile.branch.split(" ").firstOrNull() ?: "ECE"} • ${profile.collegeYear}"
                    } else {
                        "ECE • Year 2"
                    }
                    Text(
                        text = subtitleText.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = TechCyan // Premium Indigo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Career Compass",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = SlateHeading
                    )
                }
                
                val initials = if (profile != null && profile.name.isNotBlank()) {
                    profile.name.split(" ").filter { it.isNotBlank() }.map { it.take(1) }.joinToString("").uppercase().take(2)
                } else {
                    "AJ"
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(TechCyan.copy(alpha = 0.12f), CircleShape)
                        .border(1.5.dp, TechCyan.copy(alpha = 0.3f), CircleShape)
                        .clickable { showEditDialog = true }
                        .testTag("edit_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Profile details card
        item {
            if (profile != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SlateSurfaceLight)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(TechCyan.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Academic",
                                    tint = TechCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateHeading
                                )
                                Text(
                                    text = "${profile.branch} • ${profile.collegeYear}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SlateBody
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = SlateSurfaceLight
                        )

                        Text(
                            text = "Interests",
                            style = MaterialTheme.typography.labelLarge,
                            color = TechCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = profile.interests,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateHeading,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "My Current Skills",
                            style = MaterialTheme.typography.labelLarge,
                            color = TechCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = profile.skills,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateHeading,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Career Goals",
                            style = MaterialTheme.typography.labelLarge,
                            color = TechCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = profile.goals,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateHeading
                        )
                    }
                }
            }
        }

        // Target track outline progress (Refined into Recommended Match Card design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.currentTab.value = 3 }, // Go to Roadmap tab
                colors = CardDefaults.cardColors(containerColor = TechCyan), // High-fidelity bg-indigo-600
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "TOP RECOMMENDATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = activePath,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 28.sp
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    val matchPercentText = if (activePath == "Embedded Systems") "95" else "$progress"
                                    Text(
                                        text = matchPercentText,
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp
                                    )
                                    Text(
                                        text = "%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Text(
                                    text = "AI Match",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val interestPhrase = if (profile != null) profile.interests else "Engineering"
                        Text(
                            text = "Your interest in $interestPhrase aligns beautifully with core industry requirements for $activePath domain.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("career_progress_bar"),
                            color = TechEmerald,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.currentTab.value = 3 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = TechCyan
                            )
                        ) {
                            Text(
                                text = "View Career Details",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // Active Study Plan Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SlateSurfaceLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Schedule",
                                tint = TechCyanSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personalised Study Schedule",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateHeading
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (studyPlan != null) {
                        Text(
                            text = "Daily Goal: ${studyPlan!!.dailyHours} Hours",
                            style = MaterialTheme.typography.titleSmall,
                            color = TechAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = studyPlan!!.dailyPlan,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateHeading,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🎓 Access full weekly and monthly routines in the 'Roadmap' tab.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TechCyanSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No active study routines created.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateBody
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.currentTab.value = 3 },
                                colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                                modifier = Modifier.testTag("generate_schedule_direct_button")
                            ) {
                                Text("Set Study Hours & Generate")
                            }
                        }
                    }
                }
            }
        }

        // Quick AI Action (Matching design HTML's modern accent action)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.currentTab.value = 4 }, // Go to AI Mentor tab
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // bg-slate-900 style
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TechCyanSecondary, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "AI Action",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Ask AI Mentor",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "\"Which VLSI tools should I learn first?\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Navigate to AI Mentor",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // --- Profile Edit Dialog ---
    if (showEditDialog && profile != null) {
        var editName by remember { mutableStateOf(profile.name) }
        var editBranch by remember { mutableStateOf(profile.branch) }
        var editYear by remember { mutableStateOf(profile.collegeYear) }
        var editInterests by remember { mutableStateOf(profile.interests) }
        var editSkills by remember { mutableStateOf(profile.skills) }
        var editGoals by remember { mutableStateOf(profile.goals) }

        val branchesList = listOf(
            "Electronics & Communication Engineering",
            "Computer Science & Engineering",
            "Electrical Engineering",
            "Mechanical Engineering"
        )
        val yearsList = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "Post Graduation")

        var branchExpanded by remember { mutableStateOf(false) }
        var yearExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Student Profile",
                    fontWeight = FontWeight.Bold,
                    color = SlateHeading
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SlateSurfaceLight,
                            unfocusedContainerColor = SlateSurface
                        )
                    )

                    // Branch dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { branchExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateHeading)
                        ) {
                            Text(editBranch, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                        DropdownMenu(
                            expanded = branchExpanded,
                            onDismissRequest = { branchExpanded = false },
                            modifier = Modifier.background(SlateSurface)
                        ) {
                            branchesList.forEach { branch ->
                                DropdownMenuItem(
                                    text = { Text(branch, color = SlateHeading) },
                                    onClick = {
                                        editBranch = branch
                                        branchExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Year dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { yearExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateHeading)
                        ) {
                            Text(editYear, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                        DropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false },
                            modifier = Modifier.background(SlateSurface)
                        ) {
                            yearsList.forEach { yr ->
                                DropdownMenuItem(
                                    text = { Text(yr, color = SlateHeading) },
                                    onClick = {
                                        editYear = yr
                                        yearExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    TextField(
                        value = editInterests,
                        onValueChange = { editInterests = it },
                        label = { Text("Interests") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SlateSurfaceLight,
                            unfocusedContainerColor = SlateSurface
                        )
                    )

                    TextField(
                        value = editSkills,
                        onValueChange = { editSkills = it },
                        label = { Text("Already Acquired Skills") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SlateSurfaceLight,
                            unfocusedContainerColor = SlateSurface
                        )
                    )

                    TextField(
                        value = editGoals,
                        onValueChange = { editGoals = it },
                        label = { Text("Career Goals") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SlateSurfaceLight,
                            unfocusedContainerColor = SlateSurface
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = SlateBody)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            name = editName,
                            branch = editBranch,
                            year = editYear,
                            interests = editInterests,
                            skills = editSkills,
                            goals = editGoals
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save Changes")
                }
            },
            containerColor = SlateSurface
        )
    }
}

// --- TAB 1: EXPLORE TAB (Branch Explorer, Comparer, Future Me) ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreTab(
    viewModel: CareerCompassViewModel,
    activePath: String
) {
    var selectedBranchId by remember { mutableStateOf("ECE") }
    val branches = CareerKnowledgeBase.branches
    val availablePaths = CareerKnowledgeBase.getPathsForBranch(selectedBranchId)
    var selectedPathDetail by remember { mutableStateOf<CareerPathDetail?>(null) }
    var showSection by remember { mutableStateOf(0) } // 0: Explore, 1: Comparer, 2: Future Me

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Section selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateSurface, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val sections = listOf("Branch Explorer", "Career Comparer", "Future Me")
            sections.forEachIndexed { idx, label ->
                val isSelected = showSection == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SlateSurfaceLight else Color.Transparent)
                        .clickable {
                            showSection = idx
                            // reset selection if we swap sections
                            if (idx != 0) selectedPathDetail = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TechCyanSecondary else SlateBody,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (showSection) {
            0 -> {
                // BRANCH EXPLORER
                Text(
                    text = "Select Engineering Branch:",
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateBody,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable row of branches
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    branches.forEach { br ->
                        val isSelected = selectedBranchId == br.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TechCyan else SlateSurface)
                                .clickable {
                                    selectedBranchId = br.id
                                    selectedPathDetail = null
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = br.id,
                                color = if (isSelected) Color.White else SlateHeading,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedPathDetail == null) {
                    // List available careers
                    Text(
                        text = "Specialized Careers in $selectedBranchId:",
                        style = MaterialTheme.typography.titleMedium,
                        color = TechCyanSecondary,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(availablePaths) { path ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPathDetail = path }
                                    .testTag("explore_path_card_${path.id}"),
                                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SlateSurfaceLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = path.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateHeading
                                        )
                                        Text(
                                            text = path.shortDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SlateBody,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForwardIos,
                                        contentDescription = "Details",
                                        tint = TechCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Career details card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { selectedPathDetail = null },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, "Back", tint = TechCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Back to Paths", color = TechCyan)
                            }

                            Button(
                                onClick = {
                                    viewModel.activeCareerPath.value = selectedPathDetail!!.id
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activePath == selectedPathDetail!!.id) TechEmerald else TechCyan
                                )
                            ) {
                                Text(
                                    text = if (activePath == selectedPathDetail!!.id) "Active Track" else "Set Active Target",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        val detail = selectedPathDetail!!

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = detail.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = SlateHeading
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick info row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            BadgeItem("Salary: ${detail.averageSalary}", TechAmber)
                            BadgeItem("Diff: ${detail.difficulty}", TechCyan)
                            BadgeItem("Growth: ${detail.growthPotential}", TechEmerald)
                        }

                        SectionBlock("What is it?", detail.whatIsIt)
                        SectionBlock("What work do engineers do?", detail.whatTheyDo)

                        // Required Skills list
                        Text(
                            text = "Required Skills:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TechCyanSecondary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                        )
                        detail.skills.forEach { sk ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Skill Bullet",
                                    tint = TechCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(sk, style = MaterialTheme.typography.bodyMedium, color = SlateHeading)
                            }
                        }

                        // Pros & Cons
                        SectionList("Advantages", detail.advantages, TechEmerald, Icons.Default.CheckCircle)
                        SectionList("Challenges & Hurdles", detail.challenges, TechAmber, Icons.Default.Warning)

                        SectionBlock("Future Scope & Dynamics", detail.futureScope)

                        // Companies & Roles
                        SectionList("Top Recruits & Hirers", detail.topCompanies, TechCyan, Icons.Default.Business)
                        SectionList("Typical Roles", detail.typicalRoles, TechCyanSecondary, Icons.Default.Work)

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
            1 -> {
                // CAREER COMPARER SIDE BY SIDE
                val p1 by viewModel.comparePath1.collectAsStateWithLifecycle()
                val p2 by viewModel.comparePath2.collectAsStateWithLifecycle()

                val itemsNames = CareerKnowledgeBase.careerDetails.keys.toList()

                var expand1 by remember { mutableStateOf(false) }
                var expand2 by remember { mutableStateOf(false) }

                Text(
                    text = "Compare Specialized Engineering Sectors:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateHeading
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Career selector 1
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { expand1 = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateSurface)
                        ) {
                            Text(p1, maxLines = 1, overflow = TextOverflow.Ellipsis, color = SlateHeading)
                        }
                        DropdownMenu(
                            expanded = expand1,
                            onDismissRequest = { expand1 = false },
                            modifier = Modifier.background(SlateSurface)
                        ) {
                            itemsNames.forEach { nm ->
                                DropdownMenuItem(
                                    text = { Text(nm, color = SlateHeading) },
                                    onClick = {
                                        viewModel.comparePath1.value = nm
                                        expand1 = false
                                    }
                                )
                            }
                        }
                    }

                    // Career selector 2
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { expand2 = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateSurface)
                        ) {
                            Text(p2, maxLines = 1, overflow = TextOverflow.Ellipsis, color = SlateHeading)
                        }
                        DropdownMenu(
                            expanded = expand2,
                            onDismissRequest = { expand2 = false },
                            modifier = Modifier.background(SlateSurface)
                        ) {
                            itemsNames.forEach { nm ->
                                DropdownMenuItem(
                                    text = { Text(nm, color = SlateHeading) },
                                    onClick = {
                                        viewModel.comparePath2.value = nm
                                        expand2 = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comparison grid results
                val cd1 = CareerKnowledgeBase.careerDetails[p1]
                val cd2 = CareerKnowledgeBase.careerDetails[p2]

                if (cd1 != null && cd2 != null) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            ComparisonRow("Average Salary", cd1.averageSalary, cd2.averageSalary)
                        }
                        item {
                            ComparisonRow("Difficulty", cd1.difficulty, cd2.difficulty)
                        }
                        item {
                            ComparisonRow("Jobs Availability", cd1.jobAvailability, cd2.jobAvailability)
                        }
                        item {
                            ComparisonRow("Growth Potential", cd1.growthPotential, cd2.growthPotential)
                        }
                        item {
                            ComparisonRow("Work-Life Balance", cd1.workLifeBalance, cd2.workLifeBalance)
                        }
                        item {
                            ComparisonRow("Required Core Skills", cd1.skills.take(3).joinToString(", "), cd2.skills.take(3).joinToString(", "))
                        }
                        item {
                            ComparisonRow("Primary Advantage", cd1.advantages.firstOrNull() ?: "", cd2.advantages.firstOrNull() ?: "")
                        }
                    }
                }
            }
            2 -> {
                // FUTURE ME SIMULATOR
                val milestones = CareerKnowledgeBase.milestones[activePath]

                Text(
                    text = "Future Me Simulator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = SlateHeading
                )
                Text(
                    text = "Simulate my professional milestones on $activePath track:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateBody
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (milestones != null) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(milestones) { idx, item ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Timeline bullet bar
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(TechCyan.copy(alpha = 0.2f), CircleShape)
                                            .border(1.dp, TechCyanSecondary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (idx + 1).toString(),
                                            color = TechCyanSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (idx < milestones.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(90.dp)
                                                .background(SlateSurfaceLight)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SlateSurface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = item.timeframe,
                                                color = TechAmber,
                                                fontWeight = FontWeight.Black,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                        Text(
                                            text = item.title,
                                            color = SlateHeading,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.description,
                                            color = SlateBody,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Skills to have: ${item.skillsAcquired.joinToString(", ")}",
                                            color = TechCyanSecondary,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Target Project: ${item.targetProjects.joinToString(", ")}",
                                            color = TechEmerald,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Explore other paths to generate custom milestone simulations.", color = SlateBody)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItem(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun SectionBlock(title: String, text: String) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TechCyanSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = SlateHeading,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun SectionList(title: String, items: List<String>, color: Color, bullet: ImageVector) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(6.dp))
        items.forEach { it ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = bullet,
                    contentDescription = "Bullet",
                    tint = color,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateHeading
                )
            }
        }
    }
}

@Composable
fun ComparisonRow(attribute: String, left: String, right: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = BorderStroke(1.dp, SlateSurfaceLight)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = attribute,
                fontWeight = FontWeight.Bold,
                color = TechCyanSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = left,
                    modifier = Modifier.weight(1f),
                    color = SlateHeading,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(SlateSurfaceLight)
                )
                Text(
                    text = right,
                    modifier = Modifier.weight(1f),
                    color = SlateHeading,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// --- TAB 2: RECOMMENDATION TAB (Assessment Engine) ---
@Composable
fun RecommendationTab(viewModel: CareerCompassViewModel) {
    val results by viewModel.recommendedPaths.collectAsStateWithLifecycle()
    val isLoading by viewModel.isRecommendationLoading.collectAsStateWithLifecycle()

    val c by viewModel.interestCoding.collectAsStateWithLifecycle()
    val el by viewModel.interestElectronics.collectAsStateWithLifecycle()
    val hd by viewModel.interestHardware.collectAsStateWithLifecycle()
    val res by viewModel.interestResearch.collectAsStateWithLifecycle()
    val m by viewModel.interestMath.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Career Suitability Compass",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = SlateHeading
            )
            Text(
                text = "Assess your personal preferences dynamically to determine the perfect specialized engineering domain for you:",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateBody
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, SlateSurfaceLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InterestToggle("Do you enjoy designing software and programming code?", c) { viewModel.interestCoding.value = it }
                    InterestToggle("Do you enjoy circuit components and signal waveforms?", el) { viewModel.interestElectronics.value = it }
                    InterestToggle("Do you like hands-on custom electronic circuit controllers?", hd) { viewModel.interestHardware.value = it }
                    InterestToggle("Do you enjoy algorithmic calculations and statistics?", m) { viewModel.interestMath.value = it }
                    InterestToggle("Are you interested in reading and publishing research papers?", res) { viewModel.interestResearch.value = it }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.runRecommendationEngine() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_assessment_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Calculate Domain Matches", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        if (results.isNotEmpty()) {
            item {
                Text(
                    text = "Recommendation Matches:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TechCyanSecondary
                )
            }

            items(results) { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(TechCyan.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${res.matchPercentage}%",
                                color = if (res.matchPercentage > 75) TechEmerald else TechCyanSecondary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = res.pathName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateHeading
                            )
                            Text(
                                text = res.reason,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateBody
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = {
                                    viewModel.activeCareerPath.value = res.pathName
                                    viewModel.currentTab.value = 0 // Return to dashboard
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Select as My Target Goal →", color = TechCyanSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InterestToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, modifier = Modifier.weight(1f), color = SlateHeading, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TechCyan,
                checkedTrackColor = TechCyan.copy(alpha = 0.4f)
            )
        )
    }
}

// --- TAB 3: ROADMAP & STUDY PLANNING TAB ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoadmapTab(
    viewModel: CareerCompassViewModel,
    activePath: String,
    progress: Int
) {
    val details by viewModel.activeCareerDetails.collectAsStateWithLifecycle()
    val skillsProgressList by viewModel.activePathSkills.collectAsStateWithLifecycle()
    val studyPlan by viewModel.activeStudyPlan.collectAsStateWithLifecycle()
    var inputHours by remember { mutableStateOf(4) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Syllabus Curriculum Roadmap",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = SlateHeading
            )
            Text(
                text = "Targeting Core Career: $activePath",
                style = MaterialTheme.typography.titleSmall,
                color = TechCyanSecondary,
                fontWeight = FontWeight.Bold
            )
        }

        // Checklist component for Progress Tracking
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Step-By-Step Skill Verification Checklists",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = TechEmerald
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mark acquired skillsets dynamically to update your placement verification index score ($progress% completed):",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateBody
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (skillsProgressList.isEmpty()) {
                        CircularProgressIndicator(color = TechCyanSecondary)
                    } else {
                        skillsProgressList.forEach { skill ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleSkillCompletion(skill.skillName, !skill.isCompleted) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = skill.isCompleted,
                                    onCheckedChange = { viewModel.toggleSkillCompletion(skill.skillName, it) },
                                    colors = CheckboxDefaults.colors(checkedColor = TechEmerald)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = skill.skillName,
                                    color = if (skill.isCompleted) TechEmerald else SlateHeading,
                                    fontWeight = if (skill.isCompleted) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Year by Year Roadmap
        item {
            Text(
                text = "Academic Milestones Year-by-Year:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateHeading
            )
        }

        items(details.roadmap) { rYear ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = rYear.year,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TechCyanSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Key Topics:",
                        fontWeight = FontWeight.Bold,
                        color = TechAmber,
                        fontSize = 13.sp
                    )
                    Text(
                        text = rYear.topics.joinToString(" • "),
                        color = SlateHeading,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Practical Actions:",
                        fontWeight = FontWeight.Bold,
                        color = TechEmerald,
                        fontSize = 13.sp
                    )
                    rYear.actions.forEach { action ->
                        Text(
                            text = "⚡ $action",
                            color = SlateBody,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Smart Study Planner Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStroke(1.dp, SlateSurfaceLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Smart Study Planner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TechCyanSecondary
                    )
                    Text(
                        text = "Specify available daily study hours to align customized targets:",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateBody
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (inputHours > 1) inputHours-- },
                            modifier = Modifier.background(SlateSurfaceLight, CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, "Decrease", tint = Color.White)
                        }
                        Text(
                            text = "$inputHours Hours / Day",
                            fontWeight = FontWeight.Bold,
                            color = SlateHeading,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { if (inputHours < 24) inputHours++ },
                            modifier = Modifier.background(SlateSurfaceLight, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, "Increase", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.generateStudyPlan(inputHours) },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_schedule_button")
                    ) {
                        Text("Construct Daily, Weekly & Monthly Routine Plans", fontWeight = FontWeight.Bold)
                    }

                    if (studyPlan != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = SlateSurfaceLight)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Daily Routine",
                            color = TechAmber,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(studyPlan!!.dailyPlan, color = SlateHeading, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Weekly Target Schedule",
                            color = TechCyanSecondary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(studyPlan!!.weeklyPlan, color = SlateHeading, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Monthly Milestones Layout",
                            color = TechEmerald,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(studyPlan!!.monthlyPlan, color = SlateHeading, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Step 9: Internship & Project Recommendations
        item {
            Text(
                text = "Placement Boost Recommendations:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateHeading
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Suggested Placement Capstones for $activePath:",
                        fontWeight = FontWeight.Bold,
                        color = TechCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val recommendations = when(activePath) {
                        "VLSI" -> listOf(
                            "Project: Hardware RTL pipelined 16-bit RISC processor designed on Verilog.",
                            "Certification: Intel FPGA design logic certifications.",
                            "Internship Target: Silicon/ASIC verify roles at Qualcomm/AMD."
                        )
                        "Embedded Systems" -> listOf(
                            "Project: FreeRTOS automated drone navigation controllers.",
                            "Certification: ARM processor developer certifications.",
                            "Internship Target: Firmware Internships at Robert Bosch / Tesla motors."
                        )
                        "IoT" -> listOf(
                            "Project: Solar telemetry agricultural sensor grids powered by AWS cloud databases.",
                            "Certification: AWS certified IoT Solution specialist certifications.",
                            "Internship Target: Connected networks Intern at CISCO."
                        )
                        "Robotics" -> listOf(
                            "Project: 2D SLAM navigation obstacle-mapping robot configurations on ROS packages.",
                            "Certification: ROS robotics foundational program degrees.",
                            "Internship Target: R&D mechatronic design intern at Boston Dynamics / ABB robotics."
                        )
                        "AI/ML" -> listOf(
                            "Project: Transformer language encoders fine-tuned to compile programming equations.",
                            "Certification: Google Advanced Machine Learning designer certificates.",
                            "Internship Target: Research Intern at Google DeepMind."
                        )
                        else -> listOf(
                            "Project: Secure multi-view web portals utilizing REST APIs and Room local databases.",
                            "Certification: Oracle certified Java developers or associate degrees.",
                            "Internship Target: Junior backend systems developer at Microsoft."
                        )
                    }

                    recommendations.forEach { item ->
                        Text(
                            text = "⚡ $item",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateHeading,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- TAB 4: AI MENTOR CHAT CHANNELS ---
@Composable
fun MentorTab(viewModel: CareerCompassViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to the bottom of message listings when new ones appear
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI Personal Mentor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = SlateHeading
                )
                Text(
                    text = "\"Choose the Right Path, Not Just Any Path\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = TechAmber,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { viewModel.clearChatHistory() },
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear History",
                    tint = SlateBody
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Feed Scrollable
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SlateSurface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(1.dp, SlateSurfaceLight, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = "Empty Chat",
                        tint = TechCyan,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your AI Career Mentor is ready!",
                        color = SlateHeading,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Consult me on chip design, firmware, career opportunities, roadmaps, or other engineering choices details.",
                        color = SlateBody,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Suggestion Chips
                    Text("Try asking:", color = TechCyanSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val queries = listOf(
                        "How do I start VLSI?",
                        "Is coding mandatory for Embedded?",
                        "Suggest high paying branches."
                    )
                    queries.forEach { q ->
                        Box(
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendChatMessage(q)
                                }
                                .background(SlateSurface, RoundedCornerShape(8.dp))
                                .border(1.dp, SlateSurfaceLight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .padding(bottom = 6.dp)
                        ) {
                            Text(q, color = SlateHeading, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.role == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isUser) 12.dp else 0.dp,
                                            bottomEnd = if (isUser) 0.dp else 12.dp
                                        )
                                    )
                                    .background(if (isUser) TechCyan else SlateSurface)
                                    .border(1.dp, if (isUser) TechCyanSecondary else SlateSurfaceLight, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                                    .widthIn(max = 260.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    color = SlateHeading,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(SlateSurface, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Mentor is drawing plans...",
                                        color = SlateBody,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Input Field Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Consult with AI Mentor...", color = SlateBody) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SlateSurface,
                    unfocusedContainerColor = SlateSurface,
                    focusedTextColor = SlateHeading,
                    unfocusedTextColor = SlateHeading
                ),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2
            )

            Button(
                onClick = {
                    if (inputMessage.trim().isNotEmpty()) {
                        viewModel.sendChatMessage(inputMessage)
                        inputMessage = ""
                    }
                },
                modifier = Modifier
                    .size(54.dp)
                    .testTag("send_chat_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}
