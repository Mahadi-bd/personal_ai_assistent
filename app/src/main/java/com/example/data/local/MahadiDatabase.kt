package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskEntity::class,
        SecureNoteEntity::class,
        LedgerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MahadiDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun secureNoteDao(): SecureNoteDao
    abstract fun ledgerDao(): LedgerDao

    companion object {
        @Volatile
        private var INSTANCE: MahadiDatabase? = null

        fun getDatabase(context: Context): MahadiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MahadiDatabase::class.java,
                    "mahadi_ai_database.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
