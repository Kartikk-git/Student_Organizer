package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studentorganizer.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            binding.tvSelectedDate.text = "Schedule for $selectedDate"
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}