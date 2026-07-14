package com.example.billtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.domain.model.status
import com.example.billtracker.ui.components.AppTopBar
import com.example.billtracker.ui.components.CategoryIconBox
import com.example.billtracker.ui.components.StatusChip
import com.example.billtracker.ui.components.formatBaht
import com.example.billtracker.ui.components.formatDateFullThaiYear
import com.example.billtracker.ui.preview.previewBills
import com.example.billtracker.ui.preview.previewCategories
import com.example.billtracker.ui.theme.BillTrackerTheme

/**
 * Dumb component: ดูรายละเอียดบิล 1 รายการ
 * ไม่มีกล่อง "แนวโน้มการใช้งาน/analytics" ตามที่ตกลงไว้ (ไม่เอา chart)
 */
@Composable
fun BillDetailScreen(
    bill: Bill,
    category: Category,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onMarkAsPaidClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = "รายละเอียดบิล",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "แก้ไข")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "ลบ",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatusChip(status = bill.status())

            Text(
                text = bill.title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = formatBaht(bill.amount),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    DetailRow(
                        iconKey = category.iconKey,
                        label = "ประเภท",
                        value = category.name
                    )
                    DetailDivider()
                    DetailRow(
                        icon = Icons.Default.CalendarMonth,
                        label = "วันครบกำหนด",
                        value = formatDateFullThaiYear(bill.dueDate)
                    )
                    DetailDivider()
                    DetailRow(
                        icon = Icons.Default.Notifications,
                        label = "การแจ้งเตือน",
                        value = when (bill.reminderType) {
                            ReminderType.NONE -> "ไม่แจ้งเตือน"
                            ReminderType.DAILY -> "ทุกวัน"
                            ReminderType.MONTHLY -> "ทุกเดือน"
                        }
                    )
                    if (bill.note.isNotBlank()) {
                        DetailDivider()
                        DetailRow(
                            icon = Icons.Default.Notes,
                            label = "บันทึก",
                            value = bill.note
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!bill.isPaid) {
                Button(
                    onClick = onMarkAsPaidClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.height(20.dp))
                    Spacer(modifier = Modifier.height(0.dp).padding(end = 8.dp))
                    Text("ทำเครื่องหมายว่าจ่ายแล้ว")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ลบรายการนี้?") },
            text = { Text("คุณต้องการลบ \"${bill.title}\" ใช่ไหม การกระทำนี้ไม่สามารถย้อนกลับได้") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteConfirm()
                }) {
                    Text("ลบ", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconKey: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconKey != null) {
            CategoryIconBox(iconKey = iconKey, size = 36.dp)
        } else if (icon != null) {
            CategoryIconIndependentBox(icon = icon)
        }
        Spacer(modifier = Modifier.padding(start = 6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun CategoryIconIndependentBox(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .height(36.dp)
            .padding(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun DetailDivider() {
    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun BillDetailScreenPreview() {
    BillTrackerTheme {
        val bill = previewBills.first()
        val category = previewCategories.first { it.id == bill.categoryId }
        BillDetailScreen(
            bill = bill,
            category = category,
            onBackClick = {},
            onEditClick = {},
            onDeleteConfirm = {},
            onMarkAsPaidClick = {}
        )
    }
}