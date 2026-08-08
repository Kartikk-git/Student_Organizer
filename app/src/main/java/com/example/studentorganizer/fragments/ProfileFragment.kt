package com.example.studentorganizer.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var db: AppDatabase
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = AppDatabase.getDatabase(requireContext())
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        
        val sharedPref = requireActivity().getSharedPreferences("Details", MODE_PRIVATE)
        val email = sharedPref.getString("loggedInEmail", null)
        
        lifecycleScope.launch {
            if (email != null) {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    binding.tvName.text = user.name
                    binding.tvEmail.text = email
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}