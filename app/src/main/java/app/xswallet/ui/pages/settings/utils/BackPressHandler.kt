package app.xswallet.ui.pages.settings.utils

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BackPressHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
    doublePressToExit: Boolean = false,
    exitMessage: String = "再按一次退出"
) {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(false) }
    var backPressedTime by remember { mutableStateOf(0L) }

    if (enabled) {
        BackHandler {
            if (doublePressToExit) {
                val currentTime = System.currentTimeMillis()
                if (backPressedTime + 2000 > currentTime) {
                    onBack()
                } else {
                    Toast.makeText(context, exitMessage, Toast.LENGTH_SHORT).show()
                    backPressedTime = currentTime
                }
            } else {
                onBack()
            }
        }
    }
}