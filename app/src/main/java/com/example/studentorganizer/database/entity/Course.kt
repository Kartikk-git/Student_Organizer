package com.example.studentorganizer.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val name: String,
    val progress: Int
)
