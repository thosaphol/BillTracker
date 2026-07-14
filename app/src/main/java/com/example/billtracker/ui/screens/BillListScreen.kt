package com.example.billtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.status
import com.example.billtracker.domain.model.BillStatus
import com.example.billtracker.ui.components.AppTopBar
import com.example.billtracker.ui.components.BillItemCard
import com.example.billtracker.ui.components.BottomNavBar
import com.example.billtracker.ui.components.BottomNavDestination
import com.example.billtracker.ui.components.EmptyStateView
import com.example.billtracker.ui.components.formatBaht
import com.example.billtracker.ui.preview.previewBills
import com.example.billtracker.ui.preview.previewCategories
import com.example.billtracker.ui.theme.BillTrackerTheme

/**
 * Dumb component: หน้าหลักแสดงรายการค้างจ่ายทั้งหมด
 * รับ bills + categories (resolve แล้ว) จาก ViewModel ผ่าน parameter ทั้งหมด
 * ไม่มีการเรียก Repository/ViewModel ในไฟล์นี้เลย
 */
@Composable
fun BillListScreen(
    bills: List<Bill>,
    categories: List<Category>,
    onBillClick: (Int) -> Unit,
    onTogglePaid: (Bill) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavSelect: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalOutstanding = bills.filter { it.status() != BillStatus.PAID }.sumOf { it.amount }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = "ค่าใช้จ่ายค้างชำระ",
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "ตั้งค่า")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "เพิ่มรายการ")
            }
        },
        bottomBar = {
            BottomNavBar(current = BottomNavDestination.BILLS, onSelect = onNavSelect)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // สรุปยอดค้างชำระทั้งหมด (ของแถมจาก mockup ไม่ใช่ core requirement แต่ช่วย UX)
            TotalOutstandingCard(
                amount = totalOutstanding,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (bills.isEmpty()) {
                EmptyStateView(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(bills, key = { it.id }) { bill ->
                        val category = categories.firstOrNull { it.id == bill.categoryId }
                            ?: categories.first()
                        BillItemCard(
                            bill = bill,
                            category = category,
                            onClick = { onBillClick(bill.id) },
                            onTogglePaid = { onTogglePaid(bill) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalOutstandingCard(amount: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ยอดค้างชำระทั้งหมด",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = formatBaht(amount),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun BillListScreenPreview() {
    BillTrackerTheme {
        BillListScreen(
            bills = previewBills,
            categories = previewCategories,
            onBillClick = {},
            onTogglePaid = {},
            onAddClick = {},
            onSettingsClick = {},
            onNavSelect = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Empty state")
@Composable
private fun BillListScreenEmptyPreview() {
    BillTrackerTheme {
        BillListScreen(
            bills = emptyList(),
            categories = previewCategories,
            onBillClick = {},
            onTogglePaid = {},
            onAddClick = {},
            onSettingsClick = {},
            onNavSelect = {}
        )
    }
}