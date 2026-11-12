package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secure_notes")
data class SecureNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val tag: String = "Confidential",
    val isLocked: Boolean = false,
    val reminderTime: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
