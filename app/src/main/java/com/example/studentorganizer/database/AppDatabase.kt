package com.example.studentorganizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentorganizer.database.dao.AssignmentDao
import com.example.studentorganizer.database.dao.AttendanceDao
import com.example.studentorganizer.database.dao.CourseDao
import com.example.studentorganizer.database.dao.NotesDao
import com.example.studentorganizer.database.dao.UserDao
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.AttendanceRecord
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.database.entity.User

@Database(
    entities = [
        User::class,
        Notes::class,
        Course::class,
        Assignment::class,
        AttendanceRecord::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun notesDao(): NotesDao
    abstract fun courseDao(): CourseDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {

        private val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE notes ADD COLUMN attachmentUri TEXT")
                db.execSQL("ALTER TABLE notes ADD COLUMN attachmentName TEXT")
                db.execSQL("ALTER TABLE notes ADD COLUMN attachmentType TEXT")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_organizer_database"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}