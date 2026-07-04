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
import kotlin.random.Random

data class ResilienceData(val day: String, val streak: Int, val score: Float)

@Composable
fun ResilienceChartCard(currentStreak: Int) {
    val chartData = remember(currentStreak) {
        val data = mutableListOf<ResilienceData>()
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        var streak = maxOf(0, currentStreak - 7)
        var baseScore = 50f
        
        for (i in 0 until 7) {
            streak += if (Random.nextBoolean() || i == 6) 1 else 0
            if (i == 6) streak = currentStreak // ensure the last point matches actual streak
            
            baseScore += Random.nextFloat() * 10 - 3 // slight upward trend
            baseScore = baseScore.coerceIn(0f, 100f)
            
            data.add(ResilienceData(days[i], streak, baseScore))
        }
        data
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
            text = "COGNITIVE RESILIENCE & STREAK",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 24.dp)) {
                val width = size.width
                val height = size.height
                val xStep = width / (chartData.size - 1)
                
                val maxStreak = (chartData.maxOfOrNull { it.streak } ?: 10).coerceAtLeast(5)
                val maxScore = 100f

                // Draw Grid (Recharts style)
                val gridEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                (0..4).forEach { i ->
                    val y = height * (i / 4f)
                    drawLine(
                        color = DividerWhite.copy(alpha = 0.1f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        pathEffect = gridEffect,
                        strokeWidth = 2f
                    )
                }

                // Streak Line (Gold)
                val streakPath = Path()
                chartData.forEachIndexed { index, data ->
                    val x = index * xStep
                    val y = height - (data.streak.toFloat() / maxStreak) * height
                    if (index == 0) streakPath.moveTo(x, y)
                    else streakPath.lineTo(x, y)
                }
                drawPath(streakPath, color = Gold, style = Stroke(width = 4f))
                
                // Streak Points
                chartData.forEachIndexed { index, data ->
                    val x = index * xStep
                    val y = height - (data.streak.toFloat() / maxStreak) * height
                    drawCircle(color = Surface, radius = 6f, center = Offset(x, y))
                    drawCircle(color = Gold, radius = 4f, center = Offset(x, y))
                }

                // Resilience Score Line (Silver)
                val scorePath = Path()
                chartData.forEachIndexed { index, data ->
                    val x = index * xStep
                    val y = height - (data.score / maxScore) * height
                    if (index == 0) scorePath.moveTo(x, y)
                    else scorePath.lineTo(x, y)
                }
                drawPath(scorePath, color = Silver, style = Stroke(width = 4f))
                
                // Score Points
                chartData.forEachIndexed { index, data ->
                    val x = index * xStep
                    val y = height - (data.score / maxScore) * height
                    drawCircle(color = Surface, radius = 6f, center = Offset(x, y))
                    drawCircle(color = Silver, radius = 4f, center = Offset(x, y))
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(12.dp).background(Gold, RoundedCornerShape(2.dp)))
                    Text("Streak", style = MaterialTheme.typography.labelSmall, color = Gold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(12.dp).background(Silver, RoundedCornerShape(2.dp)))
                    Text("Resilience", style = MaterialTheme.typography.labelSmall, color = Silver)
                }
            }
        }
    }
}
