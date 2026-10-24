package com.example.studentorganizer.compose.notes

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.ui.theme.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoteBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var initialNote: Notes? = null
    private var availableCourses: List<Course> = emptyList()
    private var onSaveListener: ((title: String, subject: String, content: String) -> Unit)? = null

    companion object {
        fun newInstance(
            note: Notes? = null,
            courses: List<Course> = emptyList(),
            onSave: (title: String, subject: String, content: String) -> Unit
        ): NoteBottomSheetDialogFragment {
            return NoteBottomSheetDialogFragment().apply {
                this.initialNote = note
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
                    NoteBottomSheetContent(
                        initialNote = initialNote,
                        availableCourses = availableCourses,
                        onDismiss = { dismiss() },
                        onSave = { title, subject, content ->
                            onSaveListener?.invoke(title, subject, content)
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
fun NoteBottomSheetContent(
    initialNote: Notes? = null,
    availableCourses: List<Course> = emptyList(),
    onDismiss: () -> Unit = {},
    onSave: (title: String, subject: String, content: String) -> Unit = { _, _, _ -> }
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var subject by remember { mutableStateOf(initialNote?.subject ?: "") }
    var content by remember { mutableStateOf(initialNote?.content ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    // Only show courses that have actually been added to Room DB
    val subjectOptions = remember(availableCourses) {
        availableCourses.map { "${it.code} - ${it.name}" }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
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

        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (initialNote == null) "Add Note" else "Edit Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlack
                )
                Text(
                    text = if (initialNote == null) "Organize your study notes by subject" else "Update your existing study note",
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

        // Subject / Course Selection (Dropdown + text input)
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = subject,
                onValueChange = {
                    subject = it
                    errorMessage = null
                },
                label = { Text("Subject / Course Code") },
                placeholder = { Text("e.g. CS301 (or enter custom subject)") },
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

        // Note Title
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = null
            },
            label = { Text("Note Title") },
            placeholder = { Text("e.g. Binary Search Trees & AVL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Note Content
        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it
                errorMessage = null
            },
            label = { Text("Note Content") },
            placeholder = { Text("Write your key notes, formulas, syllabus points, and study remarks...") },
            minLines = 5,
            maxLines = 10,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
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
                        errorMessage = "Please enter a Note Title"
                        return@Button
                    }
                    if (content.isBlank()) {
                        errorMessage = "Please enter Note Content"
                        return@Button
                    }
                    onSave(title.trim(), subject.trim(), content.trim())
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
            ) {
                Text(
                    text = if (initialNote == null) "Add Note" else "Save Changes",
                    color = BrandWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Note Bottom Sheet - Add Mode")
@Composable
fun NoteBottomSheetAddPreview() {
    StudentOrganizerTheme {
        NoteBottomSheetContent(
            initialNote = null,
            availableCourses = listOf(
                Course(id = 1, name = "Data Structures", code = "CS301", progress = 50)
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Note Bottom Sheet - Edit Mode")
@Composable
fun NoteBottomSheetEditPreview() {
    val sampleNote = Notes(
        id = 1,
        title = "Binary Search Trees & AVL",
        subject = "CS301",
        content = "Self-balancing binary search trees maintain O(log n) worst-case time for insertions and lookups through rotational operations.",
        date = "15 September 2026"
    )

    StudentOrganizerTheme {
        NoteBottomSheetContent(
            initialNote = sampleNote,
            availableCourses = listOf(
                Course(id = 1, name = "Data Structures", code = "CS301", progress = 50)
            )
        )
    }
}
