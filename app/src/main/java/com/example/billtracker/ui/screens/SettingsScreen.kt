package com.example.billtracker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.ui.components.AppTopBar
import com.example.billtracker.ui.components.SectionLabel
import com.example.billtracker.ui.theme.BillTrackerTheme

@Composable
fun SettingsScreen(
    appVersion: String,
    onExportData: (password: String) -> Unit,
    onImportData: (uri: Uri, password: String) -> Unit,
    onDeleteAllDataConfirm: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onBackClick: () -> Unit,
//    onTestReminderClick: () -> Unit = {}, // 🔴 DEBUG ONLY - ลบ parameter นี้ทิ้งพร้อมปุ่มด้านล่างหลังทดสอบเสร็จ
    modifier: Modifier = Modifier
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showExportPasswordDialog by remember { mutableStateOf(false) }
    var showImportPasswordDialog by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            showImportPasswordDialog = true
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "ตั้งค่า", onBackClick = onBackClick) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("การแจ้งเตือน")
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        label = "เปิดการแจ้งเตือน",
                        onClick = onOpenNotificationSettings
                    )
                }
            }

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
                            onClick = { showExportPasswordDialog = true }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        SettingsRow(
                            icon = Icons.Default.Upload,
                            label = "นำเข้าข้อมูล",
                            onClick = { filePickerLauncher.launch(arrayOf("*/*")) }
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

//            // 🔴 DEBUG ONLY
//            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                SectionLabel("DEBUG")
//                Card(
//                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
//                    )
//                ) {
//                    SettingsRow(
//                        icon = Icons.Default.Delete,
//                        label = "ทดสอบแจ้งเตือนทันที",
//                        onClick = onTestReminderClick
//                    )
//                }
//            }
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

    if (showExportPasswordDialog) {
        PasswordDialog(
            title = "ตั้งรหัสผ่านสำหรับไฟล์",
            description = "ไฟล์ที่ส่งออกจะถูกเข้ารหัส ต้องใช้รหัสผ่านนี้ในการเปิดอ่านทีหลัง " +
                    "กรุณาจำรหัสผ่านนี้ไว้ให้ดี หากลืมจะไม่สามารถกู้คืนข้อมูลได้",
            confirmLabel = "ส่งออก",
            onDismiss = { showExportPasswordDialog = false },
            onConfirm = { password ->
                showExportPasswordDialog = false
                onExportData(password)
            }
        )
    }

    if (showImportPasswordDialog) {
        PasswordDialog(
            title = "ใส่รหัสผ่านของไฟล์",
            description = "ใส่รหัสผ่านที่ตั้งไว้ตอน export ไฟล์นี้ เพื่อถอดรหัสและนำเข้าข้อมูล",
            confirmLabel = "นำเข้า",
            onDismiss = {
                showImportPasswordDialog = false
                pendingImportUri = null
            },
            onConfirm = { password ->
                showImportPasswordDialog = false
                pendingImportUri?.let { uri -> onImportData(uri, password) }
                pendingImportUri = null
            }
        )
    }
}


@Composable
private fun PasswordDialog(
    title: String,
    description: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (password: String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("รหัสผ่าน (อย่างน้อย 6 ตัวอักษร)") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "ซ่อนรหัสผ่าน" else "แสดงรหัสผ่าน"
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(password) },
                enabled = password.length >= 6
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
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
            onExportData = {},
            onImportData = { _, _ -> },
            onDeleteAllDataConfirm = {},
            onOpenNotificationSettings = {},
            onBackClick = {},
        )
    }
}

