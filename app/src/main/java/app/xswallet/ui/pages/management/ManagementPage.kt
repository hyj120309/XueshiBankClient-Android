package app.xswallet.ui.pages.management

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.xswallet.ui.AppStrings

sealed class ManagementRoute(val route: String) {
    object Main : ManagementRoute("main")
    object AddUser : ManagementRoute("add_user")
    object ManageUser : ManagementRoute("manage_user")
    object AddStudent : ManagementRoute("add_student")
    object StudentList : ManagementRoute("student_list")
    object RecordAdd : ManagementRoute("record_add")
    object RecordList : ManagementRoute("record_list/{studentId}") {
        fun withStudentId(studentId: Int) = "record_list/$studentId"
    }
}

private val AnimationDuration = 350
private val AnimationEasing = FastOutSlowInEasing

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ManagementPage(
    isLoggedIn: Boolean,
    username: String,
    token: String,
    strings: AppStrings,
    isServerAvailable: Boolean
) {
    val isAdmin = username == "admin"
    val navController = rememberNavController()

    val animationSpec = tween<IntOffset>(
        durationMillis = AnimationDuration,
        easing = AnimationEasing
    )

    NavHost(
        navController = navController,
        startDestination = ManagementRoute.Main.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(
            route = ManagementRoute.Main.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = animationSpec
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = animationSpec
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = animationSpec
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = animationSpec
                )
            }
        ) {
            if (isAdmin) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "管理面板",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isServerAvailable) { navController.navigate(ManagementRoute.AddUser.route) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "添加用户", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isServerAvailable) { navController.navigate(ManagementRoute.ManageUser.route) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "管理用户", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "学生管理",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isServerAvailable) { navController.navigate(ManagementRoute.AddStudent.route) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "添加学生", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isServerAvailable) { navController.navigate(ManagementRoute.StudentList.route) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.List, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "学生列表", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isServerAvailable) { navController.navigate(ManagementRoute.RecordAdd.route) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "添加记录", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        composable(
            route = ManagementRoute.AddUser.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) }
        ) {
            AddUserScreen(
                onBack = { navController.popBackStack() },
                token = token,
                strings = strings,
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = ManagementRoute.ManageUser.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) }
        ) {
            ManageUserScreen(
                onBack = { navController.popBackStack() },
                token = token,
                strings = strings,
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = ManagementRoute.AddStudent.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) }
        ) {
            AddStudentScreen(
                onBack = { navController.popBackStack() },
                username = username,
                token = token,
                strings = strings,
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = ManagementRoute.StudentList.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) }
        ) {
            StudentListScreen(
                onBack = { navController.popBackStack() },
                username = username,
                token = token,
                strings = strings,
                onStudentClick = { studentId ->
                    navController.navigate(ManagementRoute.RecordList.withStudentId(studentId))
                },
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = ManagementRoute.RecordAdd.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) }
        ) {
            RecordAddScreen(
                onBack = { navController.popBackStack() },
                username = username,
                token = token,
                strings = strings,
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = ManagementRoute.RecordList.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = animationSpec) }
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            RecordListScreen(
                onBack = { navController.popBackStack() },
                username = username,
                token = token,
                strings = strings,
                initialStudentId = studentId,
                isServerAvailable = isServerAvailable
            )
        }
    }
}