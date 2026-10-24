package com.example.studentorganizer.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_records",
    indices = [Index(value = ["dateString"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateString: String,
    val status: String, // "PRESENT" or "ABSENT"
    val timestamp: Long = System.currentTimeMillis()
)
