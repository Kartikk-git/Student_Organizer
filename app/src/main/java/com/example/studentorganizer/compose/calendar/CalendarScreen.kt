package com.example.studentorganizer.compose.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var assignments by remember { mutableStateOf<List<Assignment>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var assignmentToDelete by remember { mutableStateOf<Assignment?>(null) }

    val displayedCalendar = remember { mutableStateOf(Calendar.getInstance()) }
    val selectedDateCalendar = remember { mutableStateOf(Calendar.getInstance()) }
    val todayCalendar = remember { Calendar.getInstance() }

    fun refreshAssignments() {
        coroutineScope.launch {
            val list = withContext(Dispatchers.IO) {
                database.assignmentDao().getAllAssignments()
            }
            assignments = list
        }
    }

    LaunchedEffect(Unit) {
        refreshAssignments()
    }

    val currentYear = displayedCalendar.value.get(Calendar.YEAR)
    val currentMonth = displayedCalendar.value.get(Calendar.MONTH)

    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val fullDateFormat = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()) }
    val shortDateFormat = remember { SimpleDateFormat("d MMMM yyyy", Locale.getDefault()) }

    val displayedMonthTitle = monthFormat.format(displayedCalendar.value.time)
    val selectedDateTitle = fullDateFormat.format(selectedDateCalendar.value.time)
    val selectedDateShort = shortDateFormat.format(selectedDateCalendar.value.time)

    val daysInGrid = remember(currentYear, currentMonth, selectedDateCalendar.value) {
        generateMonthDays(currentYear, currentMonth, todayCalendar)
    }

    val selectedDayAssignments = remember(assignments, selectedDateCalendar.value) {
        assignments.filter { isDateMatching(it.dueDate, selectedDateCalendar.value) }
    }

    val dateEventCountMap = remember(assignments) {
        val map = mutableMapOf<String, Int>()
        for (a in assignments) {
            val key = normalizeDateString(a.dueDate)
            if (key.isNotEmpty()) {
                map[key] = (map[key] ?: 0) + 1
            }
        }
        map
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandBlack,
                contentColor = BrandWhite,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header: Title & Subtitle
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Keep track of your schedule",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // Calendar Month Card
            item {
                CalendarMonthCard(
                    monthTitle = displayedMonthTitle,
                    daysInGrid = daysInGrid,
                    selectedCalendar = selectedDateCalendar.value,
                    dateEventCountMap = dateEventCountMap,
                    onPreviousMonth = {
                        val cal = displayedCalendar.value.clone() as Calendar
                        cal.add(Calendar.MONTH, -1)
                        displayedCalendar.value = cal
                    },
                    onNextMonth = {
                        val cal = displayedCalendar.value.clone() as Calendar
                        cal.add(Calendar.MONTH, 1)
                        displayedCalendar.value = cal
                    },
                    onTodayClick = {
                        val now = Calendar.getInstance()
                        displayedCalendar.value = now.clone() as Calendar
                        selectedDateCalendar.value = now.clone() as Calendar
                    },
                    onDayClick = { day ->
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, day.year)
                            set(Calendar.MONTH, day.month)
                            set(Calendar.DAY_OF_MONTH, day.dayNumber)
                        }
                        selectedDateCalendar.value = cal
                    }
                )
            }

            // Schedule Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSameDayCalendar(selectedDateCalendar.value, todayCalendar)) "Today's Schedule" else "Schedule",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = selectedDateTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = BrandGraySubtle,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${selectedDayAssignments.size} ${if (selectedDayAssignments.size == 1) "item" else "items"}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Schedule Items / Empty State
            if (selectedDayAssignments.isEmpty()) {
                item {
                    EmptyScheduleCard(
                        selectedDate = selectedDateShort,
                        onAddClick = { showAddDialog = true }
                    )
                }
            } else {
                items(selectedDayAssignments, key = { it.id }) { assignment ->
                    ScheduleItemCard(
                        assignment = assignment,
                        onStatusToggle = {
                            val newStatus = if (assignment.status.equals("Completed", ignoreCase = true)) "Pending" else "Completed"
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    database.assignmentDao().updateAssignment(assignment.copy(status = newStatus))
                                }
                                refreshAssignments()
                            }
                        },
                        onLongClick = {
                            assignmentToDelete = assignment
                        }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog (on hold / long press)
    assignmentToDelete?.let { assignment ->
        AlertDialog(
            onDismissRequest = { assignmentToDelete = null },
            title = {
                Text(
                    text = "Delete Assignment",
                    fontWeight = FontWeight.Bold,
                    color = BrandBlack
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${assignment.title}'?",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                database.assignmentDao().deleteAssignment(assignment)
                            }
                            refreshAssignments()
                            assignmentToDelete = null
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
                ) {
                    Text("Delete", color = BrandWhite)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { assignmentToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", color = BrandBlack)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Add Event Dialog
    if (showAddDialog) {
        AddEventDialog(
            initialDate = selectedDateShort,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, subject, dueDate ->
                coroutineScope.launch {
                    val newAssignment = Assignment(
                        title = title,
                        subject = subject,
                        dueDate = dueDate,
                        status = "Pending"
                    )
                    withContext(Dispatchers.IO) {
                        database.assignmentDao().insertAssignment(newAssignment)
                    }
                    refreshAssignments()
                    showAddDialog = false
                }
            }
        )
    }
}
