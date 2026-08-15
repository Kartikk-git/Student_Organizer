package com.example.studentorganizer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentorganizer.database.dao.NotesDao
import com.example.studentorganizer.database.entity.Notes

@Database(
    entities = [Notes::class],
    version = 11,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                ).fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}