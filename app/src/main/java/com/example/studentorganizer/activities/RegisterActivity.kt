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
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etPasswordConfirm.text.toString()
            val checked = binding.cbAcceptTerms.isChecked

            if(name.isEmpty()){
                binding.etName.error = "Enter your name"
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmail.error = "Enter valid email"
            }else if(password.isEmpty() || password.length < 8){
                binding.etPassword.error = "Password must be at least 8 characters"
            }else if(password != confirmPassword){
                binding.etPasswordConfirm.error = "Password does not match"
            }else if(!checked){
                binding.cbAcceptTerms.error = "Please accept terms & conditions"
            }else{
                val user = User(
                    name = name,
                    email = email,
                    password = password
                )
                val db = AppDatabase.getDatabase(this)

                lifecycleScope.launch {

                    db.userDao().insertUser(user)

                }
                sharedPref.edit()
                    .putBoolean("isRegistered", true)
                    .putString("loggedInEmail", email)
                    .putBoolean("isFirstTime", false)
                    .apply()

                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
