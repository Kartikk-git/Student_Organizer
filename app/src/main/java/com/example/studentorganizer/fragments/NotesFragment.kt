package com.example.studentorganizer.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentorganizer.adapter.NoteAdapter
import com.example.studentorganizer.database.AppDatabase
import com.example.studentorganizer.databinding.FragmentNotesBinding
import com.example.studentorganizer.database.entity.Notes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var database: AppDatabase

    private val notes = mutableListOf<Notes>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotesBinding.inflate(
            inflater,
            container,
            false
        )

        database = AppDatabase.getDatabase(requireContext().applicationContext)

        setupRecyclerView()
        loadNotes()

        binding.fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            notes = notes,
            onEdit = { note ->
                showEditNoteDialog(note)
            },
            onDelete = { note ->
                deleteNote(note)
            }
        )

        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = noteAdapter
    }

    // READ
    private fun loadNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            val allNotes = database.notesDao().getAllNotes()
            val b = _binding ?: return@launch
            notes.clear()
            notes.addAll(allNotes)
            noteAdapter.notifyDataSetChanged()
        }
    }

    // CREATE
    private fun showAddNoteDialog() {
        val ctx = context ?: return
        val titleInput = EditText(ctx).apply {
            hint = "Note title"
        }
        val subjectInput = EditText(ctx).apply {
            hint = "Subject"
        }
        val contentInput = EditText(ctx).apply {
            hint = "Note content"
            minLines = 4
            gravity = Gravity.TOP
        }

        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
            addView(titleInput)
            addView(subjectInput)
            addView(contentInput)
        }

        val dialog = AlertDialog.Builder(ctx)
            .setTitle("Add Note")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                val subject = subjectInput.text.toString().trim()
                val content = contentInput.text.toString().trim()

                if (title.isEmpty()) {
                    titleInput.error = "Enter title"
                    return@setOnClickListener
                }
                if (subject.isEmpty()) {
                    subjectInput.error = "Enter subject"
                    return@setOnClickListener
                }
                if (content.isEmpty()) {
                    contentInput.error = "Enter content"
                    return@setOnClickListener
                }

                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                val note = Notes(
                    title = title,
                    subject = subject,
                    content = content,
                    date = date
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    database.notesDao().insertNote(note)
                    if (_binding != null) {
                        loadNotes()
                    }
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // UPDATE
    private fun showEditNoteDialog(note: Notes) {
        val ctx = context ?: return
        val titleInput = EditText(ctx).apply {
            setText(note.title)
            hint = "Note title"
        }
        val subjectInput = EditText(ctx).apply {
            setText(note.subject)
            hint = "Subject"
        }
        val contentInput = EditText(ctx).apply {
            setText(note.content)
            hint = "Note content"
            minLines = 4
            gravity = Gravity.TOP
        }

        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
            addView(titleInput)
            addView(subjectInput)
            addView(contentInput)
        }

        val dialog = AlertDialog.Builder(ctx)
            .setTitle("Edit Note")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                val subject = subjectInput.text.toString().trim()
                val content = contentInput.text.toString().trim()

                if (title.isEmpty()) {
                    titleInput.error = "Enter title"
                    return@setOnClickListener
                }
                if (subject.isEmpty()) {
                    subjectInput.error = "Enter subject"
                    return@setOnClickListener
                }
                if (content.isEmpty()) {
                    contentInput.error = "Enter content"
                    return@setOnClickListener
                }

                val updatedNote = Notes(
                    id = note.id,
                    title = title,
                    subject = subject,
                    content = content,
                    date = note.date
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    database.notesDao().updateNote(updatedNote)
                    if (_binding != null) {
                        loadNotes()
                    }
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // DELETE
    private fun deleteNote(note: Notes) {
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete '${note.title}'?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.notesDao().deleteNote(note)
                    if (_binding != null) {
                        loadNotes()
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}