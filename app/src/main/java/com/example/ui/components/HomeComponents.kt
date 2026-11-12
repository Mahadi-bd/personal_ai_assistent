package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberBackgroundDark
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.cyberCard

// ---------------------------------------------------------------------------
// Greeting card: "হ্যালো, Name!" + voice style pills + progress dashes
// ---------------------------------------------------------------------------
@Composable
fun GreetingCard(
    userName: String,
    completionPercent: Int,
    voiceStyle: String,
    onVoiceStyleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cyberCard(cornerRadius = 28.dp)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag("greeting_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "হ্যালো, $userName!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "আজ আপনি কী করতে চান?", fontSize = 14.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            VoicePill("পুরুষ কণ্ঠ", voiceStyle == "male") { onVoiceStyleChange("male") }
            Spacer(modifier = Modifier.width(6.dp))
            VoicePill("নারী কণ্ঠ", voiceStyle == "female") { onVoiceStyleChange("female") }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 14 dashes = today's task progress
        val total = 14
        val filled = (completionPercent.coerceIn(0, 100) * total + 50) / 100
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            for (i in 0 until total) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i < filled) NeonCyan else Color(0x26FFFFFF))
                )
            }
        }
    }
}

@Composable
private fun VoicePill(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (selected) Brush.horizontalGradient(listOf(Color(0x558B5CF6), Color(0x33B026FF)))
                else Brush.horizontalGradient(listOf(Color(0x22FFFFFF), Color(0x11FFFFFF)))
            )
            .border(1.dp, if (selected) NeonPurple else Color(0x44FFFFFF), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) TextPrimary else TextSecondary,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Assistant card: platform rings + avatar + status + speech bubble
// ---------------------------------------------------------------------------
@Composable
fun AssistantCard(
    assistantName: String,
    statusText: String,
    speechText: String,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cyberCard(cornerRadius = 28.dp)
            .padding(16.dp)
            .testTag("hologram_avatar_deck")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LIFE EASY  •  $assistantName",
                fontSize = 13.sp,
                color = NeonCyan,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonGreen))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "সক্রিয়", fontSize = 13.sp, color = NeonGreen, fontWeight = FontWeight.SemiBold)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glowing platform rings
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height * 0.80f
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.30f), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = size.width * 0.42f
                    ),
                    topLeft = Offset(cx - size.width * 0.42f, cy - size.width * 0.12f),
                    size = Size(size.width * 0.84f, size.width * 0.24f)
                )
                val rings = listOf(
                    Triple(0.46f, NeonCyan, 3f),
                    Triple(0.38f, NeonPurple, 3f),
                    Triple(0.28f, NeonBlue, 2.5f),
                    Triple(0.18f, NeonCyan, 2f)
                )
                rings.forEach { (f, c, w) ->
                    val width = size.width * f * 2f
                    val height = width * 0.26f
                    drawOval(
                        color = c.copy(alpha = 0.75f),
                        topLeft = Offset(cx - width / 2f, cy - height / 2f),
                        size = Size(width, height),
                        style = Stroke(width = w)
                    )
                }
            }

            // Avatar
            Box(
                modifier = Modifier
                    .offset(y = (-14).dp)
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonBlue, NeonCyan)), CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hologram_avatar_1793648093192),
                    contentDescription = "Assistant $assistantName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Status pill
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x99101828))
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = statusText, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        // Speech bubble (tap to hear it)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x22FFFFFF))
                .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
                .clickable(onClick = onSpeak)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = speechText,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = NeonCyan, modifier = Modifier.size(20.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Today summary with three rings (all numbers come from the user's own data)
// ---------------------------------------------------------------------------
@Composable
fun TodaySummaryCard(
    doneTasks: Int,
    totalTasks: Int,
    expenseToday: Long,
    expenseShare: Float,
    notesCount: Int,
    onDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cyberCard(cornerRadius = 28.dp)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag("summary_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "আজকের সারাংশ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                text = "বিস্তারিত",
                fontSize = 14.sp,
                color = NeonCyan,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onDetails).padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RingStat(
                progress = if (totalTasks == 0) 0f else doneTasks.toFloat() / totalTasks,
                centerText = "$doneTasks/$totalTasks",
                label = "কাজ সম্পন্ন",
                color = NeonGreen
            )
            RingStat(
                progress = expenseShare,
                centerText = "৳$expenseToday",
                label = "আজকের খরচ",
                color = NeonCyan
            )
            RingStat(
                progress = (notesCount / 10f).coerceIn(0f, 1f),
                centerText = "$notesCount",
                label = "নোট",
                color = NeonAmber
            )
        }
    }
}

@Composable
private fun RingStat(progress: Float, centerText: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = 9.dp.toPx()
                val arcSize = Size(size.width - strokePx, size.height - strokePx)
                val topLeft = Offset(strokePx / 2f, strokePx / 2f)
                drawArc(
                    color = Color(0x22FFFFFF),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
            Text(
                text = centerText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}

// ---------------------------------------------------------------------------
// Smart shortcuts 2x2 grid (each one opens the AI chat with a ready prompt)
// ---------------------------------------------------------------------------
@Composable
fun SmartShortcutsGrid(
    onShortcut: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().testTag("smart_shortcuts")) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ShortcutCard("নোট সহায়ক", "AI Notes", "নোট সাজিয়ে লিখুন", Icons.Default.Description, NeonCyan, Modifier.weight(1f)) { onShortcut("notes") }
            ShortcutCard("ইমেইল অ্যাসিস্ট্যান্ট", "Drafts", "পেশাদার ইমেল", Icons.Default.Email, NeonPurple, Modifier.weight(1f)) { onShortcut("drafts") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ShortcutCard("স্টাডি হেল্পার", "Explain", "যেকোনো বিষয় জানুন", Icons.Default.MenuBook, NeonBlue, Modifier.weight(1f)) { onShortcut("explain") }
            ShortcutCard("দিনের পরিকল্পনা", "Plan", "কাজের ক্রম সাজান", Icons.Default.Navigation, NeonGreen, Modifier.weight(1f)) { onShortcut("plan") }
        }
    }
}

@Composable
private fun ShortcutCard(
    banglaTitle: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .cyberCard(cornerRadius = 22.dp)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(tint.copy(alpha = 0.18f))
                .border(1.dp, tint.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = banglaTitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
