package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.R
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.databinding.ItemNoteHeaderBinding
import com.example.studentorganizer.databinding.ItemNotesBinding

sealed class NoteListItem {
    data class SubjectHeader(val subject: String, val courseName: String?, val count: Int) : NoteListItem()
    data class NoteItem(val note: Notes) : NoteListItem()
}

class NoteAdapter(
    private val items: MutableList<NoteListItem> = mutableListOf(),
    var onItemClick: ((Notes) -> Unit)? = null,
    var onMoreOptionsClick: ((Notes, View) -> Unit)? = null,
    var onItemLongClick: ((Notes) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_NOTE = 1
    }

    class HeaderViewHolder(val binding: ItemNoteHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    class NoteViewHolder(val binding: ItemNotesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NoteListItem.SubjectHeader -> VIEW_TYPE_HEADER
            is NoteListItem.NoteItem -> VIEW_TYPE_NOTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = ItemNoteHeaderBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemNotesBinding.inflate(inflater, parent, false)
            NoteViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NoteListItem.SubjectHeader -> {
                val h = holder as HeaderViewHolder
                h.binding.tvHeaderSubjectCode.text = item.subject
                h.binding.tvHeaderCourseName.text = if (!item.courseName.isNullOrBlank()) item.courseName else item.subject
                h.binding.tvHeaderNotesCount.text = "${item.count} ${if (item.count == 1) "Note" else "Notes"}"
            }
            is NoteListItem.NoteItem -> {
                val h = holder as NoteViewHolder
                val note = item.note

                h.binding.tvNoteTitle.text = note.title
                h.binding.tvNoteSubject.text = note.subject
                h.binding.tvNoteContent.text = note.content
                h.binding.tvNoteDate.text = note.date

                // Attachment badge
                if (!note.attachmentUri.isNullOrBlank()) {
                    h.binding.layoutAttachmentBadge.visibility = View.VISIBLE
                    if (note.attachmentType.equals("PDF", ignoreCase = true)) {
                        h.binding.tvAttachmentText.text = "PDF: ${note.attachmentName ?: "Document"}"
                    } else {
                        h.binding.tvAttachmentText.text = "Photo: ${note.attachmentName ?: "Attached"}"
                    }
                } else {
                    h.binding.layoutAttachmentBadge.visibility = View.GONE
                }

                if (onItemClick != null) {
                    h.itemView.setOnClickListener {
                        onItemClick?.invoke(note)
                    }
                    h.itemView.isClickable = true
                } else {
                    h.itemView.setOnClickListener(null)
                    h.itemView.isClickable = false
                }

                h.itemView.setOnLongClickListener {
                    onItemLongClick?.invoke(note)
                    true
                }

                h.binding.btnMoreOptions.setOnClickListener { v ->
                    onMoreOptionsClick?.invoke(note, v)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setGroupedNotes(notes: List<Notes>, courses: List<Course>, filterSubject: String? = null) {
        items.clear()
        val filtered = if (filterSubject.isNullOrBlank() || filterSubject.equals("ALL", ignoreCase = true)) {
            notes
        } else {
            notes.filter { it.subject.trim().equals(filterSubject.trim(), ignoreCase = true) }
        }

        // Group notes by subject
        val grouped = filtered.groupBy { it.subject.trim() }
        grouped.forEach { (subject, notesForSubject) ->
            val course = courses.firstOrNull { it.code.trim().equals(subject, ignoreCase = true) }
            items.add(NoteListItem.SubjectHeader(subject, course?.name, notesForSubject.size))
            notesForSubject.forEach { note ->
                items.add(NoteListItem.NoteItem(note))
            }
        }
        notifyDataSetChanged()
    }
}