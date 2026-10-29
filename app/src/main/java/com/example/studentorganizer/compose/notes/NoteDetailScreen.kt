package com.example.studentorganizer.compose.notes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.ui.theme.*
import com.example.studentorganizer.utils.AttachmentUtils

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

            // Attachment Display Section
            if (!note.attachmentUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "ATTACHED MATERIAL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (note.attachmentType.equals("PDF", ignoreCase = true)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AttachmentUtils.openAttachment(context, note.attachmentUri, "PDF")
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandWhite),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = Color(0xFFD32F2F),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = BrandWhite,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = note.attachmentName ?: "PDF Document",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBlack,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Tap to open and read PDF document",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandGrayMedium
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open PDF",
                                tint = BrandBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    // Image attachment
                    val bitmap = remember(note.attachmentUri) {
                        try {
                            BitmapFactory.decodeFile(note.attachmentUri)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AttachmentUtils.openAttachment(context, note.attachmentUri, "IMAGE")
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandWhite),
                        border = BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Column {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = note.attachmentName ?: "Attached image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 260.dp)
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = BrandBlack,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = note.attachmentName ?: "Attached Photo",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandBlack
                                    )
                                }
                                Text(
                                    text = "Tap to view full",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandGrayMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
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

