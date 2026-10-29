package com.example.studentorganizer.compose.course

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    course: Course,
    assignments: List<Assignment>,
    notes: List<Notes>,
    onBackClick: () -> Unit,
    onEditCourse: (Course) -> Unit,
    onDeleteCourse: (Course) -> Unit,
    onToggleAssignmentStatus: (Assignment) -> Unit
) {
    val preset = PRESET_COURSES.firstOrNull {
        it.defaultCode.equals(course.code, ignoreCase = true) ||
                it.name.equals(course.name, ignoreCase = true)
    }
    val description = if (!preset?.description.isNullOrBlank()) {
        preset!!.description
    } else {
        "Comprehensive curriculum covering core subject competencies, practical applications, syllabus milestones, assignments, and study materials."
    }

    val completedAssignments = assignments.count { it.status.equals("Completed", ignoreCase = true) }
    val totalAssignments = assignments.size
    val assignmentPercentage = if (totalAssignments > 0) {
        ((completedAssignments.toDouble() / totalAssignments.toDouble()) * 100).toInt()
    } else {
        0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course.code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Courses",
                            tint = BrandBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditCourse(course) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Course",
                            tint = BrandBlack
                        )
                    }
                    IconButton(onClick = { onDeleteCourse(course) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Course",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Course Header Title Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandGraySubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Surface(
                        color = BrandBlack,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = course.code,
                            color = BrandWhite,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandDarkGray,
                        lineHeight = 20.sp
                    )
                }
            }

            // Syllabus & Academic Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandGraySubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Syllabus Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack
                        )
                        Text(
                            text = "${course.progress}%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { course.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = BrandBlack,
                        trackColor = BrandGrayLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (course.progress >= 80) "Advanced / Near Completion" else if (course.progress >= 40) "In Progress" else "Just Started",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandGrayMedium
                    )
                }
            }

            // Assignments Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandGraySubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assignments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack
                        )

                        Surface(
                            color = BrandBlack,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$completedAssignments / $totalAssignments Done",
                                color = BrandWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (totalAssignments > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { assignmentPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = BrandBlack,
                            trackColor = BrandGrayLight
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (assignments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No assignments created for this course yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandGrayMedium
                            )
                        }
                    } else {
                        assignments.forEach { assignment ->
                            val isDone = assignment.status.equals("Completed", ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(BackgroundLight, RoundedCornerShape(10.dp))
                                    .border(1.dp, BrandGrayLight, RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = assignment.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandBlack
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Due: ${assignment.dueDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BrandGrayMedium
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleAssignmentStatus(assignment) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = if (isDone) "Completed" else "Pending",
                                        tint = if (isDone) BrandBlack else BrandGrayMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notes & Study Materials Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandGraySubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Notes & Materials",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack
                        )

                        Surface(
                            color = BrandBlack,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${notes.size} Note(s)",
                                color = BrandWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (notes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No study notes or materials found for this course.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandGrayMedium
                            )
                        }
                    } else {
                        notes.forEach { note ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(BackgroundLight, RoundedCornerShape(10.dp))
                                    .border(1.dp, BrandGrayLight, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBlack
                                    )
                                    Text(
                                        text = note.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BrandGrayMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandDarkGray,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// Compose Previews for Android Studio Design & Live Editing
// -----------------------------------------------------------------------------

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Course Detail - Populated", widthDp = 390, heightDp = 844)
@Composable
fun CourseDetailScreenPreview() {
    val sampleCourse = Course(
        id = 1,
        name = "Data Structures & Algorithms",
        code = "CS301",
        progress = 68
    )

    val sampleAssignments = listOf(
        Assignment(
            id = 1,
            title = "Binary Search Tree Implementation",
            subject = "CS301",
            dueDate = "10 Oct 2026",
            status = "Completed"
        ),
        Assignment(
            id = 2,
            title = "Dijkstra's Algorithm & Graph Traversal",
            subject = "CS301",
            dueDate = "18 Oct 2026",
            status = "Pending"
        ),
        Assignment(
            id = 3,
            title = "Dynamic Programming Problem Set 4",
            subject = "CS301",
            dueDate = "25 Oct 2026",
            status = "Pending"
        )
    )

    val sampleNotes = listOf(
        Notes(
            id = 1,
            title = "Red-Black Trees & AVL Balance",
            subject = "CS301",
            date = "04 Oct 2026",
            content = "Self-balancing binary search trees maintain O(log n) worst-case time for insertions and lookups through rotational operations."
        ),
        Notes(
            id = 2,
            title = "Graph Shortest Path Summary",
            subject = "CS301",
            date = "02 Oct 2026",
            content = "Dijkstra uses priority queues for non-negative weights; Bellman-Ford handles negative edge weights."
        )
    )


    StudentOrganizerTheme {
        CourseDetailScreen(
            course = sampleCourse,
            assignments = sampleAssignments,
            notes = sampleNotes,
            onBackClick = {},
            onEditCourse = {},
            onDeleteCourse = {},
            onToggleAssignmentStatus = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Course Detail - Empty State", widthDp = 390, heightDp = 844)
@Composable
fun CourseDetailScreenEmptyPreview() {
    val sampleCourse = Course(
        id = 2,
        name = "Operating System",
        code = "CS303",
        progress = 25
    )

    StudentOrganizerTheme {
        CourseDetailScreen(
            course = sampleCourse,
            assignments = emptyList(),
            notes = emptyList(),
            onBackClick = {},
            onEditCourse = {},
            onDeleteCourse = {},
            onToggleAssignmentStatus = {}
        )
    }
}

