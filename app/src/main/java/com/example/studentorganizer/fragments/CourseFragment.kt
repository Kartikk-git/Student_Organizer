package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.CourseAdapter
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.databinding.FragmentCourseBinding
import kotlinx.coroutines.launch

class CourseFragment : Fragment() {
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        courseAdapter = CourseAdapter(courseList)
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter

        loadCourses()

        return binding.root
    }

    private fun loadCourses() {
        viewLifecycleOwner.lifecycleScope.launch {
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
            courseList.clear()
            courseList.addAll(courses)
            courseAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}