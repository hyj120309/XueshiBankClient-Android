package app.xswallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

enum class AppLanguage(val code: String) {
    ZH_CN("zh-CN"),
    ZH_TW("zh-TW"),
    EN("en"),
    LZH("lzh")
}

interface AppStrings {
    val appName: String
    val back: String
    val close: String
    val confirm: String
    val cancel: String
    val search: String
    val settings: String
    val toolbox: String
    val version: String
    val management: String

    val overview: String
    val wallet: String
    val transactions: String
    val history: String
    val account: String
    val loggedIn: String

    val overviewTitle: String
    val overviewDesc1: String
    val overviewDesc2: String
    val totalAssets: String
    val thisMonth: String
    val pending: String
    val monthTransactions: String
    val status: String
    val menuStatus: String
    val expanded: String
    val collapsed: String
    val on: String
    val off: String
    val linearProgress: String
    val sliderPosition: String
    val clickCount: String

    val controlsTitle: String
    val android16SwitchTitle: String
    val securityMode: String
    val securityModeDescOn: String
    val securityModeDescOff: String
    val nightMode: String
    val nightModeDescOn: String
    val nightModeDescOff: String

    val interactionDemo: String
    val favorite: String
    val clickMe: String
    val resetCounter: String
    val simulateLoading: String

    val progressTitle: String
    val linearProgressBar: String
    val progressValue: String
    val increase10: String
    val slider: String
    val sliderValue: String
    val circularProgress: String
    val small: String
    val medium: String
    val loading: String

    val historyTitle: String
    val historyDesc: String
    val historyStats: String
    val totalTransactions: String
    val monthTransactionsCount: String
    val activeDays: String
    val recentActivity: String
    val createWallet: String
    val largeTransfer: String
    val addSecurity: String
    val updateSettings: String
    val firstLogin: String

    val transactionsTitle: String
    val transactionsDesc: String
    val transactionFilter: String
    val all: String
    val income: String
    val expense: String
    val recentTransactions: String
    val ethTransfer: String
    val btcPurchase: String
    val usdtDeposit: String
    val nftPurchase: String
    val tokenSwap: String

    val walletTitle: String
    val walletDesc: String
    val walletOverview: String
    val mainWallet: String
    val savingsWallet: String
    val investmentWallet: String
    val changeWallet: String
    val recentActivityLabel: String
    val quickActions: String
    val send: String
    val receive: String
    val exchange: String

    val settingsTitle: String
    val accountSecurity: String
    val accountSecurityDesc: String
    val appSettings: String
    val appSettingsDesc: String
    val toolboxDesc: String

    val accountSecurityTitle: String
    val securityStatus: String
    val securityStatusGood: String
    val securityStatusDesc: String
    val securityOptions: String
    val changePassword: String
    val changePasswordDesc: String
    val twoFactor: String
    val twoFactorDesc: String
    val deviceManagement: String
    val deviceManagementDesc: String
    val securityTips: String
    val securityTip1: String
    val securityTip2: String
    val securityTip3: String
    val securityTip4: String

    val appSettingsTitle: String
    val themeSettings: String
    val darkMode: String
    val darkModeDesc: String
    val dynamicColor: String
    val dynamicColorDesc: String
    val dynamicColorUnavailable: String
    val featureUnavailable: String
    val customThemeColor: String
    val accentColorHex: String
    val accentColorDesc: String
    val colorPreview: String
    val displaySettings: String
    val dpiScale: String
    val dpiScaleDesc: String
    val dpiExample: String
    val apply: String
    val currentDpi: String
    val dpiWarning: String
    val seconds: String
    val secretPage: String

    val toolboxTitle: String
    val toolboxDesc1: String
    val comingSoon: String
    val underDevelopment: String
    val moreToolsSoon: String
    val toolboxPlan: String
    val planItem1: String
    val planItem2: String
    val planItem3: String
    val planItem4: String
    val planItem5: String

    val searchTitle: String
    val searchPlaceholder: String
    val resultsFound: String
    val categoryPage: String
    val categorySettings: String
    val categoryFunction: String

    val exitMessage: String

    val languageNote: String
    val query: String
}

