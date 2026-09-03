package com.example.studentorganizer.compose.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.ui.theme.*

@Composable
fun AddEventDialog(
    initialDate: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, subject: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(initialDate) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val quickSubjects = listOf("DBMS", "DS", "Java", "OS", "Math", "AI", "Lab")

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
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. DBMS Assignment") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Subject") },
                    placeholder = { Text("e.g. CS301") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickSubjects) { s ->
                        SuggestionChip(
                            onClick = { subject = s },
                            label = { Text(s, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(6.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Date") },
                    placeholder = { Text("e.g. 29 August 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
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
                        errorMessage = "Please specify a subject"
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
