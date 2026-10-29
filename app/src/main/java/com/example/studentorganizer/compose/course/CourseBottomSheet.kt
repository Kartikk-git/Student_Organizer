package com.example.studentorganizer.compose.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.ui.theme.*
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CourseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var initialCourse: Course? = null
    private var onSaveListener: ((name: String, code: String, progress: Int) -> Unit)? = null

    companion object {
        fun newInstance(
            course: Course? = null,
            onSave: (name: String, code: String, progress: Int) -> Unit
        ): CourseBottomSheetDialogFragment {
            return CourseBottomSheetDialogFragment().apply {
                this.initialCourse = course
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
                    CourseBottomSheetContent(
                        initialCourse = initialCourse,
                        onDismiss = { dismiss() },
                        onSave = { name, code, progress ->
                            onSaveListener?.invoke(name, code, progress)
                            dismiss()
                        }
                    )
                }
            }
        }
    }
}

data class CoursePreset(
    val name: String,
    val defaultCode: String,
    val description: String = ""
)

val PRESET_COURSES = listOf(
    CoursePreset("Data Structures & Algorithms", "CS301", "Arrays, Linked Lists, Trees, Graphs, Sorting & Searching Algorithms."),
    CoursePreset("Database Management System", "CS302", "Relational Database Design, SQL, Normalization, ACID Properties & Transactions."),
    CoursePreset("Operating System", "CS303", "Processes, Threads, CPU Scheduling, Synchronization, Memory Management."),
    CoursePreset("Digital Electronics", "CS304", "Logic Gates, Boolean Algebra, Combinational and Sequential Circuits."),
    CoursePreset("Computer Networks", "CS305", "OSI & TCP/IP Models, Routing, Transport Protocols, IP Addressing, Security."),
    CoursePreset("Software Engineering", "CS306", "SDLC Models, Requirements, Agile Development, Testing & Maintenance."),
    CoursePreset("Artificial Intelligence", "CS307", "Search Algorithms, Knowledge Representation, Heuristics, Machine Learning basics."),
    CoursePreset("Web Development", "CS308", "Frontend & Backend Web Technologies, REST APIs, responsive design."),
    CoursePreset("Machine Learning", "CS309", "Supervised/Unsupervised Learning, Regression, Classification, Model Evaluation."),
    CoursePreset("Advanced Mathematics", "MATH201", "Linear Algebra, Calculus, Probability and Numerical Analysis."),
    CoursePreset("Object Oriented Programming", "CS201", "Classes, Objects, Inheritance, Polymorphism, Design Patterns in Java/C++.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseBottomSheetContent(
    initialCourse: Course? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, code: String, progress: Int) -> Unit
) {
    var courseName by remember { mutableStateOf(initialCourse?.name ?: "") }
    var courseCode by remember { mutableStateOf(initialCourse?.code ?: "") }
    var progress by remember { mutableFloatStateOf((initialCourse?.progress ?: 0).toFloat()) }
    var isUserModifiedCode by remember { mutableStateOf(initialCourse != null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    fun autoSuggestCode(name: String) {
        if (!isUserModifiedCode) {
            val matched = PRESET_COURSES.firstOrNull { preset ->
                preset.name.contains(name.trim(), ignoreCase = true) ||
                        name.trim().contains(preset.name, ignoreCase = true)
            }
            if (matched != null) {
                courseCode = matched.defaultCode
            } else if (name.isNotBlank() && courseCode.isBlank()) {
                val words = name.trim().split(" ")
                val prefix = words.mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
                courseCode = if (prefix.length >= 2) "${prefix}101" else "CS101"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Drag indicator bar
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
            Text(
                text = if (initialCourse == null) "Add Course" else "Update Course",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrandBlack
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = BrandDarkGray
                )
            }
        }

        Text(
            text = "Select from course options or type your custom course details.",
            style = MaterialTheme.typography.bodySmall,
            color = BrandGrayMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Course Title Input Field with Seamless Connected Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = courseName,
                onValueChange = {
                    courseName = it
                    errorMessage = null
                    autoSuggestCode(it)
                },
                label = { Text("Course Title") },
                placeholder = { Text("e.g. Data Structures & Algorithms") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showDropdown = !showDropdown }) {
                        Icon(
                            imageVector = if (showDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Show Courses List",
                            tint = BrandBlack
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = if (showDropdown) {
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                } else {
                    RoundedCornerShape(12.dp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlack,
                    unfocusedBorderColor = if (showDropdown) BrandBlack else BrandGrayMedium.copy(alpha = 0.5f),
                    focusedLabelColor = BrandBlack
                )
            )

            // Seamlessly Connected Suggestions Panel
            AnimatedVisibility(visible = showDropdown) {
                val filteredPresets = PRESET_COURSES.filter {
                    courseName.isBlank() || it.name.contains(courseName, ignoreCase = true)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-1).dp)
                        .border(
                            width = 1.dp,
                            color = BrandBlack,
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        )
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .heightIn(max = 220.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (filteredPresets.isEmpty()) {
                        Text(
                            text = "No matching presets. You can type your custom course title.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandGrayMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                    } else {
                        filteredPresets.forEachIndexed { index, preset ->
                            if (index > 0) {
                                HorizontalDivider(color = BrandGrayLight, thickness = 0.5.dp)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        courseName = preset.name
                                        courseCode = preset.defaultCode
                                        isUserModifiedCode = false
                                        showDropdown = false
                                        errorMessage = null
                                    }
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = BrandBlack
                                )
                                Surface(
                                    color = BrandBlack,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = preset.defaultCode,
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

        // Course Code Field
        OutlinedTextField(
            value = courseCode,
            onValueChange = {
                courseCode = it
                isUserModifiedCode = true
                errorMessage = null
            },
            label = { Text("Subject Code") },
            placeholder = { Text("e.g. CS301") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Course Progress",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = BrandBlack
            )
            Surface(
                color = BrandBlack,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${progress.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandWhite,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = progress,
            onValueChange = { progress = it },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = BrandBlack,
                activeTrackColor = BrandBlack,
                inactiveTrackColor = BrandGrayLight
            ),
            modifier = Modifier.fillMaxWidth()
        )


        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Buttons
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
                    if (courseName.isBlank()) {
                        errorMessage = "Please enter or select a course title"
                        return@Button
                    }
                    if (courseCode.isBlank()) {
                        errorMessage = "Please enter a subject code"
                        return@Button
                    }
                    onSave(courseName.trim(), courseCode.trim().uppercase(), progress.toInt())
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
            ) {
                Text(
                    text = if (initialCourse == null) "Add Course" else "Update",
                    color = BrandWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -----------------------------------------------------------------------------
// Compose Previews for Android Studio Design & Live Editing
// -----------------------------------------------------------------------------

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Course BottomSheet - Add Mode")
@Composable
fun CourseBottomSheetAddPreview() {
    StudentOrganizerTheme {
        CourseBottomSheetContent(
            initialCourse = null,
            onDismiss = {},
            onSave = { _, _, _ -> }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Course BottomSheet - Edit Mode")
@Composable
fun CourseBottomSheetEditPreview() {
    val sampleCourse = Course(
        id = 1,
        name = "Database Management System",
        code = "CS302",
        progress = 75
    )

    StudentOrganizerTheme {
        CourseBottomSheetContent(
            initialCourse = sampleCourse,
            onDismiss = {},
            onSave = { _, _, _ -> }
        )
    }
}

