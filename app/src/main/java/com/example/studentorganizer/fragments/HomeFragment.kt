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
import androidx.viewpager2.widget.ViewPager2
import com.example.studentorganizer.R
import com.example.studentorganizer.adapter.CourseAdapter
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        courseAdapter = CourseAdapter(courseList)
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter

        binding.tvViewAllCourses.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.tab_viewpager)
            viewPager?.currentItem = 1
        }

        binding.fabReset.setOnClickListener {
            showAskDoubtDialog()
        }

        loadDashboardData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val pendingAssignments = database.assignmentDao().getPendingAssignmentsCount()

            val count = database.courseDao().getCoursesCount()
            if (count == 0) {
                val defaultCourses = listOf(
                    Course(code = "CS301", name = "Data Structures & Algorithms", progress = 68),
                    Course(code = "CS302", name = "Database Management System", progress = 82),
                    Course(code = "CS303", name = "Operating System", progress = 55),
                    Course(code = "CS304", name = "Digital Electronics", progress = 40),
                    Course(code = "CS305", name = "Advanced Mathematics", progress = 60),
                    Course(code = "CS306", name = "Programming in Java", progress = 85)
                )
                database.courseDao().insertAll(defaultCourses)
            }

            val courses = database.courseDao().getAllCourses()

            val b = _binding ?: return@launch
            b.tvAssignmentsDue.text = pendingAssignments.toString()
            courseList.clear()
            courseList.addAll(courses)
            courseAdapter.notifyDataSetChanged()
        }
    }

    private fun showAskDoubtDialog() {
        val ctx = context ?: return
        val doubtInput = EditText(ctx).apply {
            hint = "Ask a question or topic doubt..."
            minLines = 3
            gravity = Gravity.TOP
        }

        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
            addView(doubtInput)
        }

        AlertDialog.Builder(ctx)
            .setTitle("Ask a Doubt")
            .setMessage("Submit your academic questions to your course mentors.")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val query = doubtInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    Toast.makeText(ctx, "Doubt submitted successfully! A mentor will review it.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(ctx, "Please enter your question", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}