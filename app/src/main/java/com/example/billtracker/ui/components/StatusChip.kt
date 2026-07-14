package com.example.billtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.BillStatus
import com.example.billtracker.ui.theme.BillTrackerTheme
import com.example.billtracker.ui.theme.StatusOverdueBg
import com.example.billtracker.ui.theme.StatusOverdueText
import com.example.billtracker.ui.theme.StatusPaidBg
import com.example.billtracker.ui.theme.StatusPaidText
import com.example.billtracker.ui.theme.StatusUnpaidBg
import com.example.billtracker.ui.theme.StatusUnpaidText

@Composable
fun StatusChip(status: BillStatus, modifier: Modifier = Modifier) {
    val (label, bg, textColor) = when (status) {
        BillStatus.PAID -> Triple("จ่ายแล้ว", StatusPaidBg, StatusPaidText)
        BillStatus.UNPAID -> Triple("ยังไม่จ่าย", StatusUnpaidBg, StatusUnpaidText)
        BillStatus.OVERDUE -> Triple("เกินกำหนด", StatusOverdueBg, StatusOverdueText)
    }
    Text(
        text = label,
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun StatusChipPreview() {
    BillTrackerTheme {
        androidx.compose.foundation.layout.Column {
            StatusChip(BillStatus.PAID)
            StatusChip(BillStatus.UNPAID)
            StatusChip(BillStatus.OVERDUE)
        }
    }
}