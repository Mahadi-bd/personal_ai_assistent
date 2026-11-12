package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppTab
import com.example.ui.MahadiViewModel
import com.example.ui.components.AssistantCard
import com.example.ui.components.GreetingCard
import com.example.ui.components.SmartShortcutsGrid
import com.example.ui.components.TodayScheduleWidget
import com.example.ui.components.TodaySummaryCard
import com.example.ui.components.WeatherWidget
import com.example.ui.components.rememberMicToggle
import com.example.ui.theme.AppBackgroundBrush
import com.example.ui.theme.CyberBackgroundDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.cyberCard
import java.util.Calendar

@Composable
fun HudDashboardScreen(
    viewModel: MahadiViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val assistantName by viewModel.assistantName.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val speechText by viewModel.hologramSpeech.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val ledger by viewModel.ledgerEntries.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val completionPercent by viewModel.completionPercent.collectAsStateWithLifecycle()
    val listeningQuery by viewModel.listeningQuery.collectAsStateWithLifecycle()
    val voiceStyle by viewModel.voiceStyle.collectAsStateWithLifecycle()
    val onMicClick = rememberMicToggle(viewModel)

    var quickInputText by remember { mutableStateOf("") }

    // Today's money numbers (from the user's own ledger)
    val startOfDay = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val todayEntries = ledger.filter { it.timestamp >= startOfDay }
    val expenseToday = todayEntries.filter { it.isExpense }.sumOf { it.amount }
    val incomeToday = todayEntries.filter { !it.isExpense }.sumOf { it.amount }
    val expenseShare = if (expenseToday + incomeToday <= 0.0) 0f else (expenseToday / (expenseToday + incomeToday)).toFloat()

    val statusText = when {
        isListening -> "শুনছি..."
        isGenerating -> "ভাবছি..."
        else -> "প্রস্তুত"
    }

    fun shortcutPrompt(kind: String): String = when (kind) {
        "notes" -> "Help me write clear, well-structured notes from my meeting or lesson. Ask me for the details first."
        "drafts" -> "Help me write a professional email. Ask me who it is for and what it should say."
        "explain" -> "I want to learn a topic. Ask me what I want to understand, then explain it simply with examples."
        else -> {
            val pending = tasks.filter { !it.isCompleted }
            if (pending.isEmpty()) {
                "Help me plan my day. Ask me what I need to do and suggest a sensible order."
            } else {
                "Plan my day. My pending tasks: " +
                    pending.joinToString("; ") { "${it.title} (${it.timeString}, ${it.durationMinutes} min)" } +
                    ". Suggest the best order and short tips."
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackgroundBrush)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            GreetingCard(
                userName = userName,
                completionPercent = completionPercent,
                voiceStyle = voiceStyle,
                onVoiceStyleChange = { viewModel.setVoiceStyle(it) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            AssistantCard(
                assistantName = assistantName,
                statusText = statusText,
                speechText = speechText,
                onSpeak = { viewModel.speakCurrent() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick text / voice input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .cyberCard(cornerRadius = 24.dp)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickInputText,
                    onValueChange = { quickInputText = it },
                    placeholder = {
                        Text("কিছু জিজ্ঞাসা করুন... / Ask anything", fontSize = 13.sp, color = TextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_text_input")
                )
                IconButton(onClick = onMicClick, modifier = Modifier.testTag("mic_button")) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice input",
                        tint = if (isListening) NeonMagenta else NeonCyan
                    )
                }
                IconButton(
                    onClick = {
                        if (quickInputText.isNotBlank()) {
                            viewModel.submitVoiceQuery(quickInputText)
                            quickInputText = ""
                        }
                    },
                    modifier = Modifier.testTag("quick_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TodaySummaryCard(
                doneTasks = tasks.count { it.isCompleted },
                totalTasks = tasks.size,
                expenseToday = expenseToday.toLong(),
                expenseShare = expenseShare,
                notesCount = notes.size,
                onDetails = { viewModel.selectTab(AppTab.TOOLS) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            WeatherWidget(
                state = weather,
                onRetry = { viewModel.refreshWeather() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "স্মার্ট শর্টকাট",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )
            SmartShortcutsGrid(
                onShortcut = { kind ->
                    viewModel.sendChatMessage(shortcutPrompt(kind))
                    viewModel.selectTab(AppTab.CHAT)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            TodayScheduleWidget(
                tasks = tasks,
                onToggleTask = { id, comp -> viewModel.toggleTask(id, comp) },
                onViewCalendar = { viewModel.selectTab(AppTab.TOOLS) }
            )

            Spacer(modifier = Modifier.height(110.dp)) // room for the floating bottom bar
        }

        // Listening overlay
        AnimatedVisibility(
            visible = isListening,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBackgroundDark.copy(alpha = 0.88f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .cyberCard(cornerRadius = 28.dp, borderColor = NeonCyan)
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Listening",
                        tint = NeonMagenta,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "শুনছি...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = listeningQuery.ifBlank { "বলুন: \"আজ আমার কি কি টাস্ক আছে?\" অথবা \"add task buy milk\"" },
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.stopListening() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text(text = "বলা শেষ", color = CyberBackgroundDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "বাতিল",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .clickable { viewModel.cancelListening() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
