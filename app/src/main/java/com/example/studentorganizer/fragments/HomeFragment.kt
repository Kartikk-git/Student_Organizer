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

import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
            onMaterialsClick = { course ->
                val courseCode = course.code.trim().lowercase()
                val courseName = course.name.trim().lowercase()

                val matchedNotes = notesList.filter { note ->
                    val subj = note.subject.trim().lowercase()
                    val title = note.title.trim().lowercase()
                    subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj) ||
                            title.contains(courseCode) || title.contains(courseName) || courseName.contains(subj)
                }

                if (matchedNotes.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Opening notes for ${course.code}...", Toast.LENGTH_SHORT).show()
                    viewPager?.currentItem = 2
                } else {
                    Toast.makeText(requireContext(), "No notes found for ${course.code}. Add them in Notes!", Toast.LENGTH_SHORT).show()
                }
            },
            getAssignmentStats = { course ->
                val courseCode = course.code.trim().lowercase()
                val courseName = course.name.trim().lowercase()

                val matchedAssignments = assignmentList.filter { assignment ->
                    val subj = assignment.subject.trim().lowercase()
                    val title = assignment.title.trim().lowercase()
                    subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj) ||
                            title.contains(courseCode) || title.contains(courseName) || courseName.contains(subj)
                }

                val total = matchedAssignments.size
                val completed = matchedAssignments.count { it.status.equals("Completed", ignoreCase = true) }
                Pair(completed, total)
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
                val pct = if (total > 0) ((present.toDouble() / total) * 100).toInt() else 100

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
                        viewPager?.currentItem = 3
                    }
                    .setNegativeButton("Close", null)
                    .show()
            }
        }

        // Assignments Card: Navigate to Assignments Tab (Index 4)
        binding.cardAssignments.setOnClickListener {
            viewPager?.currentItem = 4
        }

        // Upcoming Exams Card: Navigate to Calendar / Schedule Tab (Index 3)
        binding.cardUpcomingExams.setOnClickListener {
            viewPager?.currentItem = 3
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
            b.tvAssignmentsDue.text = pendingAssignments.toString()

            // Update attendance percentage
            if (totalAttendance > 0) {
                val attPercentage = ((presentCount.toDouble() / totalAttendance) * 100).toInt()
                b.tvAttendancePercentage.text = "$attPercentage%"
            } else {
                b.tvAttendancePercentage.text = "100%"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}