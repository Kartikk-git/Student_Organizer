package com.example.studentorganizer.compose.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.ui.theme.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.ui.platform.LocalContext
import com.example.studentorganizer.utils.AttachmentUtils

class NoteBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var initialNote: Notes? = null
    private var availableCourses: List<Course> = emptyList()
    private var onSaveListener: ((title: String, subject: String, content: String, attachmentUri: String?, attachmentName: String?, attachmentType: String?) -> Unit)? = null

    companion object {
        fun newInstance(
            note: Notes? = null,
            courses: List<Course> = emptyList(),
            onSave: (title: String, subject: String, content: String, attachmentUri: String?, attachmentName: String?, attachmentType: String?) -> Unit
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
                    NoteBottomSheetContent(
                        initialNote = initialNote,
                        availableCourses = availableCourses,
                        onDismiss = { dismiss() },
                        onSave = { title, subject, content, attachmentUri, attachmentName, attachmentType ->
                            onSaveListener?.invoke(title, subject, content, attachmentUri, attachmentName, attachmentType)
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
    onSave: (title: String, subject: String, content: String, attachmentUri: String?, attachmentName: String?, attachmentType: String?) -> Unit = { _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var subject by remember { mutableStateOf(initialNote?.subject ?: "") }
    var content by remember { mutableStateOf(initialNote?.content ?: "") }
    var attachmentUri by remember { mutableStateOf(initialNote?.attachmentUri) }
    var attachmentName by remember { mutableStateOf(initialNote?.attachmentName) }
    var attachmentType by remember { mutableStateOf(initialNote?.attachmentType) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val res = AttachmentUtils.saveAttachmentFromUri(context, uri)
            if (res != null) {
                attachmentUri = res.filePath
                attachmentName = res.fileName
                attachmentType = "IMAGE"
                errorMessage = null
            }
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val res = AttachmentUtils.saveAttachmentFromUri(context, uri)
            if (res != null) {
                attachmentUri = res.filePath
                attachmentName = res.fileName
                attachmentType = "PDF"
                errorMessage = null
            }
        }
    }

    // Only show courses that have actually been added to Room DB
    val subjectOptions = remember(availableCourses) {
        availableCourses.map { "${it.code} - ${it.name}" }.distinct()
    }
    val courseCodes = remember(availableCourses) {
        availableCourses.map { it.code.trim() }.filter { it.isNotBlank() }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Drag indicator bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .background(BrandGrayMedium.copy(alpha = 0.35f), shape = RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (initialNote == null) "New Study Note" else "Edit Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlack
                )
                Text(
                    text = if (initialNote == null) "Record key concepts, formulas, and revision remarks" else "Update your study note details",
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

        Spacer(modifier = Modifier.height(18.dp))

        // Subject / Course Selection Header
        Text(
            text = "SUBJECT / COURSE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Course Select Chips (if courses are available in DB)
        if (courseCodes.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                courseCodes.forEach { code ->
                    val isSelected = subject.trim().equals(code, ignoreCase = true)
                    Surface(
                        modifier = Modifier.clickable {
                            subject = code
                            errorMessage = null
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) BrandBlack else SurfaceSubtle,
                        border = if (isSelected) null else BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Text(
                            text = code,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandWhite else BrandBlack,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Subject / Course Code Input (with dropdown support)
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = subject,
                onValueChange = {
                    subject = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. CS301 (or enter custom subject)", color = TextMuted) },
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlack,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedContainerColor = SurfaceLight,
                    unfocusedContainerColor = SurfaceSubtle,
                    cursorColor = BrandBlack
                ),
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
                            .heightIn(max = 200.dp)
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
        Text(
            text = "NOTE TITLE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = null
            },
            placeholder = { Text("e.g. Binary Search Trees & AVL", color = TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlack,
                unfocusedBorderColor = SurfaceBorder,
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceSubtle,
                cursorColor = BrandBlack
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Note Content Header with Live Word Counter
        val wordCount = remember(content) {
            if (content.isBlank()) 0 else content.trim().split(Regex("\\s+")).count { it.isNotBlank() }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NOTE CONTENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Insert Tags Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "• Bullet" to "\n• ",
                "📌 Key Point" to "\n📌 ",
                "💡 Formula" to "\n💡 Formula: ",
                "⚠️ Exam Tip" to "\n⚠️ Exam Tip: "
            ).forEach { (label, snippet) ->
                Surface(
                    modifier = Modifier.clickable {
                        content = if (content.isEmpty()) snippet.trimStart() else content + snippet
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceSubtle,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Note Content Text Area
        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it
                errorMessage = null
            },
            placeholder = {
                Text(
                    "Write your key notes, formulas, syllabus points, and study remarks...",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            minLines = 6,
            maxLines = 12,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlack,
                unfocusedBorderColor = SurfaceBorder,
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceSubtle,
                cursorColor = BrandBlack
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Attachments Section (Photo / PDF)
        Text(
            text = "ATTACHMENTS (PHOTO / PDF)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (attachmentUri == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { imageLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SurfaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlack)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Attach Photo",
                        tint = BrandBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = { pdfLauncher.launch("application/pdf") },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SurfaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlack)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Attach PDF",
                        tint = BrandBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add PDF", style = MaterialTheme.typography.labelMedium)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSubtle),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = if (attachmentType == "PDF") androidx.compose.ui.graphics.Color(0xFFD32F2F) else BrandBlack,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (attachmentType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                                contentDescription = null,
                                tint = BrandWhite,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = attachmentName ?: (if (attachmentType == "PDF") "PDF Document" else "Photo Attachment"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandBlack,
                                maxLines = 1
                            )
                            Text(
                                text = if (attachmentType == "PDF") "PDF File Attached" else "Photo Attached",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandGrayMedium
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            attachmentUri = null
                            attachmentName = null
                            attachmentType = null
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove attachment",
                            tint = BrandGrayMedium,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
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
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BrandGrayLight),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlack)
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold)
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
                    onSave(title.trim(), subject.trim(), content.trim(), attachmentUri, attachmentName, attachmentType)
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