object ZhCnStrings : AppStrings {
    override val query = "查询"
    override val appName = "XSWallet"
    override val back = "返回"
    override val close = "关闭"
    override val confirm = "确定"
    override val cancel = "取消"
    override val search = "搜索"
    override val settings = "设置"
    override val toolbox = "工具箱"
    override val version = "XSWallet v1.0.0"
    override val management = "管理"

    override val overview = "概览"
    override val wallet = "钱包"
    override val transactions = "交易"
    override val history = "历史"
    override val account = "用户名称"
    override val loggedIn = "已登录"

    override val overviewTitle = "概览页面"
    override val overviewDesc1 = "欢迎来到 XSWallet 概览页面。"
    override val overviewDesc2 = "这里显示您的钱包总览和重要信息。"
    override val totalAssets = "总资产"
    override val thisMonth = "本月"
    override val pending = "笔待处理"
    override val monthTransactions = "本月交易"
    override val status = "当前状态"
    override val menuStatus = "菜单状态:"
    override val expanded = "已展开"
    override val collapsed = "已收起（完全隐藏）"
    override val on = "开启 ✓"
    override val off = "关闭 ✗"
    override val linearProgress = "线性进度:"
    override val sliderPosition = "滑块位置:"
    override val clickCount = "点击计数:"

    override val controlsTitle = "Android 16 风格 MD3 控件"
    override val android16SwitchTitle = "Android 16 风格开关 (MD3)"
    override val securityMode = "安全模式"
    override val securityModeDescOn = "打开时白圈内有勾号"
    override val securityModeDescOff = "关闭时白圈内有叉号"
    override val nightMode = "夜间模式"
    override val nightModeDescOn = "打开时白圈内有勾号"
    override val nightModeDescOff = "关闭时白圈内有叉号"

    override val interactionDemo = "交互演示"
    override val favorite = "喜爱"
    override val clickMe = "点击我"
    override val resetCounter = "重置计数器"
    override val simulateLoading = "模拟加载"

    override val progressTitle = "进度条和滑块控件"
    override val linearProgressBar = "线性进度条"
    override val progressValue = "进度值:"
    override val increase10 = "增加10%"
    override val slider = "滑块控件"
    override val sliderValue = "滑块值:"
    override val circularProgress = "圆形进度指示器"
    override val small = "小型"
    override val medium = "中型"
    override val loading = "加载中"

    override val historyTitle = "历史页面"
    override val historyDesc = "查看完整的历史记录和活动日志。"
    override val historyStats = "历史统计"
    override val totalTransactions = "总交易数"
    override val monthTransactionsCount = "本月交易"
    override val activeDays = "活跃天数"
    override val recentActivity = "最近活动时间线"
    override val createWallet = "创建新钱包"
    override val largeTransfer = "完成大额转账"
    override val addSecurity = "添加安全验证"
    override val updateSettings = "更新钱包设置"
    override val firstLogin = "首次登录应用"

    override val transactionsTitle = "交易页面"
    override val transactionsDesc = "查看和管理您的交易记录。"
    override val transactionFilter = "交易过滤器"
    override val all = "全部"
    override val income = "收入"
    override val expense = "支出"
    override val recentTransactions = "最近交易"
    override val ethTransfer = "ETH 转账"
    override val btcPurchase = "BTC 购买"
    override val usdtDeposit = "USDT 存入"
    override val nftPurchase = "NFT 购买"
    override val tokenSwap = "代币兑换"

    override val walletTitle = "钱包页面"
    override val walletDesc = "管理您的加密资产和钱包。"
    override val walletOverview = "钱包总览"
    override val mainWallet = "主要钱包"
    override val savingsWallet = "储蓄钱包"
    override val investmentWallet = "投资钱包"
    override val changeWallet = "零钱钱包"
    override val recentActivityLabel = "最近活动:"
    override val quickActions = "快速操作"
    override val send = "发送"
    override val receive = "接收"
    override val exchange = "兑换"

    override val settingsTitle = "设置"
    override val accountSecurity = "账号设置"
    override val accountSecurityDesc = "管理您的账号和安全设置"
    override val appSettings = "应用设置"
    override val appSettingsDesc = "更改应用的外观等设置"
    override val toolboxDesc = "各种实用工具"

