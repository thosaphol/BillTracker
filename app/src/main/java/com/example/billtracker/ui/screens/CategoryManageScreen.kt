package com.example.billtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.Category
import com.example.billtracker.ui.components.AppTopBar
import com.example.billtracker.ui.components.BottomNavBar
import com.example.billtracker.ui.components.BottomNavDestination
import com.example.billtracker.ui.components.CategoryIconBox
import com.example.billtracker.ui.components.SectionLabel
import com.example.billtracker.ui.preview.previewCategories
import com.example.billtracker.ui.theme.BillTrackerTheme

/**
 * Dumb component: จัดการหมวดหมู่
 * onAddCategory ส่ง (name, iconKey) กลับไปให้ ViewModel สร้าง Category เอง
 * iconKey เลือกจาก icon กลุ่มเดียวกับที่มีใน CategoryIcon.kt (ตรงนี้ fix เป็น "more_horiz"
 * ให้ง่ายก่อน ถ้าอยากให้ผู้ใช้เลือกไอคอนเองค่อยเพิ่ม icon picker ทีหลังได้)
 */
@Composable
fun CategoryManageScreen(
    categories: List<Category>,
    onAddCategory: (name: String) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onBackClick: () -> Unit,
    onNavSelect: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCategoryName by remember { mutableStateOf("") }
    val defaultCats = categories.filter { !it.isCustom }
    val customCats = categories.filter { it.isCustom }

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "จัดการหมวดหมู่", onBackClick = onBackClick) },
        bottomBar = { BottomNavBar(current = BottomNavDestination.CATEGORIES, onSelect = onNavSelect) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("หมวดหมู่เริ่มต้น (DEFAULT CATEGORIES)")
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        defaultCats.forEachIndexed { index, category ->
                            CategoryRow(category = category, onDelete = null)
                            if (index != defaultCats.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionLabel("หมวดหมู่ของคุณ (YOUR CATEGORIES)")
                if (customCats.isEmpty()) {
                    Text(
                        "ยังไม่มีหมวดหมู่ที่เพิ่มเอง",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Card(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            customCats.forEachIndexed { index, category ->
                                CategoryRow(category = category, onDelete = { onDeleteCategory(category) })
                                if (index != customCats.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("เพิ่มหมวดหมู่ใหม่...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                FilledIconButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName.trim())
                            newCategoryName = ""
                        }
                    },
                    colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "เพิ่มหมวดหมู่")
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(category: Category, onDelete: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIconBox(iconKey = category.iconKey, size = 40.dp)
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        if (onDelete != null) {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "ลบหมวดหมู่",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun CategoryManageScreenPreview() {
    BillTrackerTheme {
        CategoryManageScreen(
            categories = previewCategories,
            onAddCategory = {},
            onDeleteCategory = {},
            onBackClick = {},
            onNavSelect = {}
        )
    }
}