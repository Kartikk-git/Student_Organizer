package com.example.studentorganizer.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.R
import com.example.studentorganizer.activities.SplashActivity
import com.example.studentorganizer.adapter.CourseAdapter
import com.example.studentorganizer.databinding.FragmentHomeBinding
import com.example.studentorganizer.model.Course

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var courseList: ArrayList<Course>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.fabReset.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("Details", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        courseList = arrayListOf()

        courseList.add(Course("CS301", "Data Structures & Algorithms", 68))
        courseList.add(Course("CS302", "Database Management System", 82))
        courseList.add(Course("CS303", "Operating System", 55))
        courseList.add(Course("CS304","Digital Electronics",40))
        courseList.add(Course("CS305","Advanced Mathematics",60))
        courseList.add(Course("CS306","Programming in Java",85))
        courseAdapter = CourseAdapter(courseList)

        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}