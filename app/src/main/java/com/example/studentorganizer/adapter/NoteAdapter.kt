package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.databinding.ItemNotesBinding
import com.example.studentorganizer.model.Notes

class NoteAdapter(
    private val notes: List<Notes>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(
        val binding: ItemNotesBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {

        val binding = ItemNotesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {

        val note = notes[position]

        holder.binding.tvNoteTitle.text = note.title
        holder.binding.tvNoteSubject.text = note.subject
        holder.binding.tvNoteContent.text = note.content
        holder.binding.tvNoteDate.text = note.date
    }

    override fun getItemCount(): Int = notes.size
}