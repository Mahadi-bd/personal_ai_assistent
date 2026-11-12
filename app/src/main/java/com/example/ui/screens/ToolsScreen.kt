package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MahadiViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AppBackgroundBrush
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBackgroundDark
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.CyberSurfaceLight
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.cyberCard

@Composable
fun ToolsScreen(
    viewModel: MahadiViewModel,
    modifier: Modifier = Modifier
) {
    var selectedToolTab by remember { mutableIntStateOf(0) } // 0: Tasks, 1: Notes, 2: Income/Expense

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val ledger by viewModel.ledgerEntries.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskTime by remember { mutableStateOf("15:00") }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }

    var showAddLedgerDialog by remember { mutableStateOf(false) }
    var newLedgerTitle by remember { mutableStateOf("") }
    var newLedgerAmount by remember { mutableStateOf("") }
    var isExpenseEntry by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackgroundBrush)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            ScreenHeader(
                title = "টুলস",
                subtitle = "টাস্ক, নোট ও আয়-ব্যয় এক জায়গায়",
                trailing = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(NeonGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = "ফোনে সংরক্ষিত", fontSize = 10.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs: Tasks / Secure Notes / Calculator
            TabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = CyberSurfaceGlass,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                        color = NeonCyan
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
            ) {
                Tab(
                    selected = selectedToolTab == 0,
                    onClick = { selectedToolTab = 0 },
                    text = { Text("Tasks (${tasks.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedToolTab == 1,
                    onClick = { selectedToolTab = 1 },
                    text = { Text("Notes (${notes.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedToolTab == 2,
                    onClick = { selectedToolTab = 2 },
                    text = { Text("Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedToolTab) {
                0 -> {
                    // TASKS VIEW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AGENDA & SCHEDULE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan)
                                .clickable { showAddTaskDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("add_task_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = CyberBackgroundDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Task", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberBackgroundDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (tasks.isEmpty()) {
                            item {
                                Text(
                                    text = "No tasks yet. Tap the add button to create your first task.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }
                        items(tasks, key = { it.id }) { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .cyberCard(
                                        cornerRadius = 18.dp,
                                        borderColor = if (task.isCompleted) CyberSurfaceBorder else NeonCyan.copy(alpha = 0.5f),
                                        backgroundColor = CyberSurfaceGlass
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle",
                                        tint = if (task.isCompleted) NeonGreen else NeonCyan,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { viewModel.toggleTask(task.id, task.isCompleted) }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = task.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (task.isCompleted) TextMuted else TextPrimary
                                        )
                                        Text(
                                            text = "${task.timeString} · ${task.durationMinutes} min · ${task.category}",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteTask(task.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }

                1 -> {
                    // SECURE NOTES VIEW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY NOTES (STORED ON THIS DEVICE)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonPurple)
                                .clickable { showAddNoteDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("add_note_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Note", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (notes.isEmpty()) {
                            item {
                                Text(
                                    text = "No notes yet. Tap the add button to write a note. Notes stay on your phone.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }
                        items(notes, key = { it.id }) { note ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .cyberCard(
                                        cornerRadius = 22.dp,
                                        borderColor = NeonPurple.copy(alpha = 0.4f),
                                        backgroundColor = CyberSurfaceGlass
                                    )
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (note.isLocked) Icons.Default.Lock else Icons.AutoMirrored.Filled.Note,
                                            contentDescription = null,
                                            tint = NeonPurple,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = note.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteNote(note.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.content,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )

                                if (note.reminderTime.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "⏰ Reminder: ${note.reminderTime}",
                                        fontSize = 10.sp,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }

                2 -> {
                    // FINANCIAL ACCOUNTING & CALCULATOR
                    val totalIncome = ledger.filter { !it.isExpense }.sumOf { it.amount }
                    val totalExpense = ledger.filter { it.isExpense }.sumOf { it.amount }
                    val netBalance = totalIncome - totalExpense

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .cyberCard(cornerRadius = 18.dp, borderColor = NeonGreen.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Income", fontSize = 10.sp, color = TextSecondary)
                                Text("$${"%.2f".format(totalIncome)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .cyberCard(cornerRadius = 18.dp, borderColor = NeonMagenta.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Expense", fontSize = 10.sp, color = TextSecondary)
                                Text("$${"%.2f".format(totalExpense)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonMagenta)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .cyberCard(cornerRadius = 18.dp, borderColor = NeonCyan.copy(alpha = 0.5f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Net Balance", fontSize = 10.sp, color = TextSecondary)
                                Text("$${"%.2f".format(netBalance)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "LEDGER TRANSACTIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonBlue)
                                .clickable { showAddLedgerDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("+ Entry", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (ledger.isEmpty()) {
                            item {
                                Text(
                                    text = "No income or expense recorded yet.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }
                        items(ledger, key = { it.id }) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .cyberCard(cornerRadius = 18.dp)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(entry.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(entry.category, fontSize = 10.sp, color = TextMuted)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${if (entry.isExpense) "-" else "+"}$${"%.2f".format(entry.amount)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry.isExpense) NeonMagenta else NeonGreen
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteLedger(entry.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // Add Task Modal
        if (showAddTaskDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBackgroundDark.copy(alpha = 0.85f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberCard(borderColor = NeonCyan)
                        .padding(20.dp)
                ) {
                    Text("Add New Task", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Task title (e.g. Code Review)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newTaskTime,
                        onValueChange = { newTaskTime = it },
                        placeholder = { Text("Time (e.g. 15:00)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { showAddTaskDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    viewModel.addTask(newTaskTitle, newTaskTime, 30, "General")
                                    newTaskTitle = ""
                                    showAddTaskDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Save Task", color = CyberBackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Add Note Modal
        if (showAddNoteDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBackgroundDark.copy(alpha = 0.85f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberCard(borderColor = NeonPurple)
                        .padding(20.dp)
                ) {
                    Text("Add Note", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newNoteTitle,
                        onValueChange = { newNoteTitle = it },
                        placeholder = { Text("Note title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        placeholder = { Text("Content & directives...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { showAddNoteDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newNoteTitle.isNotBlank()) {
                                    viewModel.addNote(newNoteTitle, newNoteContent, "Note")
                                    newNoteTitle = ""
                                    newNoteContent = ""
                                    showAddNoteDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Save Note", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Add Ledger Modal
        if (showAddLedgerDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBackgroundDark.copy(alpha = 0.85f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberCard(borderColor = NeonBlue)
                        .padding(20.dp)
                ) {
                    Text("Add Ledger Transaction", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newLedgerTitle,
                        onValueChange = { newLedgerTitle = it },
                        placeholder = { Text("Description (e.g. Cloud Compute)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLedgerAmount,
                        onValueChange = { newLedgerAmount = it },
                        placeholder = { Text("Amount ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { isExpenseEntry = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isExpenseEntry) NeonMagenta else CyberBackgroundDark)
                        ) {
                            Text("Expense", color = Color.White)
                        }
                        Button(
                            onClick = { isExpenseEntry = false },
                            colors = ButtonDefaults.buttonColors(containerColor = if (!isExpenseEntry) NeonGreen else CyberBackgroundDark)
                        ) {
                            Text("Income", color = if (!isExpenseEntry) CyberBackgroundDark else Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { showAddLedgerDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amountVal = newLedgerAmount.toDoubleOrNull() ?: 0.0
                                if (newLedgerTitle.isNotBlank() && amountVal > 0) {
                                    viewModel.addLedger(newLedgerTitle, amountVal, isExpenseEntry, if (isExpenseEntry) "Expense" else "Income")
                                    newLedgerTitle = ""
                                    newLedgerAmount = ""
                                    showAddLedgerDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                        ) {
                            Text("Record", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
