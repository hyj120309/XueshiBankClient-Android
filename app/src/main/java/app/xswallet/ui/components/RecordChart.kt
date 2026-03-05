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
import kotlin.math.roundToInt

data class ChartData(
    val records: List<Record>,
    val studentId: Int
) {
    val points: List<Pair<Int, Float>> by lazy {
        records.mapIndexed { index, record ->
            index to record.changeAmount
        }
    }

    val maxAmount: Float by lazy {
        if (records.isEmpty()) 0f else records.maxOf { it.changeAmount }
    }

    val minAmount: Float by lazy {
        if (records.isEmpty()) 0f else records.minOf { it.changeAmount }
    }

    val hasData: Boolean = records.isNotEmpty()
}

@Composable
fun RecordChart(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    if (!chartData.hasData) {
        Box(
            modifier = modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
        return
    }

    val points = chartData.points
    val maxValue = chartData.maxAmount
    val minValue = chartData.minAmount
    val range = maxValue - minValue
    val padding = 16f

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val textColor = MaterialTheme.colorScheme.onSurface

    val density = LocalDensity.current
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = textColor.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
        }
    }

    Column {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (points.size - 1).coerceAtLeast(1)

            val pointsPx = points.map { (index, value) ->
                val x = index * stepX
                val y = if (range > 0) {
                    height - padding - ((value - minValue) / range) * (height - 2 * padding)
                } else {
                    height / 2f
                }
                Offset(x, y)
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

            if (points.isNotEmpty()) {
                val firstPoint = pointsPx.first()
                val lastPoint = pointsPx.last()

                drawLine(
                    color = outlineColor,
                    start = Offset(firstPoint.x, height - padding),
                    end = Offset(firstPoint.x, padding),
                    strokeWidth = 1f
                )

                drawLine(
                    color = outlineColor,
                    start = Offset(padding, lastPoint.y),
                    end = Offset(width - padding, lastPoint.y),
                    strokeWidth = 1f
                )
            }

            drawIntoCanvas { canvas ->
                pointsPx.forEachIndexed { index, offset ->
                    val amount = points[index].second
                    val amountText = amount.roundToInt().toString()
                    val textWidth = textPaint.measureText(amountText)
                    val textX = offset.x - textWidth / 2f
                    val textY = offset.y - 20f
                    canvas.nativeCanvas.drawText(amountText, textX, textY, textPaint)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "最早",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "最新",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}