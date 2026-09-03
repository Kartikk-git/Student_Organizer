package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.R
import com.example.studentorganizer.database.entity.Course
import com.google.android.material.button.MaterialButtonToggleGroup

class CourseAdapter(
    private val courseList: MutableList<Course>,
    var onItemClick: ((Course) -> Unit)? = null,
    var onItemLongClick: ((Course) -> Unit)? = null
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseCode: TextView = itemView.findViewById(R.id.tvCourseCode)
        val tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressCourse)
        val toggleGroup: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_courses, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]

        holder.tvCourseCode.text = course.code
        holder.tvCourseName.text = course.name
        holder.tvProgress.text = "${course.progress}%"
        holder.progressBar.progress = course.progress

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(course)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(course)
            true
        }

        holder.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnOverview -> {
                        // Overview selected
                    }
                    R.id.btnAssignment -> {
                        Toast.makeText(holder.itemView.context, "Viewing assignments for ${course.code}", Toast.LENGTH_SHORT).show()
                    }
                    R.id.btnMaterials -> {
                        Toast.makeText(holder.itemView.context, "Course materials for ${course.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = courseList.size

    fun updateList(newList: List<Course>) {
        courseList.clear()
        courseList.addAll(newList)
        notifyDataSetChanged()
    }
}