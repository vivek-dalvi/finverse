package com.finverse.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finverse.domain.HealthScore
import com.finverse.domain.Projection
import com.finverse.ui.theme.*

@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 3.dp,
    borderBrush: Brush? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(elevation, shape, ambientColor = ElectricBlue.copy(alpha = 0.15f), spotColor = ElectricBlue.copy(alpha = 0.2f))
            .then(
                if (borderBrush != null) Modifier.border(1.dp, borderBrush, shape)
                else Modifier.border(1.dp, SlateBorder.copy(alpha = 0.6f), shape)
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun CircularHealthGauge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (score / 100f).coerceIn(0f, 1f),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "gauge_anim"
    )

    val arcColor = when {
        score >= 80 -> EmeraldNeon
        score >= 60 -> CyanNeon
        score >= 40 -> AmberGold
        else -> SunsetPink
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
            val strokeWidth = 8.dp.toPx()

            // Background Track
            drawArc(
                color = Color.White.copy(alpha = 0.25f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = Size(this.size.width - 8.dp.toPx(), this.size.height - 8.dp.toPx()),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(SunsetPink, AmberGold, CyanNeon, EmeraldNeon)
                ),
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = Size(this.size.width - 8.dp.toPx(), this.size.height - 8.dp.toPx()),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite
            )
            Text(
                text = "SCORE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite.copy(alpha = 0.75f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun InteractiveWealthChart(
    projections: List<Projection>,
    modifier: Modifier = Modifier,
    lineColor: Color = ElectricBlue,
    fillBrush: Brush = Brush.verticalGradient(
        listOf(ElectricBlue.copy(alpha = 0.45f), CyanNeon.copy(alpha = 0.15f), Color.Transparent)
    )
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (projections.size < 2) return@Canvas
        val maxVal = projections.maxOf { it.netWorth }.toFloat().coerceAtLeast(1000f)
        val minVal = projections.minOf { it.netWorth }.toFloat().coerceAtMost(0f)
        val range = (maxVal - minVal).coerceAtLeast(1f)

        val paddingBottom = 20.dp.toPx()
        val paddingTop = 10.dp.toPx()
        val chartHeight = size.height - paddingBottom - paddingTop
        val stepX = size.width / (projections.size - 1)

        val path = Path()
        val fillPath = Path()

        // Draw horizontal subtle grid lines
        val gridLines = 3
        for (i in 0..gridLines) {
            val y = paddingTop + (chartHeight / gridLines) * i
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        projections.forEachIndexed { idx, p ->
            val x = idx * stepX
            val normY = (p.netWorth.toFloat() - minVal) / range
            val y = paddingTop + chartHeight - (normY * chartHeight)

            if (idx == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height - paddingBottom)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (idx - 1) * stepX
                val prevNormY = (projections[idx - 1].netWorth.toFloat() - minVal) / range
                val prevY = paddingTop + chartHeight - (prevNormY * chartHeight)
                
                // Smooth cubic bezier
                val cx = (prevX + x) / 2
                path.cubicTo(cx, prevY, cx, y, x, y)
                fillPath.cubicTo(cx, prevY, cx, y, x, y)
            }
        }

        fillPath.lineTo(size.width, size.height - paddingBottom)
        fillPath.close()

        // Draw glowing fill
        drawPath(path = fillPath, brush = fillBrush)

        // Draw main line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw glowing milestone dots on key years
        projections.forEachIndexed { idx, p ->
            if (p.year == 1 || p.year % 5 == 0 || idx == projections.size - 1) {
                val x = idx * stepX
                val normY = (p.netWorth.toFloat() - minVal) / range
                val y = paddingTop + chartHeight - (normY * chartHeight)
                
                // Outer glow
                drawCircle(color = CyanNeon.copy(alpha = 0.5f), radius = 6.dp.toPx(), center = Offset(x, y))
                // Inner solid dot
                drawCircle(color = PureWhite, radius = 3.5.dp.toPx(), center = Offset(x, y))
                drawCircle(color = ElectricBlue, radius = 2.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}

@Composable
fun ColorfulActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradientColors))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = PureWhite, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BudgetSplitBar(
    needsPct: Float,
    wantsPct: Float,
    savingsPct: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (needsPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(needsPct.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(ElectricBlue)
                )
            }
            if (wantsPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(wantsPct.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(AmberGold)
                )
            }
            if (savingsPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(savingsPct.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(EmeraldNeon)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BudgetLegendItem("Needs ${(needsPct * 100).toInt()}%", ElectricBlue)
            BudgetLegendItem("Wants ${(wantsPct * 100).toInt()}%", AmberGold)
            BudgetLegendItem("Savings ${(savingsPct * 100).toInt()}%", EmeraldNeon)
        }
    }
}

@Composable
fun BudgetLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SlateTextSecondary)
    }
}
