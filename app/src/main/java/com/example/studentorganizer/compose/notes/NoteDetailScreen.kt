package com.example.studentorganizer.compose.notes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Notes,
    onBackClick: () -> Unit,
    onEditNote: (Notes) -> Unit,
    onDeleteNote: (Notes) -> Unit
) {
    val context = LocalContext.current
    val wordsCount = remember(note.content) {
        if (note.content.isBlank()) 0 else note.content.trim().split("\\s+".toRegex()).size
    }
    val charCount = remember(note.content) { note.content.length }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = note.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Notes",
                            tint = BrandBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Study Note", "${note.title}\n\n${note.content}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Note copied to clipboard", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Note",
                            tint = BrandBlack
                        )
                    }
                    IconButton(onClick = { onEditNote(note) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Note",
                            tint = BrandBlack
                        )
                    }
                    IconButton(onClick = { onDeleteNote(note) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = BrandBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandWhite
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Subject Badge and Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandBlack,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = note.subject,
                        color = BrandWhite,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = BrandGrayMedium,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = note.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandGrayMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note Title
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrandBlack,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reading Stats Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandGraySubtle,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$wordsCount words",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandDarkGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = BrandGraySubtle,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$charCount characters",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandDarkGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Note Content Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = BrandBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Content",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack
                        )
                    }

                    HorizontalDivider(color = BrandGrayLight, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = BrandDarkGray,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onDeleteNote(note) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = BrandBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", color = BrandBlack)
                }

                Button(
                    onClick = { onEditNote(note) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = BrandWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Note", color = BrandWhite)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Note Detail Screen Preview")
@Composable
fun NoteDetailScreenPreview() {
    val sampleNote = Notes(
        id = 1,
        title = "Binary Search Trees & AVL",
        subject = "CS301",
        content = "Binary search tree properties:\n1. Left subtree contains keys smaller than root\n2. Right subtree contains keys greater than root\n3. AVL trees maintain balance factor between -1 and +1.\n\nRotations:\n- LL Rotation: Single Right Rotation\n- RR Rotation: Single Left Rotation\n- LR Rotation: Left-Right Rotation\n- RL Rotation: Right-Left Rotation",
        date = "15 September 2026"
    )

    StudentOrganizerTheme {
        NoteDetailScreen(
            note = sampleNote,
            onBackClick = {},
            onEditNote = {},
            onDeleteNote = {}
        )
    }
}

