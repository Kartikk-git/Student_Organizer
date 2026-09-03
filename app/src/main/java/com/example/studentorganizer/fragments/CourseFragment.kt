package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.CourseAdapter
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.databinding.DialogAddCourseBinding
import com.example.studentorganizer.databinding.FragmentCourseBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        courseAdapter = CourseAdapter(
            courseList,
            onItemClick = { course ->
                showEditCourseDialog(course)
            },
            onItemLongClick = { course ->
                showDeleteCourseDialog(course)
            }
        )
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter

        loadCourses()
        binding.fabAddCourse.setOnClickListener {
            showAddCourseDialog()
        }

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

    private fun showAddCourseDialog() {
        val ctx = context ?: return
        val dialogBinding = DialogAddCourseBinding.inflate(LayoutInflater.from(ctx))

        // Set initial progress text
        dialogBinding.tvProgressValue.text = "${dialogBinding.seekBarProgress.progress}%"

        // Set live progress update listener
        dialogBinding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dialogBinding.tvProgressValue.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val dialog = MaterialAlertDialogBuilder(ctx)
            .setTitle("Add Course")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = dialogBinding.etCourseName.text.toString().trim()
                val code = dialogBinding.etCourseCode.text.toString().trim()
                val progress = dialogBinding.seekBarProgress.progress

                if (title.isEmpty()) {
                    dialogBinding.tilCourseName.error = "Enter course title"
                    return@setOnClickListener
                } else {
                    dialogBinding.tilCourseName.error = null
                }

                if (code.isEmpty()) {
                    dialogBinding.tilCourseCode.error = "Enter subject code"
                    return@setOnClickListener
                } else {
                    dialogBinding.tilCourseCode.error = null
                }

                val newCourse = Course(
                    name = title,
                    code = code,
                    progress = progress
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    database.courseDao().insertCourse(newCourse)
                    if (_binding != null) {
                        loadCourses()
                    }
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditCourseDialog(course: Course) {
        val ctx = context ?: return
        val dialogBinding = DialogAddCourseBinding.inflate(LayoutInflater.from(ctx))

        dialogBinding.etCourseName.setText(course.name)
        dialogBinding.etCourseCode.setText(course.code)
        dialogBinding.seekBarProgress.progress = course.progress
        dialogBinding.tvProgressValue.text = "${course.progress}%"

        dialogBinding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dialogBinding.tvProgressValue.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val dialog = MaterialAlertDialogBuilder(ctx)
            .setTitle("Update Course")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = dialogBinding.etCourseName.text.toString().trim()
                val code = dialogBinding.etCourseCode.text.toString().trim()
                val progress = dialogBinding.seekBarProgress.progress

                if (title.isEmpty()) {
                    dialogBinding.tilCourseName.error = "Enter course title"
                    return@setOnClickListener
                } else {
                    dialogBinding.tilCourseName.error = null
                }

                if (code.isEmpty()) {
                    dialogBinding.tilCourseCode.error = "Enter subject code"
                    return@setOnClickListener
                } else {
                    dialogBinding.tilCourseCode.error = null
                }

                val updatedCourse = Course(
                    id = course.id,
                    name = title,
                    code = code,
                    progress = progress
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    database.courseDao().updateCourse(updatedCourse)
                    if (_binding != null) {
                        loadCourses()
                    }
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDeleteCourseDialog(course: Course) {
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
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}