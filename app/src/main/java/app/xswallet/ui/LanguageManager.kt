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

    val account: String
    val loggedIn: String

    val settingsTitle: String
    val accountSecurity: String
    val accountSecurityDesc: String
    val appSettings: String
    val appSettingsDesc: String
    val toolboxDesc: String

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

    val toolboxTitle: String
    val toolboxDesc1: String
    val comingSoon: String
    val underDevelopment: String
    val moreToolsSoon: String
    val languageNote: String

    val exitMessage: String
}

object ZhCnStrings : AppStrings {
    override val appName = "XSWallet"
    override val back = "返回"
    override val close = "关闭"
    override val confirm = "确定"
    override val cancel = "取消"
    override val search = "搜索"
    override val settings = "设置"
    override val toolbox = "工具箱"
    override val version = "XSWallet v1.0.0"

    override val account = "用户名称"
    override val loggedIn = "已登录"

    override val settingsTitle = "设置"
    override val accountSecurity = "账号设置"
    override val accountSecurityDesc = "管理您的账号和安全设置"
    override val appSettings = "应用设置"
    override val appSettingsDesc = "更改应用的外观等设置"
    override val toolboxDesc = "各种实用工具"

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

    override val toolboxTitle = "工具箱"
    override val toolboxDesc1 = "这些功能你可能一辈子都用不到，但要用的时候还是有用的"
    override val comingSoon = "没了"
    override val underDevelopment = "功能开发中"
    override val moreToolsSoon = "是的，没有了"
    override val languageNote = "仅供娱乐，AI翻译，可能有错误"

    override val exitMessage = "再按一次退出应用"
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
    override val version = "XSWallet v1.0.0"

    override val account = "用戶名稱"
    override val loggedIn = "已登入"

    override val settingsTitle = "設定"
    override val accountSecurity = "帳號設定"
    override val accountSecurityDesc = "管理您的帳號和安全設定"
    override val appSettings = "應用設定"
    override val appSettingsDesc = "更改應用的外觀等設定"
    override val toolboxDesc = "各種實用工具"

    override val appSettingsTitle = "應用設定"
    override val themeSettings = "主題設定"
    override val darkMode = "深色模式"
    override val darkModeDesc = "切換到深色主題"
    override val dynamicColor = "動態取色"
    override val dynamicColorDesc = "使用 Android 12+ 系統主題顏色"
    override val dynamicColorUnavailable = "需要 Android 12+ 系統"
    override val featureUnavailable = "功能不可用"
    override val customThemeColor = "自定義主題顏色"
    override val accentColorHex = "強調色 (HEX值)"
    override val accentColorDesc = "關閉動態取色後，可在此輸入HEX顏色值（如#FF5722）"
    override val colorPreview = "顏色預覽"
    override val displaySettings = "顯示設定"
    override val dpiScale = "顯示密度 (DPI縮放)"
    override val dpiScaleDesc = "調整應用界面顯示密度，範圍：0.5 - 2.0 (默認: 1.0)"
    override val dpiExample = "例如: 1.0"
    override val apply = "應用"
    override val currentDpi = "當前DPI:"
    override val dpiWarning = "請輸入有效的DPI縮放值 (0.5 - 2.0)"
    override val seconds = "秒"

    override val toolboxTitle = "工具箱"
    override val toolboxDesc1 = "這些功能你可能一輩子都用不到，但要用的時候還是有用的"
    override val comingSoon = "沒了"
    override val underDevelopment = "功能開發中"
    override val moreToolsSoon = "是的，沒有了"
    override val languageNote = "僅供娛樂，AI翻譯，可能有錯誤"

    override val exitMessage = "再按一次退出應用"
}

object EnStrings : AppStrings {
    override val appName = "XSWallet"
    override val back = "Back"
    override val close = "Close"
    override val confirm = "Confirm"
    override val cancel = "Cancel"
    override val search = "Search"
    override val settings = "Settings"
    override val toolbox = "Toolbox"
    override val version = "XSWallet v1.0.0"

    override val account = "User Name"
    override val loggedIn = "Logged in"

    override val settingsTitle = "Settings"
    override val accountSecurity = "Account Security"
    override val accountSecurityDesc = "Manage your account and security settings"
    override val appSettings = "App Settings"
    override val appSettingsDesc = "Change the appearance of the app, etc."
    override val toolboxDesc = "Various utility tools"

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

    override val toolboxTitle = "Toolbox"
    override val toolboxDesc1 = "Features you might never use, but useful when needed"
    override val comingSoon = "No more"
    override val underDevelopment = "Under Development"
    override val moreToolsSoon = "Yes, it's gone"
    override val languageNote = "For entertainment only, AI translation, may contain errors"

    override val exitMessage = "Press back again to exit"
}

object LzhStrings : AppStrings by ZhCnStrings {
    override val query = "查詢"
    override val appName = "XSWallet"
    override val back = "返"
    override val close = "閉"
    override val confirm = "諾"
    override val cancel = "罷"
    override val search = "尋"
    override val settings = "設定"
    override val toolbox = "百寶箱"
    override val version = "XSWallet 版本一"

    override val account = "用戶名"
    override val loggedIn = "已登"

    override val settingsTitle = "設定"
    override val accountSecurity = "賬號設定"
    override val accountSecurityDesc = "管理汝之賬號與護衛設定"
    override val appSettings = "應用設定"
    override val appSettingsDesc = "更改應用之外觀等設定"
    override val toolboxDesc = "諸般實用工具"

    override val appSettingsTitle = "應用設定"
    override val themeSettings = "主題設定"
    override val darkMode = "深色模式"
    override val darkModeDesc = "切換至深色主題"
    override val dynamicColor = "動態取色"
    override val dynamicColorDesc = "使用安卓十二以上系統主題顏色"
    override val dynamicColorUnavailable = "需安卓十二以上系統"
    override val featureUnavailable = "功能不可用"
    override val customThemeColor = "自定義主題顏色"
    override val accentColorHex = "強調色 (HEX值)"
    override val accentColorDesc = "關閉動態取色後，可在此輸入HEX顏色值（如#FF5722）"
    override val colorPreview = "顏色預覽"
    override val displaySettings = "顯示設定"
    override val dpiScale = "顯示密度 (DPI縮放)"
    override val dpiScaleDesc = "調整應用界面顯示密度，範圍：0.5 - 2.0 (默認: 1.0)"
    override val dpiExample = "例如: 1.0"
    override val apply = "應用"
    override val currentDpi = "當前DPI:"
    override val dpiWarning = "請輸入有效的DPI縮放值 (0.5 - 2.0)"
    override val seconds = "秒"

    override val toolboxTitle = "百寶箱"
    override val toolboxDesc1 = "此等功能，君或終生不用，然需時則有用"
    override val comingSoon = "無矣"
    override val underDevelopment = "功能開發中"
    override val moreToolsSoon = "然，無矣"
    override val languageNote = "僅供娛樂，AI翻譯，或有謬誤"

    override val exitMessage = "再按一次退出應用"
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
