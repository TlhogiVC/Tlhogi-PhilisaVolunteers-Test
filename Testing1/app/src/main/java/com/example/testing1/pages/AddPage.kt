package com.example.testing1.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

// ── Colour tokens ─────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF121212)
private val Surface       = Color(0xFF1E1E1E)
private val Primary       = Color(0xFF6C63FF)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextMuted     = Color(0xFF666666)

private val CardShape = RoundedCornerShape(12.dp)

// ── Category data ─────────────────────────────────────────────────────────────
private data class Category(val id: String, val emoji: String, val label: String)

private val categories = listOf(
    Category("food",       "🍔", "Food"),
    Category("transport",  "🚗", "Transport"),
    Category("shopping",   "🛍️", "Shopping"),
    Category("bills",      "📄", "Bills"),
    Category("health",     "💊", "Health"),
    Category("fun",        "🎮", "Fun"),
    Category("education",  "📚", "Education"),
    Category("other",      "📦", "Other"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Root screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AddPage(modifier: Modifier = Modifier, onSubmit: (amount: String, category: String, note: String, date: String) -> Unit = { _, _, _, _ -> }) {

    var amount          by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("food") }   // default: Food selected
    var note            by remember { mutableStateOf("") }
    var date            by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 100.dp)
    ) {

        // ── Screen title ──────────────────────────────────────────────────────
        Text(
            text = "Add Expense",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // ── Amount ────────────────────────────────────────────────────────────
        SectionLabel("AMOUNT")

        // Box lets us overlay the "$" prefix on top of the text field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Text("0.00", color = TextMuted, fontSize = 18.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Surface,
                    cursorColor = Primary,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                ),
                shape = CardShape,
                // left-pad enough room for the "$" prefix
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(start = 28.dp)
            )

            // "$" prefix pinned to the left, vertically centred
            Text(
                text = "$",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 14.dp)
            )
        }

        // ── Category grid ─────────────────────────────────────────────────────
        SectionLabel("CATEGORY")

        // LazyVerticalGrid inside a scroll column must have a fixed height.
        // 2 rows × ~80dp per row = 160dp (adjust if your cells are taller).
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(bottom = 20.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false  // outer Column handles scrolling
        ) {
            items(categories) { cat ->
                CategoryButton(
                    category = cat,
                    isSelected = selectedCategory == cat.id,
                    onClick = { selectedCategory = cat.id }
                )
            }
        }

        // ── Note ──────────────────────────────────────────────────────────────
        SectionLabel("NOTE (OPTIONAL)")

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text("e.g. Lunch with team", color = TextMuted, fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Surface,
                cursorColor = Primary,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
            ),
            shape = CardShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 20.dp)
        )

        // ── Date ──────────────────────────────────────────────────────────────
        SectionLabel("DATE")

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            placeholder = { Text("YYYY-MM-DD", color = TextMuted, fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Surface,
                cursorColor = Primary,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
            ),
            shape = CardShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 24.dp)
        )

        // ── Submit button ─────────────────────────────────────────────────────
        Button(
            onClick = { onSubmit(amount, selectedCategory, note, date) },
            shape = CardShape,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Add Expense",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category chip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CategoryButton(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor     = if (isSelected) Primary.copy(alpha = 0.15f) else Surface
    val borderColor = if (isSelected) Primary else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(bgColor, CardShape)
            .border(1.dp, borderColor, CardShape)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Text(text = category.emoji, fontSize = 20.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            text = category.label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small section label (AMOUNT, CATEGORY, etc.)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}