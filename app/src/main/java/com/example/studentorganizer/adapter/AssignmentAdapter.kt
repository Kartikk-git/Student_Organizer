package com.example.studentorganizer.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.databinding.ItemAssignmentBinding

class AssignmentAdapter(
    private val assignments: MutableList<Assignment>,
    private val onStatusToggle: (Assignment) -> Unit,
    private val onDelete: (Assignment) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(
        val binding: ItemAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AssignmentViewHolder,
        position: Int
    ) {
        val assignment = assignments[position]

        holder.binding.tvAssignmentName.text = assignment.title
        holder.binding.tvAssignmentSubject.text = assignment.subject
        holder.binding.tvAssignmentDue.text = "Due: ${assignment.dueDate}"
        holder.binding.tvAssignmentStatus.text = assignment.status

        if (assignment.status.equals("Completed", ignoreCase = true)) {
            holder.binding.tvAssignmentStatus.setTextColor(Color.parseColor("#2E7D32")) // Green
        } else {
            holder.binding.tvAssignmentStatus.setTextColor(Color.parseColor("#D84315")) // Orange-Red
        }

        holder.itemView.setOnClickListener {
            onStatusToggle(assignment)
        }

        holder.itemView.setOnLongClickListener {
            onDelete(assignment)
            true
        }
    }

    override fun getItemCount(): Int = assignments.size

    fun updateList(newList: List<Assignment>) {
        assignments.clear()
        assignments.addAll(newList)
        notifyDataSetChanged()
    }
}