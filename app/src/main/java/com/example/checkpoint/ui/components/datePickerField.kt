package com.example.checkpoint.ui.components

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerField(onDateSelected: (LocalDateTime) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDate by remember { mutableStateOf<LocalDateTime?>(null) }

    val datePickerDialog = DatePickerDialog(context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
        val newDate = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDayOfMonth, 0, 0)
        selectedDate = newDate
        onDateSelected(newDate)
    }, year, month, day)

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    PixelArtButton(
        text = selectedDate?.let {
            "RENOVACIÓN: ${it.format(formatter)}"
        } ?: "FECHA DE RENOVACIÓN",
        onClick = {
            datePickerDialog.show()
        }
    )
}