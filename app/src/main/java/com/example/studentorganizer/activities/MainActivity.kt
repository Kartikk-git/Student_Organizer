package com.example.studentorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import com.example.studentorganizer.R
import com.example.studentorganizer.adapter.ViewPagerAdapter
import com.example.studentorganizer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isProgrammaticNavigation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupViewPager(binding.tabViewpager)
        updateHeaderName()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val targetPage = when (item.itemId) {
                R.id.navigation_home -> 0
                R.id.navigation_course -> 1
                R.id.navigation_notes -> 2
                R.id.navigation_calendar -> 3
                R.id.navigation_assignment -> 4
                else -> -1
            }

            if (targetPage != -1 && binding.tabViewpager.currentItem != targetPage) {
                isProgrammaticNavigation = true
                binding.tabViewpager.setCurrentItem(targetPage, false)
                isProgrammaticNavigation = false
            }
            true
        }

        binding.tabViewpager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (isProgrammaticNavigation) return

                    val navMenu = binding.bottomNavigation.menu
                    if (position < navMenu.size) {
                        val itemId = navMenu.getItem(position).itemId
                        if (binding.bottomNavigation.selectedItemId != itemId) {
                            binding.bottomNavigation.selectedItemId = itemId
                        }
                    } else {
                        uncheckAllBottomNavItems()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        updateHeaderName()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tab = intent.getIntExtra("NAVIGATE_TO_TAB", -1)
        if (tab != -1) {
            navigateToTab(tab)
        }
    }

    private fun updateHeaderName() {
        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)
        val user = sharedPref.getString("name", "Student")
        binding.tvName.text = if (!user.isNullOrBlank()) user else "Student"

        val semester = sharedPref.getString("semester", null)
        val year = sharedPref.getString("academicYear", null)

        binding.tvSemesterNumber.text = if (!semester.isNullOrBlank()) semester else "--"
        binding.tvSemesterYear.text = if (!year.isNullOrBlank()) "• $year" else "• ----"
    }

    fun navigateToTab(targetPage: Int) {
        val navMenu = binding.bottomNavigation.menu
        if (targetPage in 0 until navMenu.size) {
            val itemId = navMenu.getItem(targetPage).itemId
            binding.bottomNavigation.selectedItemId = itemId
        } else {
            binding.tabViewpager.currentItem = targetPage
        }
    }

    private fun uncheckAllBottomNavItems() {
        val menu = binding.bottomNavigation.menu
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }

    private fun setupViewPager(viewpager: ViewPager2) {
        val adapter = ViewPagerAdapter(this)
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 4
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(this, "You're all caught up on notifications!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
