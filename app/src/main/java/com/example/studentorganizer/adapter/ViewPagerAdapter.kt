package com.example.studentorganizer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.studentorganizer.fragments.AssignmentFragment
import com.example.studentorganizer.fragments.CalendarFragment
import com.example.studentorganizer.fragments.CourseFragment
import com.example.studentorganizer.fragments.HomeFragment
import com.example.studentorganizer.fragments.NotesFragment
import com.example.studentorganizer.fragments.ProfileFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> CourseFragment()
            2 -> NotesFragment()
            3 -> CalendarFragment()
            4 -> AssignmentFragment()
            else -> HomeFragment()
        }
    }
}
