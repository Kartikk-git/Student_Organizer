package com.example.studentorganizer.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.AssignmentAdapter
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.databinding.FragmentAssignmentBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssignmentFragment : Fragment() {

    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var assignmentAdapter: AssignmentAdapter
    private val assignmentList = mutableListOf<Assignment>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssignmentBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        setupRecyclerView()
        loadAssignments()

        binding.fabAddAssignment.setOnClickListener {
            showAddAssignmentDialog()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter(
            assignments = assignmentList,
            onStatusToggle = { assignment ->
                val newStatus = if (assignment.status.equals("Completed", ignoreCase = true)) "Pending" else "Completed"
                val updated = assignment.copy(status = newStatus)
                viewLifecycleOwner.lifecycleScope.launch {
                    database.assignmentDao().updateAssignment(updated)
                    if (_binding != null) {
                        loadAssignments()
                    }
                    context?.let {
                        Toast.makeText(it, "Marked as $newStatus", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDelete = { assignment ->
                val ctx = context ?: return@AssignmentAdapter
                AlertDialog.Builder(ctx)
                    .setTitle("Delete Assignment")
                    .setMessage("Are you sure you want to delete '${assignment.title}'?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            database.assignmentDao().deleteAssignment(assignment)
                            if (_binding != null) {
                                loadAssignments()
                            }
                        }
                    }
                    .show()
            }
        )

        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignments.adapter = assignmentAdapter
    }

    private fun loadAssignments() {
        viewLifecycleOwner.lifecycleScope.launch {
            val count = database.assignmentDao().getAssignmentsCount()
            if (count == 0) {
                val initialAssignments = listOf(
                    Assignment(
                        title = "Database Management Assignment",
                        subject = "DBMS",
                        dueDate = "15 August 2026",
                        status = "Pending"
                    ),
                    Assignment(
                        title = "Data Structures Lab",
                        subject = "DS",
                        dueDate = "18 August 2026",
                        status = "Pending"
                    ),
                    Assignment(
                        title = "Java Programming Task",
                        subject = "Java",
                        dueDate = "20 August 2026",
                        status = "Completed"
                    )
                )
                database.assignmentDao().insertAll(initialAssignments)
            }

            val list = database.assignmentDao().getAllAssignments()
            val b = _binding ?: return@launch
            assignmentList.clear()
            assignmentList.addAll(list)
            assignmentAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddAssignmentDialog() {
        val ctx = context ?: return
        val titleInput = EditText(ctx).apply {
            hint = "Assignment Title"
        }
        val subjectInput = EditText(ctx).apply {
            hint = "Subject (e.g. DBMS, CS301)"
        }
        val dueDateInput = EditText(ctx).apply {
            hint = "Due Date (e.g. 25 Aug 2026)"
            val defaultDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
            setText(defaultDate)
        }

        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
            addView(titleInput)
            addView(subjectInput)
            addView(dueDateInput)
        }

        val dialog = AlertDialog.Builder(ctx)
            .setTitle("Add Assignment")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                val subject = subjectInput.text.toString().trim()
                val dueDate = dueDateInput.text.toString().trim()

                if (title.isEmpty()) {
                    titleInput.error = "Enter title"
                    return@setOnClickListener
                }
                if (subject.isEmpty()) {
                    subjectInput.error = "Enter subject"
                    return@setOnClickListener
                }
                if (dueDate.isEmpty()) {
                    dueDateInput.error = "Enter due date"
                    return@setOnClickListener
                }

                val newAssignment = Assignment(
                    title = title,
                    subject = subject,
                    dueDate = dueDate,
                    status = "Pending"
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    database.assignmentDao().insertAssignment(newAssignment)
                    if (_binding != null) {
                        loadAssignments()
                    }
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}