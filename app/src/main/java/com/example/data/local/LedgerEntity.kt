package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledger_entries")
data class LedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val isExpense: Boolean = true,
    val category: String = "Operations",
    val dateString: String = "Today",
    val timestamp: Long = System.currentTimeMillis()
)
