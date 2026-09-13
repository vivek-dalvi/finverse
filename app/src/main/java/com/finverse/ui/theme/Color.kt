package com.finverse.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// FINVERSE VIBRANT COLOR PALETTE
// ==========================================

// Primary Electric Blues
val ElectricBlue = Color(0xFF0066FF)
val ElectricBlueLight = Color(0xFF38BDF8)
val ElectricBlueDark = Color(0xFF0044CC)
val ElectricBlueContainer = Color(0xFFE0F2FE)
val ElectricBlueGlow = Color(0xFF60A5FA)

// Neon & Accent Colors
val CyanNeon = Color(0xFF06B6D4)
val CyanNeonLight = Color(0xFF67E8F9)
val CyanContainer = Color(0xFFCFFAFE)

val EmeraldNeon = Color(0xFF10B981)
val EmeraldNeonDark = Color(0xFF059669)
val EmeraldContainer = Color(0xFFD1FAE5)
val EmeraldOnContainer = Color(0xFF065F46)

val VioletNeon = Color(0xFF8B5CF6)
val VioletNeonDark = Color(0xFF6D28D9)
val VioletContainer = Color(0xFFEDE9FE)
val VioletOnContainer = Color(0xFF5B21B6)

val AmberGold = Color(0xFFF59E0B)
val AmberGoldDark = Color(0xFFD97706)
val AmberContainer = Color(0xFFFEF3C7)
val AmberOnContainer = Color(0xFF92400E)

val SunsetPink = Color(0xFFF43F5E)
val SunsetPinkDark = Color(0xFFE11D48)
val SunsetContainer = Color(0xFFFFE4E6)
val SunsetOnContainer = Color(0xFF9F1239)

val IndigoRoyal = Color(0xFF4F46E5)
val IndigoContainer = Color(0xFFE0E7FF)

// Neutrals & Surfaces (Light)
val PureWhite = Color(0xFFFFFFFF)
val SlateBackground = Color(0xFFF8FAFC)
val SlateSurface = Color(0xFFFFFFFF)
val SlateSurfaceVariant = Color(0xFFF1F5F9)
val SlateBorder = Color(0xFFE2E8F0)
val SlateTextPrimary = Color(0xFF0F172A)
val SlateTextSecondary = Color(0xFF475569)
val SlateTextMuted = Color(0xFF94A3B8)
val Slate400 = Color(0xFF94A3B8)
val Slate700 = Color(0xFF334155)

// Dark Theme Surfaces
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF0A0A0A)
val DarkSurfaceVariant = Color(0xFF141414)
val DarkBorder = Color(0xFF262626)
val DarkTextPrimary = Color(0xFFF9FAFB)
val DarkTextSecondary = Color(0xFF9CA3AF)

// Vibrant Gradients
val PrimaryGradient = Brush.horizontalGradient(listOf(ElectricBlue, CyanNeon))
val HeroGradient = Brush.verticalGradient(listOf(ElectricBlueDark, ElectricBlue, CyanNeon))
val EmeraldGradient = Brush.horizontalGradient(listOf(EmeraldNeon, CyanNeon))
val VioletGradient = Brush.horizontalGradient(listOf(VioletNeon, SunsetPink))
val GoldGradient = Brush.horizontalGradient(listOf(AmberGold, Color(0xFFF97316)))
val DarkHeroGradient = Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
val GlassBorderGradient = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.1f)))
