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
import com.example.studentorganizer.adapter.AssignmentAdapter
import com.example.studentorganizer.compose.assignment.AssignmentBottomSheetDialogFragment
import com.example.studentorganizer.compose.assignment.AssignmentDetailScreen
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.databinding.FragmentAssignmentBinding
import com.example.studentorganizer.ui.theme.StudentOrganizerTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AssignmentFragment : Fragment() {

    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var assignmentAdapter: AssignmentAdapter
    private val assignmentList = mutableListOf<Assignment>()
    private val courseList = mutableListOf<Course>()

    private var currentDetailAssignment: Assignment? = null
    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssignmentBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        binding.composeViewDetail.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        setupRecyclerView()
        setupBackNavigation()

        binding.fabAddAssignment.setOnClickListener {
            showAddAssignmentBottomSheet()
        }

        loadAssignments()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadAssignments()
    }

    private fun setupBackNavigation() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val b = _binding
                if (b != null && b.composeViewDetail.visibility == View.VISIBLE) {
                    hideAssignmentDetails()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback!!)
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter(
            assignments = assignmentList,
            onItemClick = null, // Tapping card does not open details; only 3-dots "View Details" does
            onMoreOptionsClick = { assignment, anchorView ->
                showAssignmentOptionsMenu(assignment, anchorView)
            },
            onStatusToggle = { assignment ->
                val newStatus = if (assignment.status.equals("Completed", ignoreCase = true)) "Pending" else "Completed"
                val updated = assignment.copy(status = newStatus)
                viewLifecycleOwner.lifecycleScope.launch {
                    database.assignmentDao().updateAssignment(updated)
                    loadAssignments()
                }
            },
            onItemLongClick = { assignment ->
                showDeleteAssignmentDialog(assignment)
            }
        )

        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignments.adapter = assignmentAdapter
    }

    private fun loadAssignments() {
        viewLifecycleOwner.lifecycleScope.launch {
            val list = database.assignmentDao().getAllAssignments()
            val courses = database.courseDao().getAllCourses()

            val b = _binding ?: return@launch
            assignmentList.clear()
            assignmentList.addAll(list)

            courseList.clear()
            courseList.addAll(courses)

            if (list.isEmpty()) {
                b.layoutEmptyAssignments.visibility = View.VISIBLE
                b.rvAssignments.visibility = View.GONE
            } else {
                b.layoutEmptyAssignments.visibility = View.GONE
                b.rvAssignments.visibility = View.VISIBLE
            }
            assignmentAdapter.notifyDataSetChanged()

            // If detail screen is open, re-render it
            currentDetailAssignment?.let { current ->
                val updated = list.firstOrNull { it.id == current.id }
                if (updated != null) {
                    currentDetailAssignment = updated
                    renderAssignmentDetailScreen(updated)
                } else {
                    hideAssignmentDetails()
                }
            }
        }
    }

    private fun showAssignmentOptionsMenu(assignment: Assignment, anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add(0, 1, 0, "View Details")
        popup.menu.add(0, 2, 1, "Update Assignment")
        popup.menu.add(0, 3, 2, "Delete Assignment")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showAssignmentDetails(assignment)
                    true
                }
                2 -> {
                    showEditAssignmentBottomSheet(assignment)
                    true
                }
                3 -> {
                    showDeleteAssignmentDialog(assignment)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showAssignmentDetails(assignment: Assignment) {
        currentDetailAssignment = assignment
        val b = _binding ?: return
        b.composeViewDetail.visibility = View.VISIBLE
        renderAssignmentDetailScreen(assignment)
    }

    private fun hideAssignmentDetails() {
        currentDetailAssignment = null
        val b = _binding ?: return
        b.composeViewDetail.visibility = View.GONE
        loadAssignments()
    }

    private fun renderAssignmentDetailScreen(assignment: Assignment) {
        val b = _binding ?: return
        b.composeViewDetail.setContent {
            StudentOrganizerTheme {
                AssignmentDetailScreen(
                    assignment = assignment,
                    onBackClick = {
                        hideAssignmentDetails()
                    },
                    onEditAssignment = { a ->
                        showEditAssignmentBottomSheet(a)
                    },
                    onDeleteAssignment = { a ->
                        showDeleteAssignmentDialog(a) {
                            hideAssignmentDetails()
                        }
                    },
                    onToggleStatus = { a ->
                        val newStatus = if (a.status.equals("Completed", ignoreCase = true)) "Pending" else "Completed"
                        val updated = a.copy(status = newStatus)
                        viewLifecycleOwner.lifecycleScope.launch {
                            database.assignmentDao().updateAssignment(updated)
                            currentDetailAssignment = updated
                            loadAssignments()
                        }
                    }
                )
            }
        }
    }

    private fun showAddAssignmentBottomSheet() {
        val bottomSheet = AssignmentBottomSheetDialogFragment.newInstance(
            assignment = null,
            courses = courseList
        ) { title, subject, dueDate, status ->
            val newAssignment = Assignment(
                title = title,
                subject = subject,
                dueDate = dueDate,
                status = status
            )
            viewLifecycleOwner.lifecycleScope.launch {
                database.assignmentDao().insertAssignment(newAssignment)
                if (_binding != null) {
                    loadAssignments()
                }
                context?.let {
                    Toast.makeText(it, "Assignment '$title' added!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show(parentFragmentManager, "AssignmentBottomSheet")
    }

    private fun showEditAssignmentBottomSheet(assignment: Assignment) {
        val bottomSheet = AssignmentBottomSheetDialogFragment.newInstance(
            assignment = assignment,
            courses = courseList
        ) { title, subject, dueDate, status ->
            val updated = Assignment(
                id = assignment.id,
                title = title,
                subject = subject,
                dueDate = dueDate,
                status = status
            )
            viewLifecycleOwner.lifecycleScope.launch {
                database.assignmentDao().updateAssignment(updated)
                if (_binding != null) {
                    loadAssignments()
                }
                context?.let {
                    Toast.makeText(it, "Assignment updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show(parentFragmentManager, "AssignmentBottomSheet")
    }

    private fun showDeleteAssignmentDialog(assignment: Assignment, onDeleted: (() -> Unit)? = null) {
        val ctx = context ?: return
        MaterialAlertDialogBuilder(ctx)
            .setTitle("Delete Assignment")
            .setMessage("Are you sure you want to delete '${assignment.title}'?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.assignmentDao().deleteAssignment(assignment)
                    if (_binding != null) {
                        loadAssignments()
                    }
                    onDeleted?.invoke()
                    Toast.makeText(ctx, "Deleted '${assignment.title}'", Toast.LENGTH_SHORT).show()
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