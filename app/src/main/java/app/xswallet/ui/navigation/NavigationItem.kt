package app.xswallet.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import app.xswallet.ui.AppStrings

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val selected: Boolean = false
)

@Composable
fun navigationItems(strings: AppStrings): List<NavigationItem> {
    return listOf(
        NavigationItem("主页", Icons.Filled.Home),
        NavigationItem("查询", Icons.Filled.Search),
        NavigationItem("管理", Icons.Filled.Person),
        NavigationItem("设置", Icons.Filled.Settings)
    )
}