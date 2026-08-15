package com.example.studentorganizer.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.viewpager.widget.ViewPager
import com.example.studentorganizer.R
import com.example.studentorganizer.adapter.ViewPagerAdapter
import com.example.studentorganizer.databinding.ActivityMainBinding
import com.example.studentorganizer.fragments.AssignmentFragment
import com.example.studentorganizer.fragments.CalendarFragment
import com.example.studentorganizer.fragments.CourseFragment
import com.example.studentorganizer.fragments.HomeFragment
import com.example.studentorganizer.fragments.NotesFragment
import com.example.studentorganizer.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        // Enable the Action Bar title so the Top Bar can show context
        supportActionBar?.setDisplayShowTitleEnabled(true)

        setupViewPager(binding.tabViewpager)

        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)
        val user = sharedPref.getString("name","User")
        binding.tvName.text = user

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.tabViewpager.currentItem = 0
                    true
                }
                R.id.navigation_course -> {
                    binding.tabViewpager.currentItem = 1
                    true
                }
                R.id.navigation_notes ->{
                    binding.tabViewpager.currentItem = 2
                    true
                }
                R.id.navigation_calendar ->{
                    binding.tabViewpager.currentItem = 3
                    true
                }
                R.id.navigation_assignment ->{
                    binding.tabViewpager.currentItem = 4
                    true
                }
                else -> false
            }
        }

        // Sync ViewPager with BottomNavigation and update Top Bar Title
        binding.tabViewpager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(pos: Int, off: Float, pixels: Int) {}

                override fun onPageSelected(position: Int) {
                    val adapter = binding.tabViewpager.adapter as? ViewPagerAdapter
                    
                    if (position < binding.bottomNavigation.menu.size) {
                        // Sync with Bottom Navigation for main tabs
                        binding.bottomNavigation.menu[position].isChecked = true
                    } else {
                        // Clear Bottom Navigation selection when on Profile (which is an Action, not a Tab)
                        uncheckAllBottomNavItems()
                    }
                    
                    // Set Top Bar Title to the current Fragment's name
                    adapter?.let {
                        supportActionBar?.title = it.getPageTitle(position)
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}
            }
        )
        
        // Set initial title
        supportActionBar?.title = "Home"
    }

    private fun uncheckAllBottomNavItems() {
        val menu = binding.bottomNavigation.menu
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size) {
            menu[i].isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }

    private fun setupViewPager(viewpager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(CourseFragment(), "Courses")
        adapter.addFragment(NotesFragment(), "Notes")
        adapter.addFragment(CalendarFragment(), "Calendar")
        adapter.addFragment(AssignmentFragment(), "Assignments")
        adapter.addFragment(ProfileFragment(), "Profile") // Added missing Profile fragment
        viewpager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_profile -> {
                // Navigate to Profile page in ViewPager
                binding.tabViewpager.currentItem = 5
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
