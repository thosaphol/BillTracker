package com.example.billtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.ui.components.AppTopBar
import com.example.billtracker.ui.components.BottomNavBar
import com.example.billtracker.ui.components.BottomNavDestination
import com.example.billtracker.ui.components.SectionLabel
import com.example.billtracker.ui.theme.BillTrackerTheme

/**
 * Dumb component: ตั้งค่า (ทำท้ายสุดตามที่ตกลงไว้ - priority ต่ำสุด)
 * ไม่มี dark mode toggle - แอป follow system theme อัตโนมัติ (ดู ui/theme/Theme.kt)
 */
@Composable
fun SettingsScreen(
    appVersion: String,
    githubUrl: String,
    onExportData: () -> Unit,
    onDeleteAllDataConfirm: () -> Unit,
    onOpenGithub: () -> Unit,
    onBackClick: () -> Unit,
    onNavSelect: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "ตั้งค่า", onBackClick = onBackClick) },
        bottomBar = { BottomNavBar(current = BottomNavDestination.SETTINGS, onSelect = onNavSelect) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("ข้อมูล")
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.Download,
                            label = "ส่งออกข้อมูล",
                            onClick = onExportData
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        SettingsRow(
                            icon = Icons.Default.Delete,
                            label = "ลบข้อมูลทั้งหมด",
                            labelColor = MaterialTheme.colorScheme.error,
                            iconColor = MaterialTheme.colorScheme.error,
                            onClick = { showDeleteAllDialog = true }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("เกี่ยวกับ")
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.Info,
                            label = "เวอร์ชันของแอป",
                            trailingText = appVersion,
                            onClick = null
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        SettingsRow(
                            icon = Icons.Default.OpenInNew,
                            label = "GitHub Repository",
                            onClick = onOpenGithub
                        )
                    }
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("ลบข้อมูลทั้งหมด?") },
            text = {
                Text("รายการค้างจ่ายและหมวดหมู่ที่คุณเพิ่มเองทั้งหมดจะถูกลบถาวร การกระทำนี้ไม่สามารถย้อนกลับได้")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAllDialog = false
                    onDeleteAllDataConfirm()
                }) {
                    Text("ลบทั้งหมด", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: (() -> Unit)?,
    trailingText: String? = null,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    iconColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor)
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = labelColor, modifier = Modifier.weight(1f))
        if (trailingText != null) {
            Text(trailingText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun SettingsScreenPreview() {
    BillTrackerTheme {
        SettingsScreen(
            appVersion = "1.0.0",
            githubUrl = "https://github.com/yourname/bill-tracker",
            onExportData = {},
            onDeleteAllDataConfirm = {},
            onOpenGithub = {},
            onBackClick = {},
            onNavSelect = {}
        )
    }
}
