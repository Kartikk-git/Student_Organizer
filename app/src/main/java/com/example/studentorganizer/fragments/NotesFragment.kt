package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.NoteAdapter
import com.example.studentorganizer.databinding.FragmentNotesBinding
import com.example.studentorganizer.model.Notes

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotesBinding.inflate(
            inflater,
            container,
            false
        )

        val notes = listOf(
            Notes(
                "Normalization",
                "DBMS",
                "Important concepts of 1NF, 2NF and 3NF.",
                "8 August 2026"
            ),
            Notes(
                "Binary Trees",
                "Data Structures",
                "Binary tree traversal and important properties.",
                "7 August 2026"
            ),
            Notes(
                "OOP Concepts",
                "Java",
                "Encapsulation, inheritance, polymorphism and abstraction.",
                "5 August 2026"
            )
        )

        binding.rvNotes.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvNotes.adapter =
            NoteAdapter(notes)

        binding.fabAddNote.setOnClickListener {
            // Add note functionality
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}