    override val accountSecurityTitle = "账号安全"
    override val securityStatus = "安全状态："
    override val securityStatusGood = "良好"
    override val securityStatusDesc = "您的账号安全设置已基本配置完成。"
    override val securityOptions = "安全选项"
    override val changePassword = "修改密码"
    override val changePasswordDesc = "定期更换密码提高安全性"
    override val twoFactor = "两步验证"
    override val twoFactorDesc = "未开启，建议启用"
    override val deviceManagement = "登录设备管理"
    override val deviceManagementDesc = "查看和管理已登录的设备"
    override val securityTips = "安全提示"
    override val securityTip1 = "• 定期更新密码，避免使用简单密码"
    override val securityTip2 = "• 启用两步验证提高安全性"
    override val securityTip3 = "• 不要在公共设备上保持登录状态"
    override val securityTip4 = "• 注意识别钓鱼网站和诈骗信息"

    override val appSettingsTitle = "应用设置"
    override val themeSettings = "主题设置"
    override val darkMode = "深色模式"
    override val darkModeDesc = "切换到深色主题"
    override val dynamicColor = "动态取色"
    override val dynamicColorDesc = "使用 Android 12+ 系统主题颜色"
    override val dynamicColorUnavailable = "需要 Android 12+ 系统"
    override val featureUnavailable = "功能不可用"
    override val customThemeColor = "自定义主题颜色"
    override val accentColorHex = "强调色 (HEX值)"
    override val accentColorDesc = "关闭动态取色后，可在此输入HEX颜色值（如#FF5722）"
    override val colorPreview = "颜色预览"
    override val displaySettings = "显示设置"
    override val dpiScale = "显示密度 (缩放大小)"
    override val dpiScaleDesc = "调整应用界面缩放大小，如果太大了点击不到按钮可以调小一些，范围：0.5 - 2.0 (默认: 1.0)"
    override val dpiExample = "默认是1.0，建议一次调整的跨度要小一些，以免太小了啥都点不到"
    override val apply = "应用"
    override val currentDpi = "当前DPI:"
    override val dpiWarning = "请输入有效的DPI缩放值 (0.5 - 2.0)"
    override val seconds = "秒"
    override val secretPage = "秘密页面"

    override val toolboxTitle = "工具箱"
    override val toolboxDesc1 = "这些功能你可能一辈子都用不到，但要用的时候还是有用的"
    override val comingSoon = "没了"
    override val underDevelopment = "功能开发中"
    override val moreToolsSoon = "是的，没有了"
    override val toolboxPlan = "工具箱功能规划"
    override val planItem1 = "• 地址簿管理"
    override val planItem2 = "• 交易签名工具"
    override val planItem3 = "• 助记词校验"
    override val planItem4 = "• 网络状态检测"
    override val planItem5 = "• 数据备份工具"

    override val searchTitle = "搜索"
    override val searchPlaceholder = "搜索功能..."
    override val resultsFound = "找到 %d 个结果"
    override val categoryPage = "页面"
    override val categorySettings = "设置"
    override val categoryFunction = "功能"

    override val exitMessage = "再按一次退出应用"

    override val languageNote = "仅供娱乐，AI翻译，可能有错误"
}

object ZhTwStrings : AppStrings by ZhCnStrings {
    override val query = "查詢"
    override val appName = "XSWallet"
    override val back = "返回"
    override val close = "關閉"
    override val confirm = "確定"
    override val cancel = "取消"
    override val search = "搜尋"
    override val settings = "設定"
    override val toolbox = "工具箱"
    override val management = "管理"
    override val overview = "概覽"
    override val wallet = "錢包"
    override val transactions = "交易"
    override val history = "歷史"
    override val exitMessage = "再按一次退出應用"
    override val languageNote = "僅供娛樂，AI翻譯，可能有錯誤"
    override val toolboxDesc1 = "這些功能你可能一輩子都用不到，但要用的時候還是有用的"
    override val comingSoon = "沒了"
    override val moreToolsSoon = "是的，沒有了"
    override val underDevelopment = "功能開發中"
}

