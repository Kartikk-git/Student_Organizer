package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.AssignmentAdapter
import com.example.studentorganizer.databinding.FragmentAssignmentBinding
import com.example.studentorganizer.model.Assignment

class AssignmentFragment : Fragment() {

    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAssignmentBinding.inflate(
            inflater,
            container,
            false
        )

        val assignments = listOf(
            Assignment(
                "Database Management Assignment",
                "DBMS",
                "15 August 2026",
                "Pending"
            ),
            Assignment(
                "Data Structures Lab",
                "DS",
                "18 August 2026",
                "Pending"
            ),
            Assignment(
                "Java Programming Task",
                "Java",
                "20 August 2026",
                "Completed"
            )
        )

        binding.rvAssignments.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvAssignments.adapter =
            AssignmentAdapter(assignments)

        binding.fabAddAssignment.setOnClickListener {
            // Add assignment functionality
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}