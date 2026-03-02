package app.xswallet.ui.pages.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.xswallet.ui.AppStrings
import app.xswallet.ui.pages.settings.screens.AboutScreen
import app.xswallet.ui.pages.settings.screens.AccountSecurityScreen
import app.xswallet.ui.pages.settings.screens.AppSettingsScreen
import app.xswallet.ui.pages.settings.screens.MainSettingsScreen
import app.xswallet.ui.pages.settings.screens.ToolboxScreen

sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("main")
    object AccountSecurity : SettingsRoute("account_security")
    object AppSettings : SettingsRoute("app_settings")
    object Toolbox : SettingsRoute("toolbox")
    object About : SettingsRoute("about")
}

val AnimationDuration = 350
val AnimationEasing = FastOutSlowInEasing

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun SharedTransitionScope.SettingsPage(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isLoggedIn: Boolean,
    onShowLogin: () -> Unit,
    username: String,
    token: String,
    initialRoute: String = SettingsRoute.Main.route,
    strings: AppStrings
) {
    val navController = rememberNavController()

    BackHandler {
        if (!navController.popBackStack()) {
            onBack()
        }
    }

    LaunchedEffect(initialRoute) {
        if (initialRoute != SettingsRoute.Main.route) {
            navController.popBackStack(SettingsRoute.Main.route, inclusive = false)
            navController.navigate(initialRoute)
        } else {
            navController.popBackStack(SettingsRoute.Main.route, inclusive = false)
        }
    }

    val animationSpec = tween<IntOffset>(
        durationMillis = AnimationDuration,
        easing = AnimationEasing
    )

    NavHost(
        navController = navController,
        startDestination = SettingsRoute.Main.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(
            route = SettingsRoute.Main.route,
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
            MainSettingsScreen(
                onNavigate = { route -> navController.navigate(route) },
                strings = strings,
                isLoggedIn = isLoggedIn,
                onShowLogin = onShowLogin
            )
        }

        composable(
            route = SettingsRoute.AccountSecurity.route,
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
            AccountSecurityScreen(
                onBack = {
                    if (!navController.popBackStack()) {
                        onBack()
                    }
                },
                onLogout = onLogout,
                isLoggedIn = isLoggedIn,
                onShowLogin = onShowLogin,
                username = username,
                token = token,
                strings = strings
            )
        }

        composable(
            route = SettingsRoute.AppSettings.route,
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
            AppSettingsScreen(
                onBack = {
                    if (!navController.popBackStack()) {
                        onBack()
                    }
                },
                strings = strings
            )
        }

        composable(
            route = SettingsRoute.Toolbox.route,
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
            ToolboxScreen(
                onBack = {
                    if (!navController.popBackStack()) {
                        onBack()
                    }
                },
                strings = strings
            )
        }

        composable(
            route = SettingsRoute.About.route,
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
            AboutScreen(
                onBack = {
                    if (!navController.popBackStack()) {
                        onBack()
                    }
                },
                strings = strings
            )
        }
    }
}