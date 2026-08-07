package com.example.studentorganizer.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.example.studentorganizer.R
import com.example.studentorganizer.databinding.ActivityMainBinding
import com.example.studentorganizer.fragments.CourseFragment
import com.example.studentorganizer.fragments.HomeFragment
import com.example.studentorganizer.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("Details", MODE_PRIVATE)
        val name = sharedPref.getString("name","")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupViewPager(binding.tabViewpager)

        binding.tvName.setText(name)

        // Using the non-deprecated listener
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

        // Sync ViewPager with BottomNavigation
        binding.tabViewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu[position].isChecked = true
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setupViewPager(viewpager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(CourseFragment(), "Course")
        adapter.addFragment(ProfileFragment(), "Profile")
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
                binding.tabViewpager.currentItem = 2
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Pager Adapter for fragments
    class ViewPagerAdapter(supportFragmentManager: FragmentManager) :
        FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentList = ArrayList<Fragment>()
        private val fragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment = fragmentList[position]
        override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]
        override fun getCount(): Int = fragmentList.size

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }
}