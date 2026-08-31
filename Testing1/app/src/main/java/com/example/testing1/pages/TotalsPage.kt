package com.example.testing1.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDark        = Color(0xFF121212)
private val Surface       = Color(0xFF1E1E1E)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextMuted     = Color(0xFF666666)
private val CardShape     = RoundedCornerShape(16.dp)

// Palette for pie slices — extend as needed
private val SliceColours = listOf(
    Color(0xFF6C63FF), Color(0xFFFFD700), Color(0xFF4CAF50),
    Color(0xFFFF5722), Color(0xFF2196F3), Color(0xFFE91E63),
    Color(0xFF00BCD4), Color(0xFFFF9800)
)

data class CategoryTotal(val emoji: String, val label: String, val amount: Double, val percent: Float)

@Composable
fun TotalsPage(modifier: Modifier = Modifier,
    totalSpent: String               = "$0.00",
    categories: List<CategoryTotal>  = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 100.dp)
    ) {
        Text(
            "Spending Breakdown",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ── Pie / donut chart ─────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            DonutChart(
                categories = categories,
                modifier = Modifier.size(200.dp)
            )
        }

        // ── Total spent label ─────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(totalSpent, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Total Spent", fontSize = 11.sp, color = TextSecondary)
        }

        // ── Category breakdown list ───────────────────────────────────────────
        if (categories.isEmpty()) {
            Text("No data yet", fontSize = 13.sp, color = TextMuted)
        } else {
            categories.forEachIndexed { index, cat ->
                CategoryRow(
                    category = cat,
                    colour = SliceColours[index % SliceColours.size]
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Donut chart drawn with Canvas ─────────────────────────────────────────────
@Composable
private fun DonutChart(categories: List<CategoryTotal>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 48f, cap = StrokeCap.Butt)
        val diameter = size.minDimension - stroke.width
        val topLeft = Offset(stroke.width / 2f, stroke.width / 2f)
        val arcSize = Size(diameter, diameter)

        if (categories.isEmpty()) {
            // Empty ring
            drawArc(
                color = Color(0xFF2A2A2A),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            return@Canvas
        }

        var startAngle = -90f   // start from top
        categories.forEachIndexed { index, cat ->
            val sweep = 360f * (cat.percent / 100f)
            drawArc(
                color = SliceColours[index % SliceColours.size],
                startAngle = startAngle,
                sweepAngle = sweep - 2f,   // 2° gap between slices
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            startAngle += sweep
        }
    }
}

// ── Single category row ───────────────────────────────────────────────────────
@Composable
private fun CategoryRow(category: CategoryTotal, colour: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, CardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Colour dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(colour, CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Text(category.emoji, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
        Text(
            category.label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            "${category.percent.toInt()}%",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            "$${String.format("%.2f", category.amount)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colour
        )
    }
}
