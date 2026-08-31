package com.example.testing1.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens (match your existing palette) ──────────────────────────────
private val BgDark      = Color(0xFF121212)
private val Surface     = Color(0xFF1E1E1E)
private val Primary     = Color(0xFF6C63FF)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextMuted   = Color(0xFF888888)
private val TextSecondary = Color(0xFFAAAAAA)

// ── Shared card shape ─────────────────────────────────────────────────────────
private val CardShape = RoundedCornerShape(16.dp)

// ─────────────────────────────────────────────────────────────────────────────
// Root screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GoalsPage(modifier: Modifier = Modifier) {
    // Per-card state: displayed value + edit-form visibility + draft input
    var incomeDisplay    by remember { mutableStateOf("$0") }
    var incomeInput      by remember { mutableStateOf("") }
    var incomeEditing    by remember { mutableStateOf(false) }

    var spendingDisplay  by remember { mutableStateOf("Not set") }
    var spendingInput    by remember { mutableStateOf("") }
    var spendingEditing  by remember { mutableStateOf(false) }
    var spendingProgress by remember { mutableIntStateOf(0) }   // 0-100

    var investDisplay    by remember { mutableStateOf("Not set") }
    var investInput      by remember { mutableStateOf("") }
    var investEditing    by remember { mutableStateOf(false) }

    var emergencyDisplay by remember { mutableStateOf("Not set") }
    var emergencyInput   by remember { mutableStateOf("") }
    var emergencyEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .padding(bottom = 80.dp)   // nav-bar clearance (matches paddingBottom="100dp")
    ) {
        // Screen title
        Text(
            text = "Goals & Income",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // ── Card 1: Monthly Income ────────────────────────────────────────────
        GoalCard(
            emoji = "💰",
            title = "Monthly Income",
            displayValue = incomeDisplay,
            displayColor = Primary,          // accent color for income
            displayFontSize = 22,
            isEditing = incomeEditing,
            inputValue = incomeInput,
            inputHint = "Enter monthly income",
            saveLabel = "Save Income",
            onEditClick = { incomeEditing = !incomeEditing },
            onInputChange = { incomeInput = it },
            onSave = {
                incomeDisplay = if (incomeInput.isNotBlank()) "$$incomeInput" else incomeDisplay
                incomeEditing = false
            }
        )

        // ── Card 2: Spending Limit ────────────────────────────────────────────
        GoalCard(
            emoji = "🎯",
            title = "Spending Limit",
            displayValue = spendingDisplay,
            isEditing = spendingEditing,
            inputValue = spendingInput,
            inputHint = "Max monthly spend",
            onEditClick = { spendingEditing = !spendingEditing },
            onInputChange = { spendingInput = it },
            onSave = {
                spendingDisplay = if (spendingInput.isNotBlank()) "$$spendingInput" else spendingDisplay
                spendingEditing = false
            },
            extraContent = {
                // Progress bar – only shown when a limit has been set
                if (spendingDisplay != "Not set") {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { spendingProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Primary,
                        trackColor = Surface,
                    )
                    Text(
                        text = "$spendingProgress% of limit used",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        )

        // ── Card 3: Investment Goal ───────────────────────────────────────────
        GoalCard(
            emoji = "📈",
            title = "Investment Goal",
            displayValue = investDisplay,
            isEditing = investEditing,
            inputValue = investInput,
            inputHint = "Investment target",
            onEditClick = { investEditing = !investEditing },
            onInputChange = { investInput = it },
            onSave = {
                investDisplay = if (investInput.isNotBlank()) "$$investInput" else investDisplay
                investEditing = false
            }
        )

        // ── Card 4: Emergency Fund ────────────────────────────────────────────
        GoalCard(
            emoji = "🛡️",
            title = "Emergency Fund",
            displayValue = emergencyDisplay,
            isEditing = emergencyEditing,
            inputValue = emergencyInput,
            inputHint = "Emergency fund target",
            onEditClick = { emergencyEditing = !emergencyEditing },
            onInputChange = { emergencyInput = it },
            onSave = {
                emergencyDisplay = if (emergencyInput.isNotBlank()) "$$emergencyInput" else emergencyDisplay
                emergencyEditing = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable goal card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GoalCard(
    emoji: String,
    title: String,
    displayValue: String,
    displayColor: Color = TextPrimary,
    displayFontSize: Int = 16,
    isEditing: Boolean,
    inputValue: String,
    inputHint: String,
    saveLabel: String = "Save",
    onEditClick: () -> Unit,
    onInputChange: (String) -> Unit,
    onSave: () -> Unit,
    extraContent: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(text = emoji, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 1.dp, minWidth = 1.dp)
                ) {
                    Text(text = "Edit", fontSize = 11.sp, color = Color.White)
                }
            }

            // Display value
            Text(
                text = displayValue,
                fontSize = displayFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = displayColor
            )

            // Optional extra content slot (e.g. spending progress bar)
            extraContent()

            // Animated edit form
            AnimatedVisibility(visible = isEditing) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = onInputChange,
                        placeholder = {
                            Text(inputHint, color = TextMuted, fontSize = 13.sp)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = LocalTextStyle.current.copy(
                            color = TextPrimary,
                            fontSize = 13.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextMuted,
                            cursorColor = Primary
                        ),
                        shape = CardShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = onSave,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = saveLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}