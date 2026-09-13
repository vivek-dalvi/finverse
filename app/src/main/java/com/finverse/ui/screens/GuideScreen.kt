package com.finverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.finverse.ui.components.FinverseHeroLogo
import com.finverse.ui.components.ModernCard
import com.finverse.ui.theme.*

data class GuideTopic(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val description: String,
    val keyPoints: List<String>,
    val formula: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(navController: NavController) {
    val topics = remember {
        listOf(
            GuideTopic(
                title = "Financial Health Score",
                subtitle = "Comprehensive 360° Financial Wellness Rating (0 - 100)",
                icon = Icons.Default.Shield,
                accentColor = EmeraldNeon,
                description = "Your Health Score measures 4 pillars of financial strength in real time. It tells you instantly whether your finances are in excellent condition or need urgent buffer adjustments.",
                keyPoints = listOf(
                    "Emergency Fund (30% weight): Target 6 months of essential living expenses.",
                    "Savings Rate (30% weight): Target saving >= 20% of monthly income.",
                    "Debt Control (20% weight): Keeping EMI payments under 30% of income.",
                    "Investment Growth (20% weight): Continuous wealth compounding."
                ),
                formula = "Health Score = (Emergency + Savings + DebtRatio + InvestmentScore) / 4"
            ),
            GuideTopic(
                title = "Financial Twin (20-Yr Simulation)",
                subtitle = "AI-Powered Wealth Projection Engine",
                icon = Icons.Default.ShowChart,
                accentColor = ElectricBlue,
                description = "Your Financial Twin models your money 5, 10, and 20 years into the future using compound interest calculations, conservative index returns (10-12%), and regular savings contributions.",
                keyPoints = listOf(
                    "Calculates net worth growth year-by-year.",
                    "Separates liquid savings from high-compounding equity investments.",
                    "Factor in debt pay-downs and interest compounding.",
                    "Helps you visualize your future milestones like buying a home or early retirement."
                ),
                formula = "Future Value = Present Value × (1 + r)^t + Monthly SIP × [((1 + r)^t - 1) / r]"
            ),
            GuideTopic(
                title = "What-If Life Simulator",
                subtitle = "Stress-Test Major Financial Decisions",
                icon = Icons.AutoMirrored.Filled.CompareArrows,
                accentColor = VioletNeon,
                description = "Before accepting a job offer, relocating to a new city, or buying a luxury car, use the What-If Lab to test how changes in income or lifestyle expenses impact your 10-year net worth trajectory.",
                keyPoints = listOf(
                    "Real-time dynamic salary & expense sliders.",
                    "Instant side-by-side delta calculation (+₹ / -₹ difference).",
                    "Simulates +25% Career Raises vs -20% Frugal Spending presets.",
                    "Prevents lifestyle inflation from sabotaging your long-term wealth."
                )
            ),
            GuideTopic(
                title = "Smart Goals & Milestones",
                subtitle = "Targeted Goal-Driven Compounding",
                icon = Icons.Default.Flag,
                accentColor = CyanNeon,
                description = "Create dedicated milestone targets with target deadlines (e.g. Car in 2026, House Down Payment in 2029). Finverse calculates exactly how much monthly SIP is required to reach each goal on time.",
                keyPoints = listOf(
                    "Categorized milestones (House, Car, Travel, Education, Retirement).",
                    "Visual animated progress bars with percent completion.",
                    "Calculates remaining balance and required monthly pace.",
                    "Easy add/delete goal management."
                )
            ),
            GuideTopic(
                title = "Pre-Purchase Affordability Check",
                subtitle = "Instant Anti-Impulse Purchase Engine",
                icon = Icons.Default.ShoppingCart,
                accentColor = AmberGold,
                description = "Thinking of buying a new phone, luxury watch, or taking a vacation? Run the Pre-Purchase check to find out if you can truly afford it without touching your emergency reserves.",
                keyPoints = listOf(
                    "Safe Fully absorbed by surplus cashflow without touching emergency funds.",
                    "Caution Requires eating into your emergency safety cushion.",
                    "Delay Exceeds 4 months of net savings; save specifically for it first.",
                    "High Risk Exceeds total liquid assets and would cause high-interest debt."
                )
            ),
            GuideTopic(
                title = "AI Financial Advisor",
                subtitle = "Personalized Context-Aware Money Guidance",
                icon = Icons.AutoMirrored.Filled.Chat,
                accentColor = IndigoRoyal,
                description = "Ask questions in natural language. The Finverse AI advisor analyzes your exact profile (income, expenses, emergency runway, loans) to give you structured, actionable strategies.",
                keyPoints = listOf(
                    "Ask for investment allocation strategies.",
                    "Get step-by-step debt avalanche/snowball payoff plans.",
                    "Discover areas to optimize monthly recurring bills.",
                    "Works offline with built-in instant local intelligence fallback."
                )
            ),
            GuideTopic(
                title = "Zero-Trust Privacy & Security",
                subtitle = "100% Read-Only, Non-Custodial Architecture",
                icon = Icons.Default.Lock,
                accentColor = SunsetPink,
                description = "Your financial safety is our highest priority. Finverse is purely an intelligence and simulation tool.",
                keyPoints = listOf(
                    "Zero Banking Credentials: We NEVER ask for passwords, bank logins, or OTPs.",
                    "No Transaction Authority: Cannot transfer funds or execute trades.",
                    "Local Storage: Your data remains private and securely saved on your device in Room DB.",
                    "No Data Selling: Your profile is never shared or sold."
                )
            )
        )
    }

    var selectedTopicIndex by remember { mutableIntStateOf(0) }
    val currentTopic = topics[selectedTopicIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finverse Complete Guide", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Horizontal topic pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(topics) { index, topic ->
                    val isSelected = index == selectedTopicIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTopicIndex = index },
                        leadingIcon = {
                            Icon(
                                topic.icon,
                                contentDescription = null,
                                tint = if (isSelected) PureWhite else topic.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = topic.title.split(" ").take(2).joinToString(" "),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = topic.accentColor,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }

            // Topic Details Card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Topic Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        currentTopic.accentColor.copy(alpha = 0.9f),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PureWhite.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(currentTopic.icon, contentDescription = null, tint = PureWhite, modifier = Modifier.size(26.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = PureWhite.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Topic ${selectedTopicIndex + 1} of ${topics.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = currentTopic.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentTopic.subtitle,
                                fontSize = 13.sp,
                                color = PureWhite.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Description Card
                ModernCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "How It Works",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentTopic.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    if (currentTopic.formula != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Functions, contentDescription = null, tint = currentTopic.accentColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentTopic.formula,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Key Takeaways & Best Practices
                ModernCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Key Principles & Rules",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    currentTopic.keyPoints.forEach { point ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = currentTopic.accentColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Next Topic Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedTopicIndex > 0) {
                        OutlinedButton(
                            onClick = { selectedTopicIndex-- },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Previous")
                        }
                    }

                    Button(
                        onClick = {
                            if (selectedTopicIndex < topics.size - 1) {
                                selectedTopicIndex++
                            } else {
                                navController.navigate("dashboard")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = currentTopic.accentColor)
                    ) {
                        Text(
                            if (selectedTopicIndex < topics.size - 1) "Next Topic" else "Go to Dashboard",
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}
