package com.example.billtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.ui.components.AppTopBar
import com.example.billtracker.ui.components.CategorySelector
import com.example.billtracker.ui.components.SectionLabel
import com.example.billtracker.ui.components.SegmentedControl
import com.example.billtracker.ui.components.formatDateShort
import com.example.billtracker.ui.preview.previewCategories
import com.example.billtracker.ui.state.DateField
import com.example.billtracker.ui.theme.BillTrackerTheme

data class BillFormState(
    val title: String = "",
    val amount: String = "",
    val categoryId: Int? = null,
    val dueDate: Long? = null,
    val reminderEnabled: Boolean = false,
    val reminderType: ReminderType = ReminderType.NONE,
    val reminderStartDate: Long? = null,
    val note: String = ""
)

@Composable
fun AddEditBillScreen(
    isEditMode: Boolean,
    formState: BillFormState,
    categories: List<Category>,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onDueDateClick: () -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderTypeChange: (ReminderType) -> Unit,
    onReminderStartDateClick: () -> Unit,
    onNoteChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = if (isEditMode) "แก้ไขรายการ" else "เพิ่มรายการ",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onSaveClick) {
                        Icon(Icons.Default.Check, contentDescription = "บันทึก")
                    }
                }
            )
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ---- ชื่อรายการ ----
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("ชื่อรายการ")
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = onTitleChange,
                    placeholder = { Text("ระบุชื่อรายการ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // ---- จำนวนเงิน ----
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("จำนวนเงิน")
                OutlinedTextField(
                    value = formState.amount,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("^\\d{0,7}(\\.\\d{0,2})?$"))) {
                            onAmountChange(input)
                        }
                    },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Text("฿") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // ---- ประเภท ----
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("ประเภท")
                CategorySelector(
                    categories = categories,
                    selectedCategoryId = formState.categoryId,
                    onSelect = onCategorySelect
                )
            }

            // ---- วันครบกำหนด ----
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("วันครบกำหนด")
                DateField(
                    dateMillis = formState.dueDate,
                    placeholder = "เลือกวันครบกำหนด",
                    onClick = onDueDateClick
                )
            }

            HorizontalDivider()

            // ---- การแจ้งเตือน ----
            SectionLabel("การแจ้งเตือน")

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("เปิดการแจ้งเตือน", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = formState.reminderEnabled, onCheckedChange = onReminderEnabledChange)
                }

                if (formState.reminderEnabled) {

                    SegmentedControl(
                        options = listOf(ReminderType.NONE, ReminderType.DAILY, ReminderType.MONTHLY),
                        selected = formState.reminderType,
                        onSelect = onReminderTypeChange,
                        labelFor = {
                            when (it) {
                                ReminderType.NONE -> "ไม่แจ้งเตือน"
                                ReminderType.DAILY -> "ทุกวัน"
                                ReminderType.MONTHLY -> "ทุกเดือน"
                            }
                        }
                    )

                    if (formState.reminderType != ReminderType.NONE) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SectionLabel("วันที่เริ่มแจ้งเตือน (ไม่ระบุ = ใช้วันครบกำหนด)")
                            DateField(
                                dateMillis = formState.reminderStartDate,
                                placeholder = "ใช้วันครบกำหนดเป็นค่าเริ่มต้น",
                                onClick = onReminderStartDateClick
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // ---- โน้ต ----
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("โน้ต")
                OutlinedTextField(
                    value = formState.note,
                    onValueChange = onNoteChange,
                    placeholder = { Text("เพิ่มรายละเอียดเพิ่มเติม...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("บันทึก", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun DateField(dateMillis: Long?, placeholder: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = dateMillis?.let { formatDateShort(it) } ?: "",
        onValueChange = {},
        placeholder = { Text(placeholder) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(Icons.Default.DateRange, contentDescription = "เลือกวันที่")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun AddEditBillScreenPreview() {
    BillTrackerTheme {
        AddEditBillScreen(
            isEditMode = false,
            formState = BillFormState(
                title = "ค่าน้ำ",
                amount = "350.00",
                categoryId = previewCategories[2].id,
                dueDate = System.currentTimeMillis(),
                reminderEnabled = true,
                reminderType = ReminderType.MONTHLY
            ),
            categories = previewCategories,
            onTitleChange = {}, onAmountChange = {}, onCategorySelect = {},
            onDueDateClick = {}, onReminderEnabledChange = {}, onReminderTypeChange = {},
            onReminderStartDateClick = {}, onNoteChange = {}, onBackClick = {}, onSaveClick = {}
        )
    }
}