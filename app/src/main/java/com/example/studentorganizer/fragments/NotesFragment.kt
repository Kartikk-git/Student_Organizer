package com.example.studentorganizer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.NoteAdapter
import com.example.studentorganizer.compose.notes.NoteBottomSheetDialogFragment
import com.example.studentorganizer.compose.notes.NoteDetailScreen
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.databinding.FragmentNotesBinding
import com.example.studentorganizer.ui.theme.StudentOrganizerTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var database: AppDatabase

    private val notesList = mutableListOf<Notes>()
    private val courseList = mutableListOf<Course>()

    private var currentDetailNote: Notes? = null
    private var backPressedCallback: OnBackPressedCallback? = null
    private var selectedSubject: String = "ALL"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext().applicationContext)

        binding.composeViewDetail.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        setupRecyclerView()
        setupBackNavigation()

        binding.fabAddNote.setOnClickListener {
            showAddNoteBottomSheet()
        }

        loadData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupBackNavigation() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val b = _binding
                if (b != null && b.composeViewDetail.visibility == View.VISIBLE) {
                    hideNoteDetails()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback!!)
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onItemClick = null, // Tapping card does not open details; only 3-dots menu does
            onMoreOptionsClick = { note, anchorView ->
                showNoteOptionsMenu(note, anchorView)
            },
            onItemLongClick = { note ->
                showDeleteNoteDialog(note)
            }
        )

        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = noteAdapter
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val notes = database.notesDao().getAllNotes()
            val courses = database.courseDao().getAllCourses()

            val b = _binding ?: return@launch

            notesList.clear()
            notesList.addAll(notes)

            courseList.clear()
            courseList.addAll(courses)

            if (notes.isEmpty()) {
                b.layoutEmptyNotes.visibility = View.VISIBLE
                b.scrollSubjectChips.visibility = View.GONE
                b.rvNotes.visibility = View.GONE
            } else {
                b.layoutEmptyNotes.visibility = View.GONE
                b.scrollSubjectChips.visibility = View.VISIBLE
                b.rvNotes.visibility = View.VISIBLE
            }

            renderSubjectChips(notes)
            noteAdapter.setGroupedNotes(notesList, courseList, selectedSubject)

            // If detail screen is open, re-render it
            currentDetailNote?.let { current ->
                val updatedNote = notes.firstOrNull { it.id == current.id }
                if (updatedNote != null) {
                    currentDetailNote = updatedNote
                    renderNoteDetailScreen(updatedNote)
                } else {
                    hideNoteDetails()
                }
            }
        }
    }

    private fun renderSubjectChips(notes: List<Notes>) {
        val b = _binding ?: return
        val layout = b.layoutSubjectChips
        layout.removeAllViews()

        val subjects = notes.map { it.subject.trim() }.filter { it.isNotBlank() }.distinct()
        if (subjects.size <= 1) {
            b.scrollSubjectChips.visibility = View.GONE
            return
        }
        b.scrollSubjectChips.visibility = View.VISIBLE

        // Add "All" chip
        val allChip = createFilterChip("All (${notes.size})", selectedSubject.equals("ALL", ignoreCase = true)) {
            selectedSubject = "ALL"
            renderSubjectChips(notesList)
            noteAdapter.setGroupedNotes(notesList, courseList, selectedSubject)
        }
        layout.addView(allChip)

        subjects.forEach { subj ->
            val count = notes.count { it.subject.trim().equals(subj, ignoreCase = true) }
            val isSelected = selectedSubject.trim().equals(subj, ignoreCase = true)
            val chip = createFilterChip("$subj ($count)", isSelected) {
                selectedSubject = subj
                renderSubjectChips(notesList)
                noteAdapter.setGroupedNotes(notesList, courseList, selectedSubject)
            }
            layout.addView(chip)
        }
    }

    private fun createFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit): android.widget.TextView {
        val tv = android.widget.TextView(requireContext()).apply {
            this.text = text
            textSize = 12f
            setPadding(32, 16, 32, 16)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setBackgroundResource(com.example.studentorganizer.R.drawable.btn_round)
            if (isSelected) {
                backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK)
                setTextColor(android.graphics.Color.WHITE)
            } else {
                backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F4F4F5"))
                setTextColor(android.graphics.Color.parseColor("#333333"))
            }
            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }
            layoutParams = params
            setOnClickListener { onClick() }
        }
        return tv
    }

    private fun showNoteOptionsMenu(note: Notes, anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add(0, 1, 0, "View Details")
        popup.menu.add(0, 2, 1, "Update Note")
        popup.menu.add(0, 3, 2, "Delete Note")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showNoteDetails(note)
                    true
                }
                2 -> {
                    showEditNoteBottomSheet(note)
                    true
                }
                3 -> {
                    showDeleteNoteDialog(note)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showNoteDetails(note: Notes) {
        currentDetailNote = note
        val b = _binding ?: return
        b.composeViewDetail.visibility = View.VISIBLE
        renderNoteDetailScreen(note)
    }

    private fun hideNoteDetails() {
        currentDetailNote = null
        val b = _binding ?: return
        b.composeViewDetail.visibility = View.GONE
        loadData()
    }

    private fun renderNoteDetailScreen(note: Notes) {
        val b = _binding ?: return
        b.composeViewDetail.setContent {
            StudentOrganizerTheme {
                NoteDetailScreen(
                    note = note,
                    onBackClick = {
                        hideNoteDetails()
                    },
                    onEditNote = { n ->
                        showEditNoteBottomSheet(n)
                    },
                    onDeleteNote = { n ->
                        showDeleteNoteDialog(n) {
                            hideNoteDetails()
                        }
                    }
                )
            }
        }
    }

    private fun showAddNoteBottomSheet() {
        val bottomSheet = NoteBottomSheetDialogFragment.newInstance(
            note = null,
            courses = courseList
        ) { title, subject, content, attachmentUri, attachmentName, attachmentType ->
            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            val newNote = Notes(
                title = title,
                subject = subject,
                content = content,
                date = date,
                attachmentUri = attachmentUri,
                attachmentName = attachmentName,
                attachmentType = attachmentType
            )
            viewLifecycleOwner.lifecycleScope.launch {
                database.notesDao().insertNote(newNote)
                if (_binding != null) {
                    loadData()
                }
                context?.let {
                    Toast.makeText(it, "Note added for $subject!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show(parentFragmentManager, "NoteBottomSheet")
    }

    private fun showEditNoteBottomSheet(note: Notes) {
        val bottomSheet = NoteBottomSheetDialogFragment.newInstance(
            note = note,
            courses = courseList
        ) { title, subject, content, attachmentUri, attachmentName, attachmentType ->
            val updatedNote = Notes(
                id = note.id,
                title = title,
                subject = subject,
                content = content,
                date = note.date,
                attachmentUri = attachmentUri,
                attachmentName = attachmentName,
                attachmentType = attachmentType
            )
            viewLifecycleOwner.lifecycleScope.launch {
                database.notesDao().updateNote(updatedNote)
                if (_binding != null) {
                    loadData()
                }
                context?.let {
                    Toast.makeText(it, "Note updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        bottomSheet.show(parentFragmentManager, "NoteBottomSheet")
    }

    private fun showDeleteNoteDialog(note: Notes, onDeleted: (() -> Unit)? = null) {
        val ctx = context ?: return
        MaterialAlertDialogBuilder(ctx)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete '${note.title}' for ${note.subject}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.notesDao().deleteNote(note)
                    if (_binding != null) {
                        loadData()
                    }
                    onDeleted?.invoke()
                    Toast.makeText(ctx, "Deleted note '${note.title}'", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback?.remove()
        backPressedCallback = null
        _binding = null
    }
}