package app.xswallet.ui.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class ChartRecord(
    val changeAmount: Float,
    val timestamp: String
)

@Composable
fun RecordChart(
    records: List<ChartRecord>,
    currentBalance: Int,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    if (records.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无记录",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.outline
            )
        }
        return
    }

    val sortedRecords = records.sortedBy { it.timestamp }

    val cumulativeBalances = remember(sortedRecords) {
        val list = mutableListOf<Float>()
        var sum = 0f
        for (r in sortedRecords) {
            sum += r.changeAmount
            list.add(sum)
        }
        list
    }

    val initialBalance = remember(currentBalance, cumulativeBalances) {
        currentBalance - (cumulativeBalances.lastOrNull() ?: 0f)
    }

    val points = remember(initialBalance, cumulativeBalances) {
        cumulativeBalances.map { initialBalance + it }
    }

    val maxBalance = points.maxOrNull() ?: 0f
    val minBalance = points.minOrNull() ?: 0f
    val range = maxBalance - minBalance

    val padding = 16f
    val density = LocalDensity.current

    val textPaintAmount = remember(density, colorScheme) {
        android.graphics.Paint().apply {
            color = colorScheme.onSurface.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
        }
    }

    val textPaintDate = remember(density, colorScheme) {
        android.graphics.Paint().apply {
            color = colorScheme.outline.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
        }
    }

    val textPaintTime = remember(density, colorScheme) {
        android.graphics.Paint().apply {
            color = colorScheme.outline.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
        }
    }

    val primaryColor = colorScheme.primary
    val outlineColor = colorScheme.outline.copy(alpha = 0.3f)
    val gridColor = colorScheme.outline.copy(alpha = 0.15f)

    val dateList = remember(sortedRecords) {
        sortedRecords.map { formatDate(it.timestamp) }
    }
    val timeList = remember(sortedRecords) {
        sortedRecords.map { formatTime(it.timestamp) }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(16.dp)
    ) {
        val height = size.height
        val width = size.width
        val bottomMargin = 60f
        val chartHeight = height - bottomMargin

        val stepX = if (points.size > 1) width / (points.size - 1) else width / 2f
        val pointsPx = points.mapIndexed { index, value ->
            val x = if (points.size > 1) index * stepX else width / 2f
            val y = if (range > 0) {
                chartHeight - padding - ((value - minBalance) / range) * (chartHeight - 2 * padding)
            } else {
                chartHeight / 2f
            }
            Offset(x, y)
        }

        pointsPx.forEach { point ->
            drawLine(
                color = gridColor,
                start = Offset(point.x, 0f),
                end = Offset(point.x, chartHeight),
                strokeWidth = 1f
            )
        }

        if (range > 0) {
            var yValue = (minBalance - (minBalance % 50) + 50).coerceAtLeast(minBalance)
            while (yValue <= maxBalance) {
                val y = chartHeight - padding - ((yValue - minBalance) / range) * (chartHeight - 2 * padding)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                yValue += 50
            }
        }

        for (i in 0 until pointsPx.size - 1) {
            drawLine(
                color = primaryColor,
                start = pointsPx[i],
                end = pointsPx[i + 1],
                strokeWidth = 3f
            )
        }

        pointsPx.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 5f,
                center = point
            )
        }

        if (pointsPx.isNotEmpty()) {
            val firstPoint = pointsPx.first()
            val lastPoint = pointsPx.last()
            drawLine(
                color = outlineColor,
                start = Offset(firstPoint.x, 0f),
                end = Offset(firstPoint.x, chartHeight),
                strokeWidth = 1f
            )
            drawLine(
                color = outlineColor,
                start = Offset(0f, lastPoint.y),
                end = Offset(width, lastPoint.y),
                strokeWidth = 1f
            )
        }

        drawIntoCanvas { canvas ->
            pointsPx.forEachIndexed { index, offset ->
                val amountText = points[index].toInt().toString()
                val textWidth = textPaintAmount.measureText(amountText)
                var textX = offset.x - textWidth / 2f
                if (textX < 0f) textX = 0f
                if (textX + textWidth > width) textX = width - textWidth
                val textY = offset.y - 20f
                canvas.nativeCanvas.drawText(amountText, textX, textY, textPaintAmount)
            }
        }

        drawIntoCanvas { canvas ->
            pointsPx.forEachIndexed { index, offset ->
                val dateText = dateList[index]
                val textWidth = textPaintDate.measureText(dateText)
                var textX = offset.x - textWidth / 2f
                if (textX < 0f) textX = 0f
                if (textX + textWidth > width) textX = width - textWidth
                val textY = chartHeight + 15f // 第一行
                canvas.nativeCanvas.drawText(dateText, textX, textY, textPaintDate)
            }
        }

        drawIntoCanvas { canvas ->
            pointsPx.forEachIndexed { index, offset ->
                val timeText = timeList[index]
                val textWidth = textPaintTime.measureText(timeText)
                var textX = offset.x - textWidth / 2f
                if (textX < 0f) textX = 0f
                if (textX + textWidth > width) textX = width - textWidth
                val textY = chartHeight + 35f // 第二行
                canvas.nativeCanvas.drawText(timeText, textX, textY, textPaintTime)
            }
        }
    }
}

private fun formatDate(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(iso)
        val out = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        out.format(date)
    } catch (e: Exception) {
        iso.take(10)
    }
}

private fun formatTime(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(iso)
        val out = SimpleDateFormat("HH:mm", Locale.getDefault())
        out.format(date)
    } catch (e: Exception) {
        iso.takeLast(5)
    }
}