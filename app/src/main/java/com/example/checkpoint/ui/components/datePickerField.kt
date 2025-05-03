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
import com.example.checkpoint.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerField(selectedDate: LocalDateTime?, onDateSelected: (LocalDateTime) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val newDate = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDayOfMonth, 0, 0)
            onDateSelected(newDate)
        },
        year, month, day
    )

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    PixelArtButton(
        text = selectedDate?.let {
            context.getString(R.string.renewal_date_input, it.format(formatter))
        } ?: context.getString(R.string.renewal_date),
        onClick = {
            datePickerDialog.show()
        }
    )
}