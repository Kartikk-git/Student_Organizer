package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.R
import com.example.studentorganizer.model.Course

class CourseAdapter(
    private val courseList: ArrayList<Course>
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvCourseCode: TextView = itemView.findViewById(R.id.tvCourseCode)
        val tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressCourse)

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
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}