object EnStrings : AppStrings {
    override val query = "Query"
    override val appName = "XSWallet"
    override val back = "Back"
    override val close = "Close"
    override val confirm = "Confirm"
    override val cancel = "Cancel"
    override val search = "Search"
    override val settings = "Settings"
    override val toolbox = "Toolbox"
    override val version = "XSWallet v1.0.0"
    override val management = "Manage"

    override val overview = "Overview"
    override val wallet = "Wallet"
    override val transactions = "Transactions"
    override val history = "History"
    override val account = "User Name"
    override val loggedIn = "Logged in"

    override val overviewTitle = "Overview"
    override val overviewDesc1 = "Welcome to XSWallet Overview."
    override val overviewDesc2 = "Here you can see your wallet summary and important information."
    override val totalAssets = "Total Assets"
    override val thisMonth = "this month"
    override val pending = "pending"
    override val monthTransactions = "Monthly Transactions"
    override val status = "Current Status"
    override val menuStatus = "Menu:"
    override val expanded = "Expanded"
    override val collapsed = "Collapsed (hidden)"
    override val on = "On ✓"
    override val off = "Off ✗"
    override val linearProgress = "Linear Progress:"
    override val sliderPosition = "Slider Position:"
    override val clickCount = "Click Count:"

    override val controlsTitle = "Android 16 Style MD3 Controls"
    override val android16SwitchTitle = "Android 16 Style Switch (MD3)"
    override val securityMode = "Security Mode"
    override val securityModeDescOn = "Checkmark inside when on"
    override val securityModeDescOff = "Cross inside when off"
    override val nightMode = "Night Mode"
    override val nightModeDescOn = "Checkmark inside when on"
    override val nightModeDescOff = "Cross inside when off"

    override val interactionDemo = "Interaction Demo"
    override val favorite = "Favorite"
    override val clickMe = "Click Me"
    override val resetCounter = "Reset Counter"
    override val simulateLoading = "Simulate Loading"

    override val progressTitle = "Progress Bars & Sliders"
    override val linearProgressBar = "Linear Progress Bar"
    override val progressValue = "Progress:"
    override val increase10 = "Increase 10%"
    override val slider = "Slider"
    override val sliderValue = "Slider Value:"
    override val circularProgress = "Circular Progress"
    override val small = "Small"
    override val medium = "Medium"
    override val loading = "Loading"

    override val historyTitle = "History"
    override val historyDesc = "View full history and activity logs."
    override val historyStats = "History Stats"
    override val totalTransactions = "Total Transactions"
    override val monthTransactionsCount = "Monthly Transactions"
    override val activeDays = "Active Days"
    override val recentActivity = "Recent Activity Timeline"
    override val createWallet = "Create Wallet"
    override val largeTransfer = "Large Transfer Completed"
    override val addSecurity = "Add Security Verification"
    override val updateSettings = "Update Wallet Settings"
    override val firstLogin = "First Login"

    override val transactionsTitle = "Transactions"
    override val transactionsDesc = "View and manage your transaction records."
    override val transactionFilter = "Transaction Filter"
    override val all = "All"
    override val income = "Income"
    override val expense = "Expense"
    override val recentTransactions = "Recent Transactions"
    override val ethTransfer = "ETH Transfer"
    override val btcPurchase = "BTC Purchase"
    override val usdtDeposit = "USDT Deposit"
    override val nftPurchase = "NFT Purchase"
    override val tokenSwap = "Token Swap"

    override val walletTitle = "Wallet"
    override val walletDesc = "Manage your crypto assets and wallets."
    override val walletOverview = "Wallet Overview"
    override val mainWallet = "Main Wallet"
    override val savingsWallet = "Savings Wallet"
    override val investmentWallet = "Investment Wallet"
    override val changeWallet = "Change Wallet"
    override val recentActivityLabel = "Recent activity:"
    override val quickActions = "Quick Actions"
    override val send = "Send"
    override val receive = "Receive"
    override val exchange = "Exchange"

    override val settingsTitle = "Settings"
    override val accountSecurity = "Account Security"
    override val accountSecurityDesc = "Manage your account and security settings"
    override val appSettings = "App Settings"
    override val appSettingsDesc = "Change the appearance of the app, etc."
    override val toolboxDesc = "Various utility tools"

