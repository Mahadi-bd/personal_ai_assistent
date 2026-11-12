package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val timeString: String = "12:00",
    val durationMinutes: Int = 30,
    val category: String = "General",
    val isCompleted: Boolean = false,
    val priority: String = "HIGH",
    val createdTimestamp: Long = System.currentTimeMillis()
)
