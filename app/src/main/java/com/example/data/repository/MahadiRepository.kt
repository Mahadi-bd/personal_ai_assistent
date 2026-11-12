package com.example.data.repository

import com.example.data.local.LedgerDao
import com.example.data.local.LedgerEntity
import com.example.data.local.SecureNoteDao
import com.example.data.local.SecureNoteEntity
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class MahadiRepository(
    private val taskDao: TaskDao,
    private val secureNoteDao: SecureNoteDao,
    private val ledgerDao: LedgerDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()
    val allNotes: Flow<List<SecureNoteEntity>> = secureNoteDao.getAllNotes()
    val allLedgerEntries: Flow<List<LedgerEntity>> = ledgerDao.getAllEntries()

    suspend fun addTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun toggleTaskCompleted(id: Long, completed: Boolean) = taskDao.setCompleted(id, completed)

    suspend fun deleteTask(id: Long) = taskDao.deleteTaskById(id)

    suspend fun addNote(note: SecureNoteEntity): Long = secureNoteDao.insertNote(note)

    suspend fun updateNote(note: SecureNoteEntity) = secureNoteDao.updateNote(note)

    suspend fun deleteNote(id: Long) = secureNoteDao.deleteNoteById(id)

    suspend fun addLedgerEntry(entry: LedgerEntity): Long = ledgerDao.insertEntry(entry)

    suspend fun deleteLedgerEntry(id: Long) = ledgerDao.deleteEntryById(id)
}
