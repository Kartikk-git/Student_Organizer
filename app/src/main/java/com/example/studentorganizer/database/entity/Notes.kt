package com.example.studentorganizer.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subject: String,
    val content: String,
    val date: String,
    val attachmentUri: String? = null,
    val attachmentName: String? = null,
    val attachmentType: String? = null // "IMAGE" or "PDF"
)