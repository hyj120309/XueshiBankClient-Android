package app.xswallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.xswallet.ui.AppStrings

@Composable
fun StatusRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun StatusCard(
    isMenuExpanded: Boolean,
    switchChecked: Boolean,
    switchChecked2: Boolean,
    progressValue: Float,
    sliderValue: Float,
    count: Int,
    strings: AppStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = strings.status,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            StatusRow(
                strings.menuStatus,
                if (isMenuExpanded) strings.expanded else strings.collapsed
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusRow(
                strings.securityMode + ":",
                if (switchChecked) strings.on else strings.off
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusRow(
                strings.nightMode + ":",
                if (switchChecked2) strings.on else strings.off
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusRow(
                strings.linearProgress,
                "${(progressValue * 100).toInt()}%"
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusRow(
                strings.sliderPosition,
                "${(sliderValue * 100).toInt()}%"
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusRow(
                strings.clickCount,
                "$count"
            )
        }
    }
}