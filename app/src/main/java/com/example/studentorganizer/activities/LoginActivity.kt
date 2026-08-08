package com.example.studentorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)

        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch {

                val user = db.userDao().loginUser(email, password)

                if (user != null) {

                    sharedPref.edit()
                        .putBoolean("isLoggedIn", true)
                        .apply()

                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                    )

                    finish()

                } else{
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}