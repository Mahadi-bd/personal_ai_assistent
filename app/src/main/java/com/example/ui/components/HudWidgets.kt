package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskEntity
import com.example.data.weather.weatherCodeToText
import com.example.ui.WeatherUiState
import com.example.ui.theme.CyberBackgroundDark
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.cyberCard

private fun weatherIcon(code: Int): ImageVector = when (code) {
    0 -> Icons.Default.WbSunny
    1, 2 -> Icons.Default.WbCloudy
    in 51..67, in 80..82 -> Icons.Default.Grain
    in 71..77, 85, 86 -> Icons.Default.AcUnit
    95, 96, 99 -> Icons.Default.Thunderstorm
    else -> Icons.Default.WbCloudy
}

@Composable
fun WeatherWidget(
    state: WeatherUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .cyberCard()
            .padding(14.dp)
            .testTag("weather_widget")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEATHER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                if (state is WeatherUiState.Success) {
                    Text(text = state.info.city, fontSize = 11.sp, color = NeonCyan, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (state) {
                is WeatherUiState.Loading -> {
                    Text(text = "Loading weather...", fontSize = 12.sp, color = TextSecondary)
                }
                is WeatherUiState.Error -> {
                    Text(text = "Weather unavailable. Check your internet connection.", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap to retry",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.clickable(onClick = onRetry)
                    )
                }
                is WeatherUiState.Success -> {
                    val info = state.info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${info.tempC}°",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(text = weatherCodeToText(info.code), fontSize = 11.sp, color = TextSecondary)
                        }
                        Icon(
                            imageVector = weatherIcon(info.code),
                            contentDescription = "Weather icon",
                            tint = NeonCyan,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        info.days.forEach { day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = day.label, fontSize = 9.sp, color = TextMuted)
                                Icon(
                                    imageVector = weatherIcon(day.code),
                                    contentDescription = null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${day.maxTempC}°",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Data: Open-Meteo.com", fontSize = 8.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
fun ProductivityScoreWidget(
    percent: Int,
    totalTasks: Int,
    doneTasks: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .cyberCard()
            .padding(14.dp)
            .testTag("productivity_widget")
    ) {
        Column {
            Text(
                text = "PROGRESS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar (real data: completed tasks / all tasks)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberBackgroundDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percent.coerceIn(0, 100) / 100f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple, NeonMagenta)))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Tasks done", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = if (totalTasks == 0) "No tasks yet" else "$doneTasks of $totalTasks",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyberBackgroundDark)
                        .border(
                            2.5.dp,
                            Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonBlue, NeonCyan)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$percent%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun AiToolsGrid(
    onToolClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tools = listOf(
        Triple("Summarize", Icons.AutoMirrored.Filled.Subject, NeonCyan),
        Triple("Translate", Icons.Default.Translate, NeonBlue),
        Triple("Write", Icons.Default.Edit, NeonPurple),
        Triple("Analyze", Icons.Default.Insights, NeonMagenta),
        Triple("Design", Icons.Default.Palette, NeonGreen),
        Triple("Code", Icons.Default.Code, NeonAmber)
    )

    Box(
        modifier = modifier
            .cyberCard()
            .padding(14.dp)
            .testTag("ai_tools_grid")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI TOOLS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Explore all tools ›",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    modifier = Modifier.clickable { onToolClicked("All Tools") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2 rows of 3 columns
            for (row in tools.chunked(3)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (name, icon, color) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberBackgroundDark.copy(alpha = 0.8f))
                                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .clickable { onToolClicked(name) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = name,
                                    tint = color,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TodayScheduleWidget(
    tasks: List<TaskEntity>,
    onToggleTask: (Long, Boolean) -> Unit,
    onViewCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .cyberCard()
            .padding(14.dp)
            .testTag("schedule_widget")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "আপনার টাস্ক",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ম্যানেজ ›",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    modifier = Modifier.clickable(onClick = onViewCalendar)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks yet. Add one in the Tools tab, or just say \"add task ...\".",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            tasks.take(4).forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle task",
                            tint = if (task.isCompleted) NeonGreen else TextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onToggleTask(task.id, task.isCompleted) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.timeString,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.title,
                            fontSize = 11.sp,
                            color = if (task.isCompleted) TextMuted else TextPrimary,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "${task.durationMinutes} min",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun SmartShortcutsWidget(
    onShortcutClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shortcuts = listOf(
        "Meeting Summary" to "Summarize your meeting",
        "Email Assistant" to "Draft professional email",
        "Study Helper" to "Explain any topic",
        "Plan My Day" to "Schedule & prioritize"
    )

    Box(
        modifier = modifier
            .cyberCard()
            .padding(14.dp)
            .testTag("shortcuts_widget")
    ) {
        Column {
            Text(
                text = "SMART SHORTCUTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            shortcuts.forEach { (title, subtitle) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShortcutClicked(title) }
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = subtitle, fontSize = 9.sp, color = TextSecondary)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingMicButton(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_rings")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clickable(onClick = onClick)
                .testTag("mic_hud_button"),
            contentAlignment = Alignment.Center
        ) {
            // Animated Glowing concentric rings
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                (if (isListening) NeonMagenta else NeonCyan).copy(alpha = 0.45f),
                                NeonPurple.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Inner Ring
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (isListening) NeonMagenta else NeonCyan,
                                NeonPurple
                            )
                        )
                    )
                    .border(2.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input Microphone",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isListening) "Listening... Tap to stop" else "Tap to speak",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isListening) NeonMagenta else NeonCyan
        )
    }
}
