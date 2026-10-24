package com.example.studentorganizer.compose.calendar

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEventDialog(
    initialDate: String,
    availableCourses: List<Course> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (title: String, subject: String, dueDate: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(initialDate) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    // Only suggest courses actually saved in Room DB
    val subjectOptions = remember(availableCourses) {
        availableCourses.map { "${it.code} - ${it.name}" }.distinct()
    }

    fun showDatePicker() {
        val cal = Calendar.getInstance()
        try {
            val parsed = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).parse(dueDate)
            if (parsed != null) cal.time = parsed
        } catch (_: Exception) {
            // default to current date
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                dueDate = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(selectedCal.time)
                if (errorMessage != null) errorMessage = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Calendar Event",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrandBlack
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Event Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Midterm Exam, Assignment 2") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Subject / Course with Dropdown Picker (Room only)
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {
                            subject = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("Subject / Course") },
                        placeholder = { Text("e.g. CS301 (or enter custom subject)") },
                        singleLine = true,
                        trailingIcon = {
                            if (subjectOptions.isNotEmpty()) {
                                IconButton(onClick = { showDropdown = !showDropdown }) {
                                    Icon(
                                        imageVector = if (showDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Toggle Subject Dropdown",
                                        tint = BrandBlack
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    AnimatedVisibility(visible = showDropdown && subjectOptions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 180.dp)
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Enrolled Courses",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGrayMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )

                                subjectOptions.forEach { option ->
                                    val parts = option.split(" - ", limit = 2)
                                    val code = parts.getOrNull(0) ?: option
                                    val name = parts.getOrNull(1) ?: ""

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                subject = code.trim()
                                                showDropdown = false
                                                if (errorMessage != null) errorMessage = null
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name.ifBlank { code },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = BrandBlack,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = BrandBlack,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = code,
                                                color = BrandWhite,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Date Picker field
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = {
                        dueDate = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Date") },
                    placeholder = { Text("Select date from calendar or type") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pick Date",
                                tint = BrandBlack
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker() },
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter an event title"
                        return@Button
                    }
                    if (subject.isBlank()) {
                        errorMessage = "Please select or enter a subject"
                        return@Button
                    }
                    if (dueDate.isBlank()) {
                        errorMessage = "Please specify a date"
                        return@Button
                    }
                    onConfirm(title.trim(), subject.trim(), dueDate.trim())
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
            ) {
                Text("Add", color = BrandWhite)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = BrandBlack)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Add Event Dialog Preview")
@Composable
fun AddEventDialogPreview() {
    StudentOrganizerTheme {
        AddEventDialog(
            initialDate = "29 August 2026",
            availableCourses = listOf(
                Course(id = 1, name = "Data Structures", code = "CS301", progress = 50)
            ),
            onDismiss = {},
            onConfirm = { _, _, _ -> }
        )
    }
}
