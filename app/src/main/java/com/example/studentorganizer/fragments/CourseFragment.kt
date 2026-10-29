package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.studentorganizer.R
import com.example.studentorganizer.adapter.CourseAdapter
import com.example.studentorganizer.compose.course.CourseBottomSheetDialogFragment
import com.example.studentorganizer.compose.course.CourseDetailScreen
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.databinding.FragmentCourseBinding
import com.example.studentorganizer.ui.theme.StudentOrganizerTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CourseFragment : Fragment() {
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()
    private val assignmentList = mutableListOf<Assignment>()
    private val notesList = mutableListOf<Notes>()
    private lateinit var database: AppDatabase
    private var currentDetailCourse: Course? = null
    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        binding.composeViewDetail.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        setupAdapter()

        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter

        loadCourses()

        binding.fabAddCourse.setOnClickListener {
            showAddCourseBottomSheet()
        }

        setupBackNavigation()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadCourses()
    }

    private fun setupBackNavigation() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val b = _binding
                if (b != null && b.composeViewDetail.visibility == View.VISIBLE) {
                    hideCourseDetails()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback!!)
    }

    private fun setupAdapter() {
        courseAdapter = CourseAdapter(
            courseList = courseList,
            onItemClick = {
                // Card click intentionally does not open details
            },
            onMoreOptionsClick = { course, anchorView ->
                showCourseOptionsMenu(course, anchorView)
            },
            onItemLongClick = { course ->
                showDeleteCourseDialog(course)
            },
            getAssignmentStats = { course ->
                calculateAssignmentStats(course)
            },
            getNotesCount = { course ->
                calculateNotesCount(course)
            }
        )
    }

    private fun calculateNotesCount(course: Course): Int {
        val courseCode = course.code.trim().lowercase()
        val courseName = course.name.trim().lowercase()

        return notesList.count { note ->
            val subj = note.subject.trim().lowercase()
            val title = note.title.trim().lowercase()

            (courseCode.isNotEmpty() && subj.isNotEmpty() && (subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj))) ||
            (courseName.isNotEmpty() && subj.isNotEmpty() && (courseName == subj || courseName.contains(subj) || subj.contains(courseName))) ||
            (courseCode.isNotEmpty() && title.contains(courseCode)) ||
            (courseName.isNotEmpty() && title.contains(courseName))
        }
    }

    private fun calculateAssignmentStats(course: Course): Pair<Int, Int> {
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
        return Pair(completed, total)
    }

    private fun showCourseOptionsMenu(course: Course, anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add(0, 1, 0, "View Details")
        popup.menu.add(0, 2, 1, "Update Course")
        popup.menu.add(0, 3, 2, "Delete Course")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showCourseDetails(course)
                    true
                }
                2 -> {
                    showEditCourseBottomSheet(course)
                    true
                }
                3 -> {
                    showDeleteCourseDialog(course)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showCourseDetails(course: Course) {
        currentDetailCourse = course
        val b = _binding ?: return
        b.composeViewDetail.visibility = View.VISIBLE

        renderCourseDetailScreen(course)
    }

    private fun hideCourseDetails() {
        currentDetailCourse = null
        val b = _binding ?: return
        b.composeViewDetail.visibility = View.GONE
        loadCourses()
    }

    private fun renderCourseDetailScreen(course: Course) {
        val b = _binding ?: return
        val courseCode = course.code.trim().lowercase()
        val courseName = course.name.trim().lowercase()

        val matchedAssignments = assignmentList.filter { assignment ->
            val subj = assignment.subject.trim().lowercase()
            val title = assignment.title.trim().lowercase()
            subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj) ||
                    title.contains(courseCode) || title.contains(courseName) || courseName.contains(subj)
        }

        val matchedNotes = notesList.filter { note ->
            val subj = note.subject.trim().lowercase()
            val title = note.title.trim().lowercase()
            subj == courseCode || subj.contains(courseCode) || courseCode.contains(subj) ||
                    title.contains(courseCode) || title.contains(courseName) || courseName.contains(subj)
        }

        b.composeViewDetail.setContent {
            StudentOrganizerTheme {
                CourseDetailScreen(
                    course = course,
                    assignments = matchedAssignments,
                    notes = matchedNotes,
                    onBackClick = {
                        hideCourseDetails()
                    },
                    onEditCourse = { c ->
                        showEditCourseBottomSheet(c)
                    },
                    onDeleteCourse = { c ->
                        showDeleteCourseDialog(c) {
                            hideCourseDetails()
                        }
                    },
                    onToggleAssignmentStatus = { assignment ->
                        val newStatus = if (assignment.status.equals("Completed", ignoreCase = true)) "Pending" else "Completed"
                        val updated = assignment.copy(status = newStatus)
                        viewLifecycleOwner.lifecycleScope.launch {
                            database.assignmentDao().updateAssignment(updated)
                            val allAssignments = database.assignmentDao().getAllAssignments()
                            assignmentList.clear()
                            assignmentList.addAll(allAssignments)
                            currentDetailCourse?.let { current ->
                                renderCourseDetailScreen(current)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun loadCourses() {
        viewLifecycleOwner.lifecycleScope.launch {
            val courses = database.courseDao().getAllCourses()
            val assignments = database.assignmentDao().getAllAssignments()
            val notes = database.notesDao().getAllNotes()

            val b = _binding ?: return@launch

            assignmentList.clear()
            assignmentList.addAll(assignments)

            notesList.clear()
            notesList.addAll(notes)

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

            // If detail screen is open, re-render it
            currentDetailCourse?.let { current ->
                val updatedCourse = courses.firstOrNull { it.id == current.id }
                if (updatedCourse != null) {
                    currentDetailCourse = updatedCourse
                    renderCourseDetailScreen(updatedCourse)
                } else {
                    hideCourseDetails()
                }
            }
        }
    }

    private fun showAddCourseBottomSheet() {
        val bottomSheet = CourseBottomSheetDialogFragment.newInstance(null) { name, code, progress ->
            val newCourse = Course(
                name = name,
                code = code,
                progress = progress
            )
            viewLifecycleOwner.lifecycleScope.launch {
                database.courseDao().insertCourse(newCourse)
                if (_binding != null) {
                    loadCourses()
                }
                context?.let {
                    Toast.makeText(it, "Course '$name' added successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show(parentFragmentManager, "CourseBottomSheet")
    }

    private fun showEditCourseBottomSheet(course: Course) {
        val bottomSheet = CourseBottomSheetDialogFragment.newInstance(course) { name, code, progress ->
            val updatedCourse = Course(
                id = course.id,
                name = name,
                code = code,
                progress = progress
            )
            viewLifecycleOwner.lifecycleScope.launch {
                database.courseDao().updateCourse(updatedCourse)
                if (_binding != null) {
                    loadCourses()
                }
                context?.let {
                    Toast.makeText(it, "Course '$name' updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show(parentFragmentManager, "CourseBottomSheet")
    }

    private fun showDeleteCourseDialog(course: Course, onDeleted: (() -> Unit)? = null) {
        val ctx = context ?: return
        MaterialAlertDialogBuilder(ctx)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete '${course.name}' (${course.code})?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.courseDao().deleteCourse(course)
                    if (_binding != null) {
                        loadCourses()
                    }
                    onDeleted?.invoke()
                    Toast.makeText(ctx, "Deleted '${course.name}'", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback?.remove()
        backPressedCallback = null
        _binding = null
    }
}