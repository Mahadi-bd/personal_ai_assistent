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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.cyberCard

@Composable
fun PersonaStudioScreen(
    viewModel: MahadiViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val currentName by viewModel.assistantName.collectAsStateWithLifecycle()
    val currentRole by viewModel.personaRole.collectAsStateWithLifecycle()
    val currentTone by viewModel.voiceTone.collectAsStateWithLifecycle()
    val currentLang by viewModel.language.collectAsStateWithLifecycle()

    var nameInput by remember(currentName) { mutableStateOf(currentName) }
    var selectedRole by remember(currentRole) { mutableStateOf(currentRole) }
    var selectedTone by remember(currentTone) { mutableStateOf(currentTone) }
    var selectedLang by remember(currentLang) { mutableStateOf(currentLang) }
    val currentUser by viewModel.userName.collectAsStateWithLifecycle()
    val currentCity by viewModel.weatherCity.collectAsStateWithLifecycle()
    var userInput by remember(currentUser) { mutableStateOf(currentUser) }
    var cityInput by remember(currentCity) { mutableStateOf(currentCity) }
    var saveFeedback by remember { mutableStateOf(false) }

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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            ScreenHeader(
                title = "পার্সোনা",
                subtitle = "আপনার সহকারীকে নিজের মতো সাজান"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PERSONA SELECTION
            Text(
                text = "PERSONA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val personas = listOf(
                Triple("Corporate Analyst", "Data-driven insights", Icons.Default.Analytics),
                Triple("Project Manager", "Planning & tracking", Icons.AutoMirrored.Filled.Assignment),
                Triple("Executive Assistant", "Schedule & priorities", Icons.Default.Person),
                Triple("HR Advisor", "People & culture", Icons.Default.Groups),
                Triple("Customer Success", "Support & engagement", Icons.Default.SentimentSatisfied)
            )

            personas.forEach { (role, desc, icon) ->
                val isSelected = selectedRole == role
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyberSurfaceLight else CyberSurfaceGlass)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonPurple else CyberSurfaceBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedRole = role }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else CyberBackgroundDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) NeonCyan else TextSecondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = role, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = desc, fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    if (isSelected) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Selected", tint = NeonPurple, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TONE OF VOICE
            Text(
                text = "TONE OF VOICE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val tones = listOf(
                Pair("Professional", "Clear and concise"),
                Pair("Friendly", "Warm and helpful"),
                Pair("Formal", "Polished and refined")
            )

            tones.forEach { (tone, desc) ->
                val isSelected = selectedTone == tone
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyberSurfaceLight else CyberSurfaceGlass)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonCyan else CyberSurfaceBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTone = tone }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = tone, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = desc, fontSize = 10.sp, color = TextSecondary)
                    }

                    if (isSelected) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Selected", tint = NeonCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LANGUAGE & LOCALIZATION (Bengali / English)
            Text(
                text = "RESPONSE LANGUAGE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("English (US)", "Bengali (বাংলা)").forEach { lang ->
                    val isSelected = selectedLang == lang
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonBlue.copy(alpha = 0.3f) else CyberSurfaceGlass)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) NeonBlue else CyberSurfaceBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedLang = lang }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lang,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // PERSONALIZE YOUR ASSISTANT BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .cyberCard(
                        cornerRadius = 22.dp,
                        borderColor = NeonPurple,
                        backgroundColor = CyberSurfaceGlass
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "PERSONALIZE YOUR ASSISTANT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Assistant Name", color = TextSecondary, fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )


                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it; saveFeedback = false },
                        label = { Text("Your Name", color = TextSecondary, fontSize = 10.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cityInput,
                        onValueChange = { cityInput = it; saveFeedback = false },
                        label = { Text("Weather City", color = TextSecondary, fontSize = 10.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updatePersona(
                                name = nameInput.ifBlank { "Ava" },
                                role = selectedRole,
                                tone = selectedTone,
                                lang = selectedLang
                            )
                            viewModel.updateProfile(userInput, cityInput)
                            saveFeedback = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_persona_changes")
                    ) {
                        Text(
                            text = if (saveFeedback) "✓ Settings Applied!" else "Save Changes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            AboutSection()

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
