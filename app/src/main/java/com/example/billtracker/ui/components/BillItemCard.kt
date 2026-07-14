package com.example.billtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.ui.preview.previewBills
import com.example.billtracker.ui.preview.previewCategories
import com.example.billtracker.domain.model.status
import com.example.billtracker.ui.theme.BillTrackerTheme

/**
 * Dumb component: รับ bill + category (resolve แล้ว) + callback
 * ไม่รู้จัก ViewModel/Repository ใดๆ ต่อกับ ViewModel ของคุณได้ตรงๆ
 */
@Composable
fun BillItemCard(
    bill: Bill,
    category: Category,
    onClick: () -> Unit,
    onTogglePaid: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconBox(iconKey = category.iconKey)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (bill.isPaid) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (bill.isPaid) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status = bill.status())
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${formatDateShort(bill.dueDate)} · ${category.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatBaht(bill.amount),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                MarkPaidButton(isPaid = bill.isPaid, onClick = onTogglePaid)
            }
        }
    }
}

@Composable
private fun MarkPaidButton(isPaid: Boolean, onClick: () -> Unit) {
    val bg = if (isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val tint = if (isPaid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .background(bg, CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = if (isPaid) "จ่ายแล้ว" else "ทำเครื่องหมายว่าจ่ายแล้ว",
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BillItemCardPreview() {
    BillTrackerTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            previewBills.forEach { bill ->
                val category = previewCategories.first { it.id == bill.categoryId }
                BillItemCard(bill = bill, category = category, onClick = {}, onTogglePaid = {})
            }
        }
    }
}