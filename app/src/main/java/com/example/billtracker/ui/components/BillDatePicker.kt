package com.example.billtracker.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDatePicker(initialMillis:Long? = null,
    onDismiss: () -> Unit = {},
    onConfirm: (Long?) -> Unit = {}
) {

    val state = rememberDatePickerState(initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,

        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        ),

        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.selectedDateMillis)
                }
            ) {
                Text(
                    "ตกลง",
                    color = Color.Black
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "ยกเลิก",
                    color = Color.Black
                )
            }
        }
    ) {

        DatePicker(
            state = state,

            colors = DatePickerDefaults.colors(

                containerColor = Color.White,

                titleContentColor = Color.Black,

                headlineContentColor = Color.Black,

                weekdayContentColor = Color.DarkGray,

                subheadContentColor = Color.Gray,

                navigationContentColor = Color.Black,

                yearContentColor = Color.Black,

                currentYearContentColor = Color.Black,

                selectedYearContentColor = Color.White,

                selectedYearContainerColor = Color.Black,

                dayContentColor = Color.Black,

                todayContentColor = Color.Black,

                todayDateBorderColor = Color.Black,

                selectedDayContentColor = Color.White,

                selectedDayContainerColor = Color.Black
            )
        )
    }
}