package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.studentorganizer.R
import com.example.studentorganizer.adapter.CourseAdapter
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

import com.example.studentorganizer.activities.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()
    private val assignmentList = mutableListOf<Assignment>()
    private val notesList = mutableListOf<Notes>()
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        setupAdapter()
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter

        setupNavigationListeners()
        loadDashboardData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun setupAdapter() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.tab_viewpager)

        courseAdapter = CourseAdapter(
            courseList = courseList,
            onItemClick = {
                viewPager?.currentItem = 1
            },
            onMoreOptionsClick = { _, anchorView ->
                val popup = PopupMenu(requireContext(), anchorView)
                popup.menu.add(0, 1, 0, "Go to Courses")
                popup.setOnMenuItemClickListener {
                    viewPager?.currentItem = 1
                    true
                }
                popup.show()
            },
            getAssignmentStats = { course ->
                val courseCode = course.code.trim().lowercase()
                val courseName = course.name.trim().lowercase()

                val matchedAssignments = assignmentList.filter { assignment ->
                    val subj = assignment.subject.trim().lowercase()
                    val title = assignment.title.trim().lowercase()
                    (courseCode.isNotEmpty() && subj.isNotEmpty() && (subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj))) ||
                    (courseName.isNotEmpty() && subj.isNotEmpty() && (courseName == subj || courseName.contains(subj) || subj.contains(courseName))) ||
                    (courseCode.isNotEmpty() && title.contains(courseCode)) ||
                    (courseName.isNotEmpty() && title.contains(courseName))
                }

                val total = matchedAssignments.size
                val completed = matchedAssignments.count { it.status.equals("Completed", ignoreCase = true) }
                Pair(completed, total)
            },
            getNotesCount = { course ->
                val courseCode = course.code.trim().lowercase()
                val courseName = course.name.trim().lowercase()

                notesList.count { note ->
                    val subj = note.subject.trim().lowercase()
                    val title = note.title.trim().lowercase()
                    (courseCode.isNotEmpty() && subj.isNotEmpty() && (subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj))) ||
                    (courseName.isNotEmpty() && subj.isNotEmpty() && (courseName == subj || courseName.contains(subj) || subj.contains(courseName))) ||
                    (courseCode.isNotEmpty() && title.contains(courseCode)) ||
                    (courseName.isNotEmpty() && title.contains(courseName))
                }
            }
        )
    }

    private fun setupNavigationListeners() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.tab_viewpager)

        // Attendance Card: Informative details dialog
        binding.cardAttendance.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val present = database.attendanceDao().getPresentCount()
                val absent = database.attendanceDao().getAbsentCount()
                val total = present + absent

                if (total == 0) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Attendance Summary")
                        .setMessage(
                            "No attendance logged yet.\n\n" +
                            "Tip: In the Calendar tab, double-tap dates to mark Present, triple-tap to mark Absent, or long press to clear."
                        )
                        .setPositiveButton("Open Calendar") { _, _ ->
                            (activity as? MainActivity)?.navigateToTab(3) ?: viewPager?.let { it.currentItem = 3 }
                        }
                        .setNegativeButton("Close", null)
                        .show()
                } else {
                    val pct = ((present.toDouble() / total) * 100).toInt()
                    val statusText = if (pct >= 75) "Good (Above 75%)" else "Warning (Below 75%)"

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Attendance Summary")
                        .setMessage(
                            "Overall Attendance: $pct%\n" +
                            "Status: $statusText\n\n" +
                            "• Total Days Logged: $total\n" +
                            "• Present: $present days 🟢\n" +
                            "• Absent: $absent days 🔴\n\n" +
                            "Tip: Double-tap dates in Calendar to mark Present, triple-tap to mark Absent, or long press to clear."
                        )
                        .setPositiveButton("View Calendar") { _, _ ->
                            (activity as? MainActivity)?.navigateToTab(3) ?: viewPager?.let { it.currentItem = 3 }
                        }
                        .setNegativeButton("Close", null)
                        .show()
                }
            }
        }

        // Assignments Card: Navigate to Assignments Tab (Index 4)
        binding.cardAssignments.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(4) ?: viewPager?.let { it.currentItem = 4 }
        }

        // Upcoming Events / Exams Card: Navigate to Calendar Tab (Index 3)
        binding.cardUpcomingExams.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(3) ?: viewPager?.let { it.currentItem = 3 }
        }

        // View All Courses -> Navigate to Course Tab (Index 1)
        binding.tvViewAllCourses.setOnClickListener {
            viewPager?.currentItem = 1
        }

        // Empty state Add Course button -> Navigate to Course Tab (Index 1)
        binding.btnAddCoursesEmpty.setOnClickListener {
            viewPager?.currentItem = 1
        }
    }

    private fun loadDashboardData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val pendingAssignments = database.assignmentDao().getPendingAssignmentsCount()
            val courses = database.courseDao().getAllCourses()
            val allAssignments = database.assignmentDao().getAllAssignments()
            val allNotes = database.notesDao().getAllNotes()
            val presentCount = database.attendanceDao().getPresentCount()
            val absentCount = database.attendanceDao().getAbsentCount()
            val totalAttendance = presentCount + absentCount

            val b = _binding ?: return@launch

            assignmentList.clear()
            assignmentList.addAll(allAssignments)

            notesList.clear()
            notesList.addAll(allNotes)

            // Update pending assignments counter
            if (pendingAssignments > 0) {
                b.tvAssignmentsDue.text = pendingAssignments.toString()
            } else {
                b.tvAssignmentsDue.text = "--"
            }

            // Update upcoming events / exams countdown dynamically from calendar events
            val upcomingDays = getEarliestUpcomingDays(allAssignments)
            if (upcomingDays != null) {
                b.tvUpcomingExamDaysLeft.text = "${upcomingDays}d"
            } else {
                b.tvUpcomingExamDaysLeft.text = "--"
            }

            // Update attendance percentage
            if (totalAttendance > 0) {
                val attPercentage = ((presentCount.toDouble() / totalAttendance) * 100).toInt()
                b.tvAttendancePercentage.text = "$attPercentage%"
            } else {
                b.tvAttendancePercentage.text = "--"
            }

            // Update course list and handle dynamic empty state
            if (courses.isEmpty()) {
                b.layoutEmptyCourses.visibility = View.VISIBLE
                b.rvCourses.visibility = View.GONE
                courseList.clear()
                courseAdapter.notifyDataSetChanged()
            } else {
                b.layoutEmptyCourses.visibility = View.GONE
                b.rvCourses.visibility = View.VISIBLE
                courseList.clear()
                courseList.addAll(courses)
                courseAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getEarliestUpcomingDays(assignments: List<Assignment>): Long? {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dateFormats = listOf(
            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()),
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()),
            SimpleDateFormat("d MMM yyyy", Locale.getDefault()),
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()),
            SimpleDateFormat("d/M/yyyy", Locale.getDefault()),
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )

        var minDays: Long? = null

        for (assignment in assignments) {
            if (assignment.status.equals("Completed", ignoreCase = true)) continue

            val dateStr = assignment.dueDate.trim()
            if (dateStr.isEmpty()) continue

            var parsedDate: Date? = null
            for (format in dateFormats) {
                try {
                    parsedDate = format.parse(dateStr)
                    if (parsedDate != null) break
                } catch (_: Exception) {
                }
            }

            if (parsedDate != null) {
                val eventCal = Calendar.getInstance().apply {
                    time = parsedDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val diffMillis = eventCal.timeInMillis - today.timeInMillis
                val diffDays = diffMillis / (1000 * 60 * 60 * 24)

                if (diffDays >= 0) {
                    if (minDays == null || diffDays < minDays) {
                        minDays = diffDays
                    }
                }
            }
        }
        return minDays
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}