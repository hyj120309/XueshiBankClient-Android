package app.xswallet

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xswallet.data.SecurePrefs
import app.xswallet.ui.LanguageManager
import app.xswallet.ui.components.LoginDialog
import app.xswallet.ui.components.MaterialExpressiveLoading
import app.xswallet.ui.navigation.AppPage
import app.xswallet.ui.navigation.navigationItems
import app.xswallet.ui.pages.*
import app.xswallet.ui.pages.management.ManagementPage
import app.xswallet.ui.pages.settings.SettingsPage
import app.xswallet.ui.pages.settings.navigation.SettingsRoute
import app.xswallet.ui.theme.ThemeManager
import app.xswallet.ui.theme.XSWalletTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager.init(this)

        val window = window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            XSWalletTheme(
                darkTheme = ThemeManager.isDarkMode,
                useDynamicColor = ThemeManager.useDynamicColor
            ) {
                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as android.app.Activity).window
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SharedTransitionLayout {
                        WalletAppContent()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun SharedTransitionScope.WalletAppContent() {
    val context = LocalContext.current
    val strings = LanguageManager.strings
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var isServerAvailable by remember { mutableStateOf<Boolean?>(null) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUsername by remember { mutableStateOf("") }
    var currentToken by remember { mutableStateOf("") }
    var isAutoLoggingIn by remember { mutableStateOf(true) }

    var showLoginDialog by remember { mutableStateOf(false) }

    var showServerUnavailableDialog by remember { mutableStateOf(false) }

    val topBarHeight = 12.dp

    var selectedPage by remember { mutableStateOf(AppPage.OVERVIEW) }
    var isLoading by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var settingsInitialRoute by remember { mutableStateOf(SettingsRoute.Main.route) }

    var backPressedTime by remember { mutableStateOf(0L) }

    var queryInitialStudentId by remember { mutableStateOf("") }

    val menuWidth by animateDpAsState(
        targetValue = if (isMenuExpanded) 280.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "menuWidth"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (isMenuExpanded) 0.4f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "overlayAlpha"
    )

    suspend fun checkServerStatus(): Boolean = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/status"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            responseCode == 200
        } catch (e: Exception) {
            false
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun performAutoLogin(savedUsername: String, savedPassword: String): String? = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/login?usrname=${URLEncoder.encode(savedUsername, "UTF-8")}&passwd=${URLEncoder.encode(savedPassword, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }.trim()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    LaunchedEffect(Unit) {
        val serverOk = checkServerStatus()
        isServerAvailable = serverOk
        if (!serverOk) {
            showServerUnavailableDialog = true
        }
        if (serverOk) {
            val (savedUsername, savedPassword) = SecurePrefs.getUser(context)
            if (savedUsername != null && savedPassword != null) {
                val token = performAutoLogin(savedUsername, savedPassword)
                if (token != null) {
                    isLoggedIn = true
                    currentUsername = savedUsername
                    currentToken = token
                }
            }
        }
        isAutoLoggingIn = false
    }


    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()

        if (isMenuExpanded) {
            isMenuExpanded = false
            return@BackHandler
        }
        if (selectedPage == AppPage.SETTINGS) {
            selectedPage = AppPage.OVERVIEW
            return@BackHandler
        }
        if (backPressedTime + 2000 > currentTime) {
            (context as? Activity)?.finish()
        } else {
            Toast.makeText(context, strings.exitMessage, Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        }
    }

    val pageTitle = when (selectedPage) {
        AppPage.OVERVIEW -> "公告"
        AppPage.QUERY -> "查询"
        AppPage.MANAGEMENT -> "管理"
        AppPage.SETTINGS -> "设置"
    }

    if (isAutoLoggingIn || isServerAvailable == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MaterialExpressiveLoading(modifier = Modifier.size(64.dp))
        }
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(top = topBarHeight),
                    navigationIcon = {
                        IconButton(
                            onClick = { isMenuExpanded = !isMenuExpanded }
                        ) {
                            Icon(
                                if (isMenuExpanded) Icons.Filled.ArrowBack
                                else Icons.Filled.Menu,
                                contentDescription = if (isMenuExpanded) strings.back else "菜单",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {}
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = topBarHeight)
                ) {
                    AnimatedContent(
                        targetState = selectedPage,
                        transitionSpec = {
                            if (targetState.ordinal > initialState.ordinal) {
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(
                                        durationMillis = 350,
                                        easing = FastOutSlowInEasing
                                    )
                                ) togetherWith
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> -fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                            } else {
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(
                                        durationMillis = 350,
                                        easing = FastOutSlowInEasing
                                    )
                                ) togetherWith
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                            }
                        },
                        label = "PageTransition"
                    ) { page ->
                        when (page) {
                            AppPage.OVERVIEW -> OverviewPage(
                                isLoggedIn = isLoggedIn,
                                username = currentUsername,
                                token = currentToken,
                                strings = strings,
                                isServerAvailable = isServerAvailable == true,
                                viewModel = viewModel()
                            )
                            AppPage.QUERY -> QueryPage(
                                isLoggedIn = isLoggedIn,
                                username = currentUsername,
                                token = currentToken,
                                strings = strings,
                                isServerAvailable = isServerAvailable == true,
                                initialStudentId = queryInitialStudentId,
                                onSearchPerformed = {
                                    queryInitialStudentId = ""
                                }
                            )
                            AppPage.MANAGEMENT -> ManagementPage(
                                isLoggedIn = isLoggedIn,
                                username = currentUsername,
                                token = currentToken,
                                strings = strings,
                                isServerAvailable = isServerAvailable == true
                            )
                            AppPage.SETTINGS -> SettingsPage(
                                onBack = { selectedPage = AppPage.OVERVIEW },
                                onLogout = {
                                    SecurePrefs.clearUser(context)
                                    isLoggedIn = false
                                    currentUsername = ""
                                    currentToken = ""
                                    showLoginDialog = true
                                },
                                isLoggedIn = isLoggedIn,
                                onShowLogin = { showLoginDialog = true },
                                username = currentUsername,
                                token = currentToken,
                                initialRoute = settingsInitialRoute,
                                strings = strings,
                                isServerAvailable = isServerAvailable == true
                            )
                        }
                    }
                }

                if (isMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = overlayAlpha))
                            .clickable { isMenuExpanded = false }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(menuWidth)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(
                                topEnd = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(
                                topEnd = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(top = topBarHeight),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (menuWidth > 0.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "学士银行",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            navigationItems(strings).forEachIndexed { index, item ->
                                val page = when (index) {
                                    0 -> AppPage.OVERVIEW
                                    1 -> AppPage.QUERY
                                    2 -> AppPage.MANAGEMENT
                                    3 -> AppPage.SETTINGS
                                    else -> AppPage.OVERVIEW
                                }

                                val enabled = if (page == AppPage.MANAGEMENT) {
                                    isLoggedIn && (isServerAvailable == true)
                                } else if (page == AppPage.QUERY) {
                                    isServerAvailable == true
                                } else {
                                    true
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(enabled = enabled) {
                                            if (page == AppPage.SETTINGS) {
                                                settingsInitialRoute = SettingsRoute.Main.route
                                            }
                                            selectedPage = page
                                            isMenuExpanded = false
                                        }
                                        .background(
                                            if (selectedPage == page) MaterialTheme.colorScheme.primaryContainer
                                            else Color.Transparent
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            item.icon,
                                            contentDescription = item.title,
                                            modifier = Modifier.size(24.dp),
                                            tint = if (!enabled) {
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            } else if (selectedPage == page) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            }
                                        )
                                        Box(
                                            modifier = Modifier.width(180.dp)
                                        ) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = if (selectedPage == page) FontWeight.Bold else FontWeight.Normal,
                                                color = if (!enabled) {
                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                                } else if (selectedPage == page) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                },
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp)
                                    .clickable(
                                        enabled = isServerAvailable == true,
                                        onClick = {
                                            if (isLoggedIn) {
                                                selectedPage = AppPage.SETTINGS
                                                settingsInitialRoute = SettingsRoute.AccountSecurity.route
                                                isMenuExpanded = false
                                            } else {
                                                if (isServerAvailable == true) {
                                                    showLoginDialog = true
                                                } else {
                                                    Toast.makeText(context, "服务器不可用", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = strings.account,
                                        modifier = Modifier.size(36.dp),
                                        tint = if (isServerAvailable == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (isLoggedIn) currentUsername else "未登录",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            color = if (isServerAvailable == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                        )
                                        Text(
                                            text = if (isLoggedIn) strings.loggedIn else "点击登录",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isServerAvailable == true) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBarHeight)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )

                if (showServerUnavailableDialog) {
                    AlertDialog(
                        onDismissRequest = { showServerUnavailableDialog = false },
                        title = { Text("提示") },
                        text = { Text("服务器不可用，部分功能将受限。") },
                        confirmButton = {
                            TextButton(onClick = { showServerUnavailableDialog = false }) {
                                Text("确定")
                            }
                        }
                    )
                }

                if (showLoginDialog) {
                    LoginDialog(
                        onDismiss = { showLoginDialog = false },
                        onLoginSuccess = { username, token ->
                            isLoggedIn = true
                            currentUsername = username
                            currentToken = token
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=320")
@Composable
fun WalletAppPreview() {
    XSWalletTheme {
        SharedTransitionLayout {
            WalletAppContent()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=320")
@Composable
fun WalletAppPortraitPreview() {
    XSWalletTheme {
        SharedTransitionLayout {
            WalletAppContent()
        }
    }
}