    override val accountSecurityTitle = "Account Security"
    override val securityStatus = "Security Status: "
    override val securityStatusGood = "Good"
    override val securityStatusDesc = "Your account security is well configured."
    override val securityOptions = "Security Options"
    override val changePassword = "Change Password"
    override val changePasswordDesc = "Change password regularly for better security"
    override val twoFactor = "Two-Factor Authentication"
    override val twoFactorDesc = "Not enabled, recommended"
    override val deviceManagement = "Device Management"
    override val deviceManagementDesc = "View and manage logged-in devices"
    override val securityTips = "Security Tips"
    override val securityTip1 = "• Change passwords regularly, avoid simple passwords"
    override val securityTip2 = "• Enable two-factor authentication"
    override val securityTip3 = "• Do not stay logged in on public devices"
    override val securityTip4 = "• Beware of phishing and scams"

    override val appSettingsTitle = "App Settings"
    override val themeSettings = "Theme Settings"
    override val darkMode = "Dark Mode"
    override val darkModeDesc = "Switch to dark theme"
    override val dynamicColor = "Dynamic Color"
    override val dynamicColorDesc = "Use Android 12+ system theme color"
    override val dynamicColorUnavailable = "Requires Android 12+"
    override val featureUnavailable = "Unavailable"
    override val customThemeColor = "Custom Theme Color"
    override val accentColorHex = "Accent Color (HEX)"
    override val accentColorDesc = "Enter HEX color value (e.g., #FF5722) when dynamic color is off"
    override val colorPreview = "Color Preview"
    override val displaySettings = "Display Settings"
    override val dpiScale = "DPI Scale"
    override val dpiScaleDesc = "Adjust interface density (0.5 - 2.0, default 1.0)"
    override val dpiExample = "e.g., 1.0"
    override val apply = "Apply"
    override val currentDpi = "Current DPI:"
    override val dpiWarning = "Please enter a valid DPI value (0.5 - 2.0)"
    override val seconds = "s"
    override val secretPage = "Secret Page"

    override val toolboxTitle = "Toolbox"
    override val toolboxDesc1 = "Features you might never use, but useful when needed"
    override val comingSoon = "No more"
    override val underDevelopment = "Under Development"
    override val moreToolsSoon = "Yes, it's gone"
    override val toolboxPlan = "Toolbox Plan"
    override val planItem1 = "• Address Book"
    override val planItem2 = "• Transaction Signing Tool"
    override val planItem3 = "• Mnemonic Validation"
    override val planItem4 = "• Network Status"
    override val planItem5 = "• Data Backup"

    override val searchTitle = "Search"
    override val searchPlaceholder = "Search features..."
    override val resultsFound = "Found %d results"
    override val categoryPage = "Page"
    override val categorySettings = "Settings"
    override val categoryFunction = "Feature"

    override val exitMessage = "Press back again to exit"

    override val languageNote = "For fun only, AI translation, may contain errors"
}

object LzhStrings : AppStrings by ZhCnStrings {
    override val query = "查詢"
    override val appName = "XSWallet"
    override val overview = "總覽"
    override val wallet = "錢囊"
    override val transactions = "交易錄"
    override val history = "史錄"
    override val settings = "設定"
    override val toolbox = "百寶箱"
    override val search = "尋覓"
    override val management = "管理"
    override val back = "歸去"
    override val confirm = "然也"
    override val cancel = "否也"
    override val exitMessage = "再按一次退出應用"
    override val languageNote = "僅供娛樂，AI翻譯，或有謬誤"
    override val toolboxDesc1 = "此等功能，君或終生不用，然需時則有用"
    override val comingSoon = "無矣"
    override val moreToolsSoon = "然，無矣"
    override val underDevelopment = "功能開發中"
}

@Stable
object LanguageManager {
    var currentLanguage: AppLanguage by mutableStateOf(AppLanguage.ZH_CN)
        private set

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    val strings: AppStrings
        @Composable
        get() = when (currentLanguage) {
            AppLanguage.ZH_CN -> ZhCnStrings
            AppLanguage.ZH_TW -> ZhTwStrings
            AppLanguage.EN -> EnStrings
            AppLanguage.LZH -> LzhStrings
        }
}

val LocalLanguageManager = staticCompositionLocalOf { LanguageManager }
