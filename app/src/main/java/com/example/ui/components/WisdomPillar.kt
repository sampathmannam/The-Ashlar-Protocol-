package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DividerWhite
import com.example.ui.theme.Gold
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate
import com.example.ui.theme.Surface

data class ChartData(val day: Int, val score: Float)

val INITIAL_WISDOM_DATA = listOf(
    ChartData(1, 7f), ChartData(2, 6f), ChartData(3, 8f),
    ChartData(4, 5f), ChartData(5, 4f), ChartData(6, 7f),
    ChartData(7, 8f), ChartData(8, 9f), ChartData(9, 4f),
    ChartData(10, 3f), ChartData(11, 5f), ChartData(12, 6f),
    ChartData(13, 7f), ChartData(14, 7f), ChartData(15, 8f),
    ChartData(16, 8f), ChartData(17, 6f), ChartData(18, 4f),
    ChartData(19, 3f), ChartData(20, 2f)
)

@Composable
fun WisdomPillar() {
    val processedData = remember {
        INITIAL_WISDOM_DATA.mapIndexed { index, data ->
            val start = maxOf(0, index - 4)
            val window = INITIAL_WISDOM_DATA.subList(start, index + 1)
            val sma = window.map { it.score }.average().toFloat()
            Pair(data.score, sma)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "PILLAR OF WISDOM: THE LONG VIEW",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxScore = 10f
                val minScore = 0f
                val range = maxScore - minScore
                val height = size.height
                val width = size.width
                val xStep = width / (processedData.size - 1)

                // Draw Grid
                val gridEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                (0..4).forEach { i ->
                    val y = height * (i / 4f)
                    drawLine(
                        color = Slate,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        pathEffect = gridEffect
                    )
                }

                // Draw Raw Score (Silver)
                val rawPath = Path()
                processedData.forEachIndexed { index, pair ->
                    val x = index * xStep
                    val y = height - ((pair.first - minScore) / range) * height
                    if (index == 0) {
                        rawPath.moveTo(x, y)
                    } else {
                        rawPath.lineTo(x, y)
                    }
                }
                drawPath(rawPath, color = Silver, style = Stroke(width = 2f))

                // Draw SMA (Gold)
                val smaPath = Path()
                processedData.forEachIndexed { index, pair ->
                    val x = index * xStep
                    val y = height - ((pair.second - minScore) / range) * height
                    if (index == 0) {
                        smaPath.moveTo(x, y)
                    } else {
                        smaPath.lineTo(x, y)
                    }
                }
                drawPath(smaPath, color = Gold, style = Stroke(width = 4f))
            }
        }
    }
}
