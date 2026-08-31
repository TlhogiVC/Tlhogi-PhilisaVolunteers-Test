package com.example.testing1.pages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDark        = Color(0xFF121212)
private val Surface       = Color(0xFF1E1E1E)
private val Primary       = Color(0xFF6C63FF)
private val TextPrimary   = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFAAAAAA)
private val TextMuted     = Color(0xFF666666)
private val ErrorRed      = Color(0xFFCF6679)
private val CardShape     = RoundedCornerShape(16.dp)

data class HistoryExpense(
    val id: String,
    val emoji: String,
    val label: String,
    val amount: String,
    val date: String
)

@Composable
fun HistoryPage(modifier: Modifier = Modifier,
    expenses: List<HistoryExpense> = emptyList(),
    onDelete: (id: String) -> Unit = {}
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
            "Expense History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (expenses.isEmpty()) {
            Text("No history yet", fontSize = 13.sp, color = TextMuted)
        } else {
            expenses.forEach { expense ->
                HistoryRow(expense = expense, onDelete = { onDelete(expense.id) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HistoryRow(expense: HistoryExpense, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, CardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(expense.emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(expense.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(expense.date, fontSize = 11.sp, color = TextSecondary)
        }

        Text(
            expense.amount,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete expense",
                tint = ErrorRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}