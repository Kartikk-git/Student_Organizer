package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.R
import com.example.studentorganizer.database.entity.Course
import com.google.android.material.button.MaterialButtonToggleGroup

class CourseAdapter(
    private val courseList: MutableList<Course>,
    var onItemClick: ((Course) -> Unit)? = null,
    var onItemLongClick: ((Course) -> Unit)? = null,
    var onMoreOptionsClick: ((Course, View) -> Unit)? = null,
    var onMaterialsClick: ((Course) -> Unit)? = null,
    var getAssignmentStats: ((Course) -> Pair<Int, Int>)? = null, // Pair(completedCount, totalCount)
    var getNotesCount: ((Course) -> Int)? = null
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseCode: TextView = itemView.findViewById(R.id.tvCourseCode)
        val tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        val tvProgressLabel: TextView = itemView.findViewById(R.id.tvProgressLabel)
        val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressCourse)
        val toggleGroup: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleGroup)
        val btnMoreOptions: ImageView = itemView.findViewById(R.id.btnMoreOptions)
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

        // Reset to Overview state by default
        holder.toggleGroup.check(R.id.btnOverview)
        setOverviewState(holder, course)

        // Item click
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(course)
        }

        // Long click
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(course)
            true
        }

        // 3-dot overflow menu
        holder.btnMoreOptions.setOnClickListener { v ->
            onMoreOptionsClick?.invoke(course, v)
        }

        // Toggle Group Listener (Overview, Assignment, Materials)
        holder.toggleGroup.clearOnButtonCheckedListeners()
        holder.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnOverview -> {
                        setOverviewState(holder, course)
                    }
                    R.id.btnAssignment -> {
                        setAssignmentState(holder, course)
                    }
                    R.id.btnMaterials -> {
                        setMaterialsState(holder, course)
                    }
                }
            }
        }
    }

    private fun setMaterialsState(holder: CourseViewHolder, course: Course) {
        val count = getNotesCount?.invoke(course) ?: 0
        holder.tvProgressLabel.text = "Study Material Available"
        if (count <= 0) {
            holder.tvProgress.text = "NA"
            holder.progressBar.progress = 0
        } else {
            holder.tvProgress.text = count.toString()
            holder.progressBar.progress = 100
        }
    }

    private fun setOverviewState(holder: CourseViewHolder, course: Course) {
        holder.tvProgressLabel.text = "Course Progress"
        holder.tvProgress.text = "${course.progress}%"
        holder.progressBar.progress = course.progress
    }

    private fun setAssignmentState(holder: CourseViewHolder, course: Course) {
        val stats = getAssignmentStats?.invoke(course) ?: Pair(0, 0)
        val completed = stats.first
        val total = stats.second

        holder.tvProgressLabel.text = "Assignment Completion"
        if (total == 0) {
            holder.tvProgress.text = "0%"
            holder.progressBar.progress = 0
        } else {
            val percentage = ((completed.toDouble() / total.toDouble()) * 100).toInt()
            holder.tvProgress.text = "$percentage%"
            holder.progressBar.progress = percentage
        }
    }

    override fun getItemCount(): Int = courseList.size

    fun updateList(newList: List<Course>) {
        courseList.clear()
        courseList.addAll(newList)
        notifyDataSetChanged()
    }
}