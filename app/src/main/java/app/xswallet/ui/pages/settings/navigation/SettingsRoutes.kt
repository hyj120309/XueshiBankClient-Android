package app.xswallet.ui.pages.settings.navigation

sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("main")
    object AccountSecurity : SettingsRoute("account_security")
    object AppSettings : SettingsRoute("app_settings")
    object Toolbox : SettingsRoute("toolbox")
    object About : SettingsRoute("about")
    object PrivacyPolicy : SettingsRoute("privacy_policy")
}