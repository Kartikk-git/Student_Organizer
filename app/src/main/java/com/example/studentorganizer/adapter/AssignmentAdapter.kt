package com.example.studentorganizer.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.databinding.ItemAssignmentBinding

class AssignmentAdapter(
    private val assignments: MutableList<Assignment>,
    var onItemClick: ((Assignment) -> Unit)? = null,
    var onMoreOptionsClick: ((Assignment, View) -> Unit)? = null,
    var onStatusToggle: ((Assignment) -> Unit)? = null,
    var onItemLongClick: ((Assignment) -> Unit)? = null
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    class AssignmentViewHolder(
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

        val isCompleted = assignment.status.equals("Completed", ignoreCase = true)
        if (isCompleted) {
            holder.binding.tvAssignmentStatus.text = "Completed"
            holder.binding.tvAssignmentStatus.setTextColor(Color.parseColor("#2E7D32"))
            holder.binding.tvAssignmentStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
            holder.binding.btnToggleStatus.text = "Mark Pending"
        } else {
            holder.binding.tvAssignmentStatus.text = "Pending"
            holder.binding.tvAssignmentStatus.setTextColor(Color.parseColor("#E65100"))
            holder.binding.tvAssignmentStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFF3E0"))
            holder.binding.btnToggleStatus.text = "Mark Done"
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(assignment)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(assignment)
            true
        }

        holder.binding.btnMoreOptions.setOnClickListener { v ->
            onMoreOptionsClick?.invoke(assignment, v)
        }

        holder.binding.btnToggleStatus.setOnClickListener {
            onStatusToggle?.invoke(assignment)
        }
    }

    override fun getItemCount(): Int = assignments.size

    fun updateList(newList: List<Assignment>) {
        assignments.clear()
        assignments.addAll(newList)
        notifyDataSetChanged()
    }
}