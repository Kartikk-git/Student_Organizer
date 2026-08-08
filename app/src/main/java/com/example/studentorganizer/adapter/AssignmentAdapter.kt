package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.databinding.ItemAssignmentBinding
import com.example.studentorganizer.model.Assignment


class AssignmentAdapter(
    private val assignments: List<Assignment>
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
    }

    override fun getItemCount(): Int = assignments.size
}