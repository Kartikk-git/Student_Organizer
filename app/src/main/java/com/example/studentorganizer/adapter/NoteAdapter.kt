package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.databinding.ItemNotesBinding

class NoteAdapter(
    private val notes: MutableList<Notes>,
    var onItemClick: ((Notes) -> Unit)? = null,
    var onMoreOptionsClick: ((Notes, View) -> Unit)? = null,
    var onItemLongClick: ((Notes) -> Unit)? = null
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(
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
            onItemClick?.invoke(note)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(note)
            true
        }

        holder.binding.btnMoreOptions.setOnClickListener { v ->
            onMoreOptionsClick?.invoke(note, v)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateList(newNotes: List<Notes>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }
}