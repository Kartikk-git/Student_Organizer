package com.example.studentorganizer.database.dao

import androidx.room.*
import com.example.studentorganizer.database.entity.AttendanceRecord

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records")
    suspend fun getAllAttendanceRecords(): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE dateString = :dateString LIMIT 1")
    suspend fun getAttendanceForDate(dateString: String): AttendanceRecord?

    @Query("DELETE FROM attendance_records WHERE dateString = :dateString")
    suspend fun deleteAttendanceByDate(dateString: String)

    @Query("SELECT COUNT(*) FROM attendance_records WHERE status = 'PRESENT'")
    suspend fun getPresentCount(): Int

    @Query("SELECT COUNT(*) FROM attendance_records WHERE status = 'ABSENT'")
    suspend fun getAbsentCount(): Int

    @Query("SELECT COUNT(*) FROM attendance_records")
    suspend fun getTotalCount(): Int

    @Delete
    suspend fun deleteAttendance(record: AttendanceRecord)
}
