package com.example.studentorganizer.compose.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentorganizer.ui.theme.*
import java.util.Calendar

val AttendanceGreen = Color(0xFF2E7D32)
val AttendanceRed = Color(0xFFD32F2F)

@Composable
fun CalendarMonthCard(
    monthTitle: String,
    daysInGrid: List<DayModel>,
    selectedCalendar: Calendar,
    dateEventCountMap: Map<String, Int>,
    attendanceMap: Map<String, String>, // "PRESENT" or "ABSENT"
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onTodayClick: () -> Unit = {},
    onDayClick: (DayModel) -> Unit = {},
    onMarkPresent: (DayModel) -> Unit = {},
    onMarkAbsent: (DayModel) -> Unit = {},
    onClearAttendance: (DayModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Month Header & Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(
                        onClick = onTodayClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onPreviousMonth,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weekdays Row
            val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekdays.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (dayName == "Sun" || dayName == "Sat") TextSecondary else TextMuted,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days Grid
            daysInGrid.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { day ->
                        val isSelected = isSameDay(day, selectedCalendar)
                        val eventCount = dateEventCountMap[normalizeDateString(day.dateString)] ?: 0
                        val attendanceStatus = attendanceMap[normalizeDateString(day.dateString)]

                        DayCell(
                            day = day,
                            isSelected = isSelected,
                            hasEvents = eventCount > 0,
                            attendanceStatus = attendanceStatus,
                            onClick = { onDayClick(day) },
                            onMarkPresent = { onMarkPresent(day) },
                            onMarkAbsent = { onMarkAbsent(day) },
                            onClearAttendance = { onClearAttendance(day) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Attendance Heatmap Legend
            HorizontalDivider(color = BrandGrayLight.copy(alpha = 0.6f), thickness = 0.8.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AttendanceGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Present (2× tap)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AttendanceRed)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Absent (3× tap)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .border(1.dp, BrandGrayMedium, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hold to clear",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: DayModel,
    isSelected: Boolean,
    hasEvents: Boolean,
    attendanceStatus: String?,
    onClick: () -> Unit,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onClearAttendance: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f, label = "dayScale")

    val targetBgColor = when (attendanceStatus) {
        "PRESENT" -> AttendanceGreen
        "ABSENT" -> AttendanceRed
        else -> when {
            isSelected -> BrandBlack
            day.isToday -> BrandGraySubtle
            else -> Color.Transparent
        }
    }

    val backgroundColor by animateColorAsState(targetValue = targetBgColor, label = "dayBgColor")

    val textColor = when {
        attendanceStatus != null -> BrandWhite
        isSelected -> BrandWhite
        !day.isCurrentMonth -> TextMuted.copy(alpha = 0.4f)
        day.isToday -> BrandBlack
        else -> MaterialTheme.colorScheme.onSurface
    }

    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    val borderModifier = if (isSelected && attendanceStatus != null) {
        Modifier.border(2.dp, BrandBlack, RoundedCornerShape(10.dp))
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .then(borderModifier)
            .background(backgroundColor)
            .pointerInput(day.dateString) {
                detectTapGestures(
                    onTap = {
                        val now = System.currentTimeMillis()
                        if (now - lastTapTime < 380) {
                            tapCount++
                        } else {
                            tapCount = 1
                        }
                        lastTapTime = now

                        when (tapCount) {
                            1 -> {
                                onClick()
                            }
                            2 -> {
                                onMarkPresent()
                            }
                            3 -> {
                                onMarkAbsent()
                                tapCount = 0
                            }
                            else -> {
                                tapCount = 0
                            }
                        }
                    },
                    onLongPress = {
                        onClearAttendance()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${day.dayNumber}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || day.isToday || attendanceStatus != null) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )

            if (hasEvents) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected || attendanceStatus != null) BrandWhite else BrandBlack)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Calendar Month Card Preview")
@Composable
fun CalendarMonthCardPreview() {
    val sampleDays = (1..30).map { dayNum ->
        DayModel(
            dayNumber = dayNum,
            month = 8,
            year = 2026,
            isCurrentMonth = true,
            isToday = dayNum == 15,
            dateString = "2026-09-${dayNum.toString().padStart(2, '0')}"
        )
    }

    StudentOrganizerTheme {
        CalendarMonthCard(
            monthTitle = "September 2026",
            daysInGrid = sampleDays,
            selectedCalendar = Calendar.getInstance(),
            dateEventCountMap = mapOf("2026-09-15" to 2, "2026-09-18" to 1),
            attendanceMap = mapOf("2026-09-10" to "PRESENT", "2026-09-12" to "ABSENT", "2026-09-15" to "PRESENT")
        )
    }
}
