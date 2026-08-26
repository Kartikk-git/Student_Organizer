package com.example.studentorganizer.fragments

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studentorganizer.activities.LoginActivity
import com.example.studentorganizer.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

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
        b.tvName.text = if (!name.isNullOrBlank()) name else "Student"
        b.tvEmail.text = if (!email.isNullOrBlank()) email else "student@example.com"
        b.tvRoll.text = "Roll No: SO-2026"
    }

    private fun setupActionListeners() {
        binding.layoutNotification.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            AlertDialog.Builder(ctx)
                .setTitle("Notifications")
                .setMessage("All notification channels (Assignment reminders, Exam alerts, Class updates) are currently active.")
                .setPositiveButton("OK", null)
                .show()
        }

        binding.layoutSettings.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            AlertDialog.Builder(ctx)
                .setTitle("App Settings")
                .setMessage("Student Organizer v1.0\nTheme: Clean Modern Light\nOffline Mode: Active (Room Database)\nStatus: Up to date")
                .setPositiveButton("OK", null)
                .show()
        }

        binding.layoutLogout.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            AlertDialog.Builder(ctx)
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
