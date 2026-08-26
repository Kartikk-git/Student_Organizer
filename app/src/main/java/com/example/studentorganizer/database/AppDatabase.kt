package com.example.studentorganizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentorganizer.database.dao.AssignmentDao
import com.example.studentorganizer.database.dao.CourseDao
import com.example.studentorganizer.database.dao.NotesDao
import com.example.studentorganizer.database.dao.UserDao
import com.example.studentorganizer.database.entity.Assignment
import com.example.studentorganizer.database.entity.Course
import com.example.studentorganizer.database.entity.Notes
import com.example.studentorganizer.database.entity.User

@Database(
    entities = [
        User::class,
        Notes::class,
        Course::class,
        Assignment::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun notesDao(): NotesDao
    abstract fun courseDao(): CourseDao
    abstract fun assignmentDao(): AssignmentDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_organizer_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}