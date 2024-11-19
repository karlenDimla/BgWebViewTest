package com.bgwebviewtest.pirtest.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SimulationUrl::class], version = 1, exportSchema = false)
abstract class SimulationDatabase : RoomDatabase() {
    abstract fun urlsDao(): UrlsDao

    companion object {
        @Volatile
        private var INSTANCE: SimulationDatabase? = null

        fun getInstance(context: Context): SimulationDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): SimulationDatabase {
            return Room.databaseBuilder(
                context,
                SimulationDatabase::class.java,
                "app_database"
            )
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING) // Enable WAL for multi-process
                .build()
        }
    }
}