package com.example.testing1.pages
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ── Colour tokens ─────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF121212)
private val Surface       = Color(0xFF1E1E1E)
private val SurfaceLight  = Color(0xFF2A2A2A)
private val Primary       = Color(0xFF6C63FF)
private val AccentGold    = Color(0xFFFFD700)
private val AccentGreen   = Color(0xFF4CAF50)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextMuted     = Color(0xFF666666)
private val White70       = Color(0xB3FFFFFF)
private val White60       = Color(0x99FFFFFF)

private val CardShape = RoundedCornerShape(16.dp)
private val PillShape = RoundedCornerShape(50.dp)
private val ChipShape = RoundedCornerShape(8.dp)

// ── Data models ───────────────────────────────────────────────────────────────
data class Badge(val label: String)
data class Expense(val emoji: String, val label: String, val amount: String, val date: String)
data class Tip(val icon: String, val title: String, val body: String)
data class Achievement(
    val icon: String,
    val title: String,
    val description: String,
    val xp: Int,
    val unlocked: Boolean
)

// ── Sample data ───────────────────────────────────────────────────────────────
val sampleTips = listOf(
    Tip("☕", "The Latte Factor",      "Small daily purchases add up fast. A $5 daily coffee = $1,825/year."),
    Tip("📊", "50/30/20 Rule",         "Spend 50% on needs, 30% on wants, and save at least 20% of your income."),
    Tip("🔄", "Pay Yourself First",    "Move savings to a separate account the moment your pay lands."),
    Tip("🎯", "Zero-Based Budgeting",  "Assign every dollar a job so your income minus expenses equals zero."),
)

val sampleAchievements = listOf(
    Achievement("🥾", "First Step",      "Log your first expense",            20,  true),
    Achievement("🔥", "On a Streak",     "Log expenses 7 days in a row",      50,  false),
    Achievement("💰", "Saver",           "Stay under budget for a full month", 100, false),
    Achievement("📈", "Investor",        "Set an investment goal",             30,  true),
    Achievement("🛡️", "Safety Net",     "Set an emergency fund goal",         30,  false),
)

