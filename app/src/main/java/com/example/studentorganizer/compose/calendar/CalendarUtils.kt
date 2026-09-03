package com.example.studentorganizer.compose.calendar

import androidx.compose.foundation.style.apply
import java.text.SimpleDateFormat
import java.util.*

data class DayModel(
    val dayNumber: Int,
    val month: Int,
    val year: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val dateString: String
)

fun generateMonthDays(year: Int, month: Int, today: Calendar): List<DayModel> {
    val days = mutableListOf<DayModel>()

    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday

    val prevMonthCal = (calendar.clone() as Calendar).apply {
        add(Calendar.MONTH, -1)
    }
    val maxDaysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val prevMonthDaysCount = firstDayOfWeek - 1

    for (i in (maxDaysInPrevMonth - prevMonthDaysCount + 1)..maxDaysInPrevMonth) {
        val dateStr = "$i/${prevMonthCal.get(Calendar.MONTH) + 1}/${prevMonthCal.get(Calendar.YEAR)}"
        days.add(
            DayModel(
                dayNumber = i,
                month = prevMonthCal.get(Calendar.MONTH),
                year = prevMonthCal.get(Calendar.YEAR),
                isCurrentMonth = false,
                isToday = false,
                dateString = dateStr
            )
        )
    }

    for (i in 1..maxDaysInMonth) {
        val isToday = today.get(Calendar.YEAR) == year &&
                today.get(Calendar.MONTH) == month &&
                today.get(Calendar.DAY_OF_MONTH) == i

        val dateStr = "$i/${month + 1}/$year"
        days.add(
            DayModel(
                dayNumber = i,
                month = month,
                year = year,
                isCurrentMonth = true,
                isToday = isToday,
                dateString = dateStr
            )
        )
    }

    val remaining = if (days.size <= 35) 35 - days.size else 42 - days.size
    val nextMonthCal = (calendar.clone() as Calendar).apply {
        add(Calendar.MONTH, 1)
    }

    for (i in 1..remaining) {
        val dateStr = "$i/${nextMonthCal.get(Calendar.MONTH) + 1}/${nextMonthCal.get(Calendar.YEAR)}"
        days.add(
            DayModel(
                dayNumber = i,
                month = nextMonthCal.get(Calendar.MONTH),
                year = nextMonthCal.get(Calendar.YEAR),
                isCurrentMonth = false,
                isToday = false,
                dateString = dateStr
            )
        )
    }

    return days
}

fun isSameDay(day: DayModel, target: Calendar): Boolean {
    return day.year == target.get(Calendar.YEAR) &&
            day.month == target.get(Calendar.MONTH) &&
            day.dayNumber == target.get(Calendar.DAY_OF_MONTH)
}

fun isSameDayCalendar(c1: Calendar, c2: Calendar): Boolean {
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

fun isDateMatching(dueDateString: String, selectedDate: Calendar): Boolean {
    val normalizedDue = normalizeDateString(dueDateString)
    val day = selectedDate.get(Calendar.DAY_OF_MONTH)
    val month = selectedDate.get(Calendar.MONTH) + 1
    val year = selectedDate.get(Calendar.YEAR)

    val compactStr = "$day/$month/$year"
    if (normalizedDue == compactStr) return true

    val formats = listOf("d MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "d MMM yyyy")
    for (f in formats) {
        val formatted = SimpleDateFormat(f, Locale.getDefault()).format(selectedDate.time)
        if (dueDateString.equals(formatted, ignoreCase = true)) return true
    }

    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(selectedDate.time)
    val shortMonthName = SimpleDateFormat("MMM", Locale.getDefault()).format(selectedDate.time)
    val hasDay = dueDateString.contains("$day")
    val hasMonth = dueDateString.contains(monthName, ignoreCase = true) || dueDateString.contains(shortMonthName, ignoreCase = true)

    return hasDay && hasMonth
}

fun normalizeDateString(dateStr: String): String {
    val trimmed = dateStr.trim()
    val parts = trimmed.split("/", "-")
    if (parts.size == 3) {
        val d = parts[0].toIntOrNull()
        val m = parts[1].toIntOrNull()
        val y = parts[2].toIntOrNull()
        if (d != null && m != null && y != null) {
            return "$d/$m/$y"
        }
    }
    return trimmed.lowercase(Locale.getDefault())
}
