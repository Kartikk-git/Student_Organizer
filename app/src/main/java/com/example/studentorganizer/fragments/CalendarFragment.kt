package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.studentorganizer.compose.calendar.CalendarScreen
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.ui.theme.StudentOrganizerTheme

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val database = AppDatabase.getDatabase(requireContext().applicationContext)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                StudentOrganizerTheme {
                    CalendarScreen(
                        database = database
                    )
                }
            }
        }
    }
}