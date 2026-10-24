package com.example.studentorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.User
import com.example.studentorganizer.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etPasswordConfirm.text.toString()
            val checked = binding.cbAcceptTerms.isChecked

            if (name.isEmpty()) {
                binding.etName.error = "Enter your name"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Enter valid email"
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 8) {
                binding.etPassword.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.etPasswordConfirm.error = "Password does not match"
                return@setOnClickListener
            }
            if (!checked) {
                binding.cbAcceptTerms.error = "Please accept terms & conditions"
                Toast.makeText(this, "Please accept terms & conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getDatabase(this)

            lifecycleScope.launch {
                try {
                    val existingUser = db.userDao().getUserByEmail(email)
                    if (existingUser != null) {
                        binding.etEmail.error = "Email is already registered"
                        Toast.makeText(this@RegisterActivity, "Email is already registered", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val user = User(
                        name = name,
                        email = email,
                        password = password
                    )

                    db.userDao().insertUser(user)

                    sharedPref.edit()
                        .putBoolean("isRegistered", true)
                        .putString("email", email)
                        .putString("name", name)
                        .putBoolean("isFirstTime", false)
                        .apply()

                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration error: ${e.localizedMessage ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
