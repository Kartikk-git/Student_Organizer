package com.example.studentorganizer.compose.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.compose.course.PRESET_COURSES
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignment: Assignment,
    onBackClick: () -> Unit = {},
    onEditAssignment: (Assignment) -> Unit = {},
    onDeleteAssignment: (Assignment) -> Unit = {},
    onToggleStatus: (Assignment) -> Unit = {}
) {
    val isCompleted = assignment.status.equals("Completed", ignoreCase = true)
    val matchedPreset = PRESET_COURSES.firstOrNull {
        it.defaultCode.equals(assignment.subject, ignoreCase = true) ||
                it.name.contains(assignment.subject, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = assignment.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Assignments",
                            tint = BrandBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditAssignment(assignment) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Assignment",
                            tint = BrandBlack
                        )
                    }
                    IconButton(onClick = { onDeleteAssignment(assignment) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Assignment",
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
            // Subject & Status Badges
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
                        text = assignment.subject,
                        color = BrandWhite,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isCompleted) "✓ Completed" else "⏳ Pending",
                        color = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFE65100),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assignment Title
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrandBlack,
                lineHeight = 32.sp
            )

            if (matchedPreset != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = matchedPreset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Due Date Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandGraySubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = BrandBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Due Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandGrayMedium
                            )
                            Text(
                                text = assignment.dueDate,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BrandGrayLight, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Toggle Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) Color(0xFFE8F5E9) else BrandGraySubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (isCompleted) Color(0xFF2E7D32) else BrandGrayMedium,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Current Status",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandGrayMedium
                                )
                                Text(
                                    text = if (isCompleted) "Completed" else "Pending Submission",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlack
                                )
                            }
                        }

                        Button(
                            onClick = { onToggleStatus(assignment) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) BrandGraySubtle else BrandBlack,
                                contentColor = if (isCompleted) BrandBlack else BrandWhite
                            )
                        ) {
                            Text(
                                text = if (isCompleted) "Mark Pending" else "Mark Complete",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onDeleteAssignment(assignment) },
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
                    onClick = { onEditAssignment(assignment) },
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
                    Text("Edit", color = BrandWhite)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Assignment Detail Screen Preview")
@Composable
fun AssignmentDetailScreenPreview() {
    StudentOrganizerTheme {
        AssignmentDetailScreen(
            assignment = Assignment(
                id = 1,
                title = "Database Normalization & SQL Queries",
                subject = "CS302",
                dueDate = "15 September 2026",
                status = "Pending"
            )
        )
    }
}
