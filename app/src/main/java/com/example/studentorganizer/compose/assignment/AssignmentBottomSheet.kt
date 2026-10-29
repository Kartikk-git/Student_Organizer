package com.example.studentorganizer.compose.assignment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.ui.theme.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class AssignmentBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var initialAssignment: Assignment? = null
    private var availableCourses: List<Course> = emptyList()
    private var onSaveListener: ((title: String, subject: String, dueDate: String, status: String) -> Unit)? = null

    companion object {
        fun newInstance(
            assignment: Assignment? = null,
            courses: List<Course> = emptyList(),
            onSave: (title: String, subject: String, dueDate: String, status: String) -> Unit
        ): AssignmentBottomSheetDialogFragment {
            return AssignmentBottomSheetDialogFragment().apply {
                this.initialAssignment = assignment
                this.availableCourses = courses
                this.onSaveListener = onSave
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as? BottomSheetDialog
        val sheet = d?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        sheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            it.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
        d?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                StudentOrganizerTheme {
                    AssignmentBottomSheetContent(
                        initialAssignment = initialAssignment,
                        availableCourses = availableCourses,
                        onDismiss = { dismiss() },
                        onSave = { title, subject, dueDate, status ->
                            onSaveListener?.invoke(title, subject, dueDate, status)
                            dismiss()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentBottomSheetContent(
    initialAssignment: Assignment? = null,
    availableCourses: List<Course> = emptyList(),
    onDismiss: () -> Unit = {},
    onSave: (title: String, subject: String, dueDate: String, status: String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialAssignment?.title ?: "") }
    var subject by remember { mutableStateOf(initialAssignment?.subject ?: "") }
    var dueDate by remember { mutableStateOf(initialAssignment?.dueDate ?: "") }
    var status by remember { mutableStateOf(initialAssignment?.status ?: "Pending") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    // Only suggest courses actually added into Room DB
    val subjectOptions = remember(availableCourses) {
        availableCourses.map { "${it.code} - ${it.name}" }.distinct()
    }

    // Function to open Date Picker Dialog
    fun showDatePicker() {
        val cal = Calendar.getInstance()
        try {
            val parsed = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).parse(dueDate)
            if (parsed != null) cal.time = parsed
        } catch (_: Exception) {
            // keep current date
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
                errorMessage = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Drag bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .background(BrandGrayMedium.copy(alpha = 0.4f), shape = RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (initialAssignment == null) "Add Assignment" else "Edit Assignment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlack
                )
                Text(
                    text = if (initialAssignment == null) "Track pending tasks and deadlines" else "Update task deadlines and status",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandGrayMedium
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(32.dp)
                    .background(BrandGraySubtle, RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = BrandBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subject / Course Code Field (Room dropdown + custom text input)
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = subject,
                onValueChange = {
                    subject = it
                    errorMessage = null
                },
                label = { Text("Subject / Course Code") },
                placeholder = { Text("e.g. CS301 (or type custom subject)") },
                singleLine = true,
                trailingIcon = {
                    if (subjectOptions.isNotEmpty()) {
                        IconButton(onClick = { showDropdown = !showDropdown }) {
                            Icon(
                                imageVector = if (showDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Toggle subject dropdown",
                                tint = BrandBlack
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            AnimatedVisibility(visible = showDropdown && subjectOptions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "Enrolled Courses",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandGrayMedium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
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
                                        errorMessage = null
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name.ifBlank { code },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = BrandBlack,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = BrandBlack,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = code,
                                        color = BrandWhite,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = null
            },
            label = { Text("Assignment Title") },
            placeholder = { Text("e.g. Relational Calculus & SQL Task") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Due Date with Calendar picker
        OutlinedTextField(
            value = dueDate,
            onValueChange = {
                dueDate = it
                errorMessage = null
            },
            label = { Text("Due Date") },
            placeholder = { Text("Select date from calendar or type") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick Due Date",
                        tint = BrandBlack
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker() },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Selection
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = BrandBlack
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isPending = status.equals("Pending", ignoreCase = true)
            val isCompleted = status.equals("Completed", ignoreCase = true)

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { status = "Pending" },
                color = if (isPending) BrandBlack else BrandGraySubtle,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Pending",
                    color = if (isPending) BrandWhite else BrandDarkGray,
                    fontWeight = if (isPending) FontWeight.Bold else FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { status = "Completed" },
                color = if (isCompleted) BrandBlack else BrandGraySubtle,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Completed",
                    color = if (isCompleted) BrandWhite else BrandDarkGray,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = BrandBlack)
            }

            Button(
                onClick = {
                    if (subject.isBlank()) {
                        errorMessage = "Please enter or select a Subject/Course"
                        return@Button
                    }
                    if (title.isBlank()) {
                        errorMessage = "Please enter an Assignment Title"
                        return@Button
                    }
                    if (dueDate.isBlank()) {
                        errorMessage = "Please enter or pick a Due Date"
                        return@Button
                    }
                    onSave(title.trim(), subject.trim(), dueDate.trim(), status)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
            ) {
                Text(
                    text = if (initialAssignment == null) "Add Assignment" else "Save Changes",
                    color = BrandWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "Assignment Bottom Sheet Preview")
@Composable
fun AssignmentBottomSheetPreview() {
    StudentOrganizerTheme {
        AssignmentBottomSheetContent(
            initialAssignment = null,
            availableCourses = listOf(
                Course(id = 1, name = "Data Structures", code = "CS301", progress = 60),
                Course(id = 2, name = "Database Systems", code = "CS204", progress = 80)
            )
        )
    }
}
