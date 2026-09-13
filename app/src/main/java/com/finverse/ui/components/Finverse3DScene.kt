package com.finverse.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finverse.R
import com.finverse.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Finverse3DHeroBadge(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "3d_hero_infinite")

    // Continuous 3D rotation angles
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    val counterOrbitAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_orbit"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_float"
    )

    val tiltX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_x"
    )

    val tiltY by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_y"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(
        modifier = modifier
            .size(size + 60.dp)
            .graphicsLayer {
                translationY = floatOffset
                rotationX = tiltX
                rotationY = tiltY
                cameraDistance = 16f * density
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer 3D Cosmic Orbital Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val orbitRadius1 = (size.toPx() / 2f) + 22.dp.toPx()
            val orbitRadius2 = (size.toPx() / 2f) + 12.dp.toPx()

            // Orbit Ring 1 (Primary Electric Blue with rotating dashed stroke)
            rotate(orbitAngle, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            ElectricBlue.copy(alpha = 0.8f),
                            CyanNeon.copy(alpha = 0.6f),
                            Color.Transparent,
                            ElectricBlue.copy(alpha = 0.8f)
                        )
                    ),
                    radius = orbitRadius1,
                    center = center,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )

                // Orbiting satellite energy particle
                val rad = orbitAngle * (PI.toFloat() / 180f)
                val satX = center.x + orbitRadius1 * cos(rad)
                val satY = center.y + orbitRadius1 * sin(rad)
                drawCircle(
                    color = CyanNeon,
                    radius = 4.dp.toPx(),
                    center = Offset(satX, satY)
                )
                drawCircle(
                    color = PureWhite,
                    radius = 2.dp.toPx(),
                    center = Offset(satX, satY)
                )
            }

            // Orbit Ring 2 (Secondary Violet/Emerald with counter-rotation)
            rotate(counterOrbitAngle, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            VioletNeon.copy(alpha = 0.6f),
                            EmeraldNeon.copy(alpha = 0.4f),
                            Color.Transparent,
                            VioletNeon.copy(alpha = 0.6f)
                        )
                    ),
                    radius = orbitRadius2,
                    center = center,
                    style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                )
            }
        }

        // Ambient Multi-layered Glow Halo
        Box(
            modifier = Modifier
                .size(size + 30.dp)
                .scale(glowScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.45f),
                            CyanNeon.copy(alpha = 0.25f),
                            VioletNeon.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3D Glass Surface Core
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = ElectricBlue,
                    spotColor = CyanNeon
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF0D52D6),
                            Color(0xFF0038A8),
                            Color(0xFF051C48)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            PureWhite.copy(alpha = 0.9f),
                            CyanNeon.copy(alpha = 0.7f),
                            Color.Transparent,
                            ElectricBlueLight.copy(alpha = 0.8f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.finverse_logo),
                contentDescription = "Finverse Official 3D Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun Floating3DMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    delayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_3d_$title")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_float"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                translationY = floatY
                cameraDistance = 14f * density
            }
            .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor = accentColor.copy(alpha = 0.4f), spotColor = accentColor),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF131D31).copy(alpha = 0.92f),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                listOf(
                    accentColor.copy(alpha = 0.8f),
                    accentColor.copy(alpha = 0.2f),
                    Color.White.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Medium)
                Text(text = value, fontSize = 13.sp, color = PureWhite, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16,
    activeColor: Color = CyanNeon
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barWidth = (width / (barCount * 1.6f)).coerceAtLeast(3.dp.toPx())
        val spacing = (width - (barWidth * barCount)) / (barCount - 1).coerceAtLeast(1)

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount
            val wave = if (isPlaying) {
                val harmonic1 = sin(normalizedX * 4f * PI.toFloat() + phase) * 0.35f
                val harmonic2 = cos(normalizedX * 8f * PI.toFloat() - phase * 1.4f) * 0.25f
                (harmonic1 + harmonic2 + 0.55f).coerceIn(0.18f, 1f)
            } else {
                0.18f
            }

            val barHeight = height * wave
            val startX = i * (barWidth + spacing)
            val startY = (height - barHeight) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        activeColor,
                        ElectricBlue
                    )
                ),
                topLeft = Offset(startX, startY),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
