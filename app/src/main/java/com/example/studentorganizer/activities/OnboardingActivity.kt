package com.example.studentorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.studentorganizer.R
import com.example.studentorganizer.adapter.OnboardingAdapter
import com.example.studentorganizer.model.OnboardingItem

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button

    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)

        btnNext = findViewById(R.id.btnNext)
        btnGetStarted = findViewById(R.id.btnGetStarted)

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        val list = arrayListOf(
            OnboardingItem(
                R.drawable.img,
                "Welcome to Student Organizer",
                "Manage your classes, assignments, attendance, and academic tasks all in one place."
            ),
            OnboardingItem(
                R.drawable.img,
                "Stay Organized",
                "Keep track of your timetable, deadlines, and upcoming exams with ease."
            ),
            OnboardingItem(
                R.drawable.img,
                "Ready to Get Started?",
                "Create your account and take control of your academic journey today."
            )
        )

        viewPager.adapter = OnboardingAdapter(list)

        btnNext.setOnClickListener {
            if (viewPager.currentItem < list.size - 1) {
                viewPager.currentItem += 1
            }
        }

        btnGetStarted.setOnClickListener {
            val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)
            sharedPref.edit().putBoolean("isFirstTime", false).apply()

            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateDots(position)

                    if (position == list.size - 1) {
                        btnNext.visibility = View.GONE
                        btnGetStarted.visibility = View.VISIBLE
                    } else {
                        btnNext.visibility = View.VISIBLE
                        btnGetStarted.visibility = View.GONE
                    }
                }
            }
        )
    }

    private fun updateDots(position: Int) {
        dot3.setTextColor(ContextCompat.getColor(this, R.color.gray_medium))
        dot1.setTextColor(ContextCompat.getColor(this, R.color.gray_medium))
        dot2.setTextColor(ContextCompat.getColor(this, R.color.gray_medium))

        when (position) {
            0 -> dot1.setTextColor(ContextCompat.getColor(this, R.color.black))
            1 -> dot2.setTextColor(ContextCompat.getColor(this, R.color.black))
            2 -> dot3.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }
}