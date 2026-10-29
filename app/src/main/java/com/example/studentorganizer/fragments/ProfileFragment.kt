package com.example.studentorganizer.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.studentorganizer.R
import com.example.studentorganizer.activities.LoginActivity
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        setupProfileData()
        setupActionListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupProfileData()
    }

    private fun setupProfileData() {
        val act = activity ?: return
        val sharedPref = act.getSharedPreferences("Details", MODE_PRIVATE)
        val name = sharedPref.getString("name", "Student")
        val email = sharedPref.getString("email", "student@example.com")

        val b = _binding ?: return
        val roll = sharedPref.getString("roll", null)
        val semester = sharedPref.getString("semester", null)
        val cgpa = sharedPref.getString("cgpa", null)

        b.tvName.text = if (!name.isNullOrBlank()) name else "Student"
        b.tvEmail.text = if (!email.isNullOrBlank()) email else "student@example.com"
        b.tvRoll.text = if (!roll.isNullOrBlank()) "Roll No: $roll" else "Roll No: --"
        b.tvSemester.text = if (!semester.isNullOrBlank()) semester else "--"
        b.tvCgpa.text = if (!cgpa.isNullOrBlank()) cgpa else "--"

        // Update Attendance calculation from database
        viewLifecycleOwner.lifecycleScope.launch {
            val presentCount = database.attendanceDao().getPresentCount()
            val absentCount = database.attendanceDao().getAbsentCount()
            val totalAttendance = presentCount + absentCount

            if (totalAttendance > 0) {
                val percentage = ((presentCount.toDouble() / totalAttendance) * 100).toInt()
                _binding?.tvAttendance?.text = "$percentage%"
            } else {
                _binding?.tvAttendance?.text = "--"
            }
        }
    }

    private fun setupActionListeners() {
        // Attendance Report Dialog
        binding.cardAttendanceReport.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            viewLifecycleOwner.lifecycleScope.launch {
                val present = database.attendanceDao().getPresentCount()
                val absent = database.attendanceDao().getAbsentCount()
                val total = present + absent
                val pct = if (total > 0) ((present.toDouble() / total) * 100).toInt() else 100

                val statusMessage = when {
                    total == 0 -> "No attendance records logged yet. Double-tap dates in the Calendar tab to start tracking!"
                    pct >= 75 -> {
                        val buffer = (present - (0.75 * total)) / 0.75
                        "✅ Great Standing: Your attendance is $pct%, which meets the 75% academic requirement." +
                                if (buffer.toInt() > 0) "\n(You can safely miss up to ${buffer.toInt()} more class(es) while staying at or above 75%)." else ""
                    }
                    else -> {
                        val needed = ((0.75 * total - present) / 0.25).toInt() + 1
                        "⚠️ Attendance Warning: Your attendance is $pct%, which is below the 75% minimum requirement.\n(You need to attend the next $needed consecutive class(es) to reach 75%)."
                    }
                }

                MaterialAlertDialogBuilder(ctx)
                    .setTitle("📊 Attendance Report")
                    .setMessage(
                        "Overall Attendance: $pct%\n\n" +
                        "• Total Logged Days: $total\n" +
                        "• Days Present: $present 🟢\n" +
                        "• Days Absent: $absent 🔴\n\n" +
                        "$statusMessage\n\n" +
                        "How to Log: Go to Calendar, double-tap any date for Present, triple-tap for Absent, or long press to clear."
                    )
                    .setPositiveButton("Open Calendar") { _, _ ->
                        val act = activity
                        if (act is com.example.studentorganizer.activities.ProfileActivity) {
                            val intent = Intent(act, com.example.studentorganizer.activities.MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                putExtra("NAVIGATE_TO_TAB", 3)
                            }
                            startActivity(intent)
                            act.finish()
                        } else {
                            act?.findViewById<ViewPager2>(R.id.tab_viewpager)?.currentItem = 3
                        }
                    }
                    .setNegativeButton("Close", null)
                    .show()
            }
        }

        binding.layoutNotification.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Notifications")
                .setMessage("All notification channels (Assignment reminders, Exam alerts, Class updates) are currently active.")
                .setPositiveButton("OK", null)
                .show()
        }

        binding.layoutSettings.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            MaterialAlertDialogBuilder(ctx)
                .setTitle("App Settings")
                .setMessage("Student Organizer v1.0\nTheme: Clean Modern Light\nOffline Mode: Active (Room Database)\nStatus: Up to date")
                .setPositiveButton("OK", null)
                .show()
        }

        binding.layoutLogout.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout") { _, _ ->
                    val act = activity ?: return@setPositiveButton
                    val sharedPref = act.getSharedPreferences("Details", MODE_PRIVATE)
                    sharedPref.edit()
                        .putBoolean("isLoggedIn", false)
                        .apply()

                    Toast.makeText(act, "Logged out successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(act, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    act.finish()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
