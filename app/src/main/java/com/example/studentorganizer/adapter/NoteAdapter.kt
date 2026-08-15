package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.databinding.ItemNotesBinding
import com.example.studentorganizer.database.entity.Notes

class NoteAdapter(
    private val notes: MutableList<Notes>,
    private val onEdit: (Notes) -> Unit,
    private val onDelete: (Notes) -> Unit
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

        holder.itemView.setOnClickListener {
            onEdit(note)
        }

        holder.itemView.setOnLongClickListener {
            onDelete(note)
            true
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateList(newNotes: List<Notes>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }
}