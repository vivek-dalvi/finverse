package com.finverse.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finverse.R
import com.finverse.ui.theme.*

@Composable
fun FinverseLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconPadding: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = ElectricBlue, spotColor = ElectricBlue)
            .clip(CircleShape)
            .background(ElectricBlue)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), Color.Transparent)),
                shape = CircleShape
            )
            .padding(iconPadding),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.finverse_logo),
            contentDescription = "Finverse Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FinverseHeroLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    // Subtle breathing/pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier.size(size + 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing halo
        Box(
            modifier = Modifier
                .size(size + 16.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = glowAlpha),
                            CyanNeon.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main brand logo
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = ElectricBlue,
                    spotColor = CyanNeon
                )
                .clip(CircleShape)
                .background(ElectricBlue)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.2f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.finverse_logo),
                contentDescription = "Finverse Official Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun FinverseBrandHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FinverseLogoBadge(size = 36.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Finverse",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.5).sp
        )
    }
}
