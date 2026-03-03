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
    strings: AppStrings,
    isServerAvailable: Boolean
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
        }
    }

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
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            }
        ) {
            MainSettingsScreen(
                onNavigate = { route -> navController.navigate(route) },
                strings = strings,
                isLoggedIn = isLoggedIn,
                onShowLogin = onShowLogin,
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = SettingsRoute.AccountSecurity.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
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
                strings = strings,
                isServerAvailable = isServerAvailable
            )
        }

        composable(
            route = SettingsRoute.AppSettings.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
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
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
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
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(AnimationDuration, easing = AnimationEasing)
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