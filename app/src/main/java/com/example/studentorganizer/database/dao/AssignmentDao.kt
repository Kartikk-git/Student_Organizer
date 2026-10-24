package com.example.studentorganizer.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studentorganizer.database.entity.Assignment

@Dao
interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assignments: List<Assignment>)

    @Query("SELECT * FROM assignments ORDER BY id DESC")
    suspend fun getAllAssignments(): List<Assignment>

    @Query("SELECT COUNT(*) FROM assignments WHERE status = 'Pending'")
    suspend fun getPendingAssignmentsCount(): Int

    @Query("SELECT COUNT(*) FROM assignments")
    suspend fun getAssignmentsCount(): Int

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)
}
