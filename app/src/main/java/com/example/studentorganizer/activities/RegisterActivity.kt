package com.example.studentorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studentorganizer.R
import com.example.studentorganizer.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
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
            }else if(password.isEmpty() || confirmPassword.isEmpty() || password.length < 8 ){
                binding.etPassword.error = "Enter valid password"
                binding.etPasswordConfirm.error = "Enter valid password"
            }else if(password != confirmPassword){
                binding.etPasswordConfirm.error = "Password does not match"
            }else if(!checked){
                binding.cbAcceptTerms.error = "Accept please"
            }else{
                sharedPref.edit()
                    .putString("name", name)
                    .putString("email", email)
                    .putString("password", password)
                    .putBoolean("isRegistered", true)
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