// ─────────────────────────────────────────────────────────────────────────────
// Root screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HomePage( modifier: Modifier = Modifier,
    welcomeText: String           = "Welcome back 👋",
    appTitle: String              = "BudgetQuest",
    level: Int                    = 3,
    xpCurrent: Int                = 60,
    xpMax: Int                    = 100,
    balance: String               = "$1,240.00",
    income: String                = "$3,000",
    spent: String                 = "$1,760",
    badges: List<Badge>           = listOf(Badge("🏆 Saver"), Badge("🔥 Streak")),
    recentExpenses: List<Expense> = listOf(
        Expense("🍔", "Food",      "-$12.50", "Today"),
        Expense("🚗", "Transport", "-$4.00",  "Yesterday"),
    ),
    tips: List<Tip>               = sampleTips,
    achievements: List<Achievement> = sampleAchievements
) {
    // ── Dialog state ──────────────────────────────────────────────────────────
    var selectedTip         by remember { mutableStateOf<Tip?>(null) }
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    var showAllTips         by remember { mutableStateOf(false) }
    var showAllAchievements by remember { mutableStateOf(false) }

    // ── Main scrollable content ───────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 100.dp)
    ) {

        // Header
        HeaderRow(welcomeText, appTitle, level)

        // XP bar
        XpBar(xpCurrent, xpMax)

        // Balance card
        BalanceCard(balance, income, spent)

        // Badges
        BadgesSection(badges)

        // ── Tips card ─────────────────────────────────────────────────────────
        SectionCard(
            title = "💡 Financial Tips",
            actionLabel = if (showAllTips) "Show less" else "See all",
            onActionClick = { showAllTips = !showAllTips }
        ) {
            val visibleTips = if (showAllTips) tips else tips.take(2)
            visibleTips.forEach { tip ->
                TipRow(tip = tip, onClick = { selectedTip = tip })
                if (tip != visibleTips.last()) Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Achievements card ─────────────────────────────────────────────────
        SectionCard(
            title = "🏆 Achievements",
            actionLabel = if (showAllAchievements) "Show less" else "See all",
            onActionClick = { showAllAchievements = !showAllAchievements }
        ) {
            val visibleAchievements = if (showAllAchievements) achievements else achievements.take(3)
            visibleAchievements.forEach { achievement ->
                AchievementRow(
                    achievement = achievement,
                    onClick = { selectedAchievement = achievement }
                )
                if (achievement != visibleAchievements.last()) Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Recent expenses
        RecentExpensesSection(recentExpenses)
    }

    // ── Tip detail popup ──────────────────────────────────────────────────────
    selectedTip?.let { tip ->
        TipDialog(tip = tip, onDismiss = { selectedTip = null })
    }

    // ── Achievement detail popup ──────────────────────────────────────────────
    selectedAchievement?.let { achievement ->
        AchievementDialog(achievement = achievement, onDismiss = { selectedAchievement = null })
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section wrapper card  (title bar + "See all" + content slot)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SectionCard(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    actionLabel,
                    fontSize = 11.sp,
                    color = Primary,
                    modifier = Modifier.clickable(onClick = onActionClick)
                )
            }

            HorizontalDivider(color = SurfaceLight, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tip row  (icon | title + body)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TipRow(tip: Tip, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLight, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // Icon circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .background(Primary.copy(alpha = 0.15f), CircleShape)
        ) {
            Text(tip.icon, fontSize = 18.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(tip.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(
                tip.body,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Achievement row  (big emoji | title + desc | XP badge)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AchievementRow(achievement: Achievement, onClick: () -> Unit) {
    val bgColor     = if (achievement.unlocked) Primary.copy(alpha = 0.12f) else SurfaceLight
    val borderColor = if (achievement.unlocked) Primary.copy(alpha = 0.4f)  else Color.Transparent
    val iconAlpha   = if (achievement.unlocked) 1f else 0.4f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // Big emoji icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp)
        ) {
            Text(
                achievement.icon,
                fontSize = 26.sp,
                color = TextPrimary.copy(alpha = iconAlpha)
            )
        }

        Spacer(Modifier.width(12.dp))

        // Title + description
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = achievement.title + if (achievement.unlocked) " ✓" else "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.unlocked) TextPrimary else TextSecondary
                )
            }
            Text(achievement.description, fontSize = 11.sp, color = TextSecondary)
        }

        Spacer(Modifier.width(8.dp))

        // XP badge
        Surface(shape = ChipShape, color = AccentGold.copy(alpha = 0.15f)) {
            Text(
                "+${achievement.xp} XP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tip detail dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TipDialog(tip: Tip, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
            exit  = fadeOut() + slideOutVertically()
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    // Large icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .background(Primary.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Text(tip.icon, fontSize = 36.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        tip.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    HorizontalDivider(color = SurfaceLight)

                    Spacer(Modifier.height(12.dp))

                    Text(
                        tip.body,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Got it!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Achievement detail dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AchievementDialog(achievement: Achievement, onDismiss: () -> Unit) {
    val statusColor = if (achievement.unlocked) AccentGreen else TextMuted
    val statusText  = if (achievement.unlocked) "✓ Unlocked" else "🔒 Locked"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
            exit  = fadeOut() + slideOutVertically()
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    // Icon with gold ring if unlocked
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                if (achievement.unlocked) AccentGold.copy(alpha = 0.15f)
                                else SurfaceLight,
                                CircleShape
                            )
                    ) {
                        Text(
                            achievement.icon,
                            fontSize = 40.sp,
                            color = TextPrimary.copy(
                                alpha = if (achievement.unlocked) 1f else 0.4f
                            )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Status pill
                    Surface(shape = PillShape, color = statusColor.copy(alpha = 0.15f)) {
                        Text(
                            statusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        achievement.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        achievement.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    HorizontalDivider(color = SurfaceLight)
                    Spacer(Modifier.height(16.dp))

                    // XP reward row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("⚡", fontSize = 18.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Reward: +${achievement.xp} XP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (achievement.unlocked) AccentGreen else Primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (achievement.unlocked) "Awesome! 🎉" else "Keep going!",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Extracted sub-composables (unchanged from previous version)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HeaderRow(welcomeText: String, appTitle: String, level: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(welcomeText, fontSize = 12.sp, color = TextSecondary)
            Text(appTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Surface, PillShape)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("⚡", fontSize = 13.sp)
            Spacer(Modifier.width(4.dp))
            Text("Lv $level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
        }
    }
}

@Composable
private fun XpBar(xpCurrent: Int, xpMax: Int) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            Text("Experience", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.weight(1f))
            Text("$xpCurrent / $xpMax XP", fontSize = 11.sp, color = AccentGold)
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(10.dp)
                .background(Surface, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (xpCurrent / xpMax.toFloat()).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(Brush.horizontalGradient(listOf(Primary, AccentGold)), RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun BalanceCard(balance: String, income: String, spent: String) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.linearGradient(listOf(Primary, Color(0xFF9C27B0))), CardShape)
                .padding(20.dp)
        ) {
            Column {
                Text("Remaining Budget", fontSize = 12.sp, color = White70)
                Text(balance, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.padding(top = 4.dp))
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Column(modifier = Modifier.padding(end = 24.dp)) {
                        Text("Income", fontSize = 10.sp, color = White60)
                        Text(income, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("Spent", fontSize = 10.sp, color = White60)
                        Text(spent, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgesSection(badges: List<Badge>) {
    Text("BADGES EARNED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary,
        modifier = Modifier.padding(bottom = 8.dp))
    if (badges.isEmpty()) {
        Surface(shape = PillShape, color = Surface, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("No badges yet", fontSize = 11.sp, color = TextMuted,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            badges.forEach { badge ->
                Surface(shape = PillShape, color = Primary.copy(alpha = 0.2f)) {
                    Text(badge.label, fontSize = 11.sp, color = Primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentExpensesSection(expenses: List<Expense>) {
    Text("RECENT EXPENSES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary,
        modifier = Modifier.padding(bottom = 12.dp))
    if (expenses.isEmpty()) {
        Text("No expenses yet", fontSize = 13.sp, color = TextMuted)
    } else {
        expenses.forEach { expense ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(Surface, CardShape)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(expense.emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(expense.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(expense.date, fontSize = 11.sp, color = TextSecondary)
                }
                Text(expense.amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}