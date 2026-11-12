package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.R
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.cyberCard

object AppInfo {
    const val BRAND = "LIFE EASY"
    const val DEVELOPER = "MAHADI HASAN JEWEL"

    /** Put the public link of your privacy policy page here (for example your GitHub Pages URL). */
    const val PRIVACY_POLICY_URL = ""
}

private val howToSteps = listOf(
    "Home" to "Greeting, assistant, today's summary (tasks done, today's expense, notes), weather, smart shortcuts and your task list. Tap the circle next to a task to mark it done. Choose পুরুষ কণ্ঠ (male) or নারী কণ্ঠ (female) for spoken replies; tap the speech bubble to hear it.",
    "Ask by typing" to "Type a question in the box on the Home screen or in the Chat tab and press send.",
    "Ask by voice" to "Tap the microphone in the input bar on Home, allow microphone access, then speak in English or বাংলা (choose the language in the Persona tab). The mic needs an internet connection.",
    "Add a task by voice or text" to "Say or type: \"add task buy groceries\" or \"নতুন টাস্ক যোগ করো ...\". You can also add tasks in the Tools tab.",
    "Chat tab" to "Talk to the AI assistant. Switch on Deep thinking for longer, more careful answers.",
    "Tools tab" to "Manage Tasks, write Notes and record Income / Expenses. All of this is stored only on your phone.",
    "Persona tab" to "Change the assistant name, role, tone and response language, your own name, and the city used for the weather."
)

/** Returns the newest release number from GitHub (tag like v1.0.12 -> 12), or null if it cannot be read. */
private suspend fun latestReleaseNumber(repo: String): Int? = withContext(Dispatchers.IO) {
    try {
        val request = Request.Builder()
            .url("https://api.github.com/repos/$repo/releases/latest")
            .header("Accept", "application/vnd.github+json")
            .build()
        OkHttpClient().newCall(request).execute().use { r ->
            if (!r.isSuccessful) return@withContext null
            val tag = JSONObject(r.body?.string().orEmpty()).optString("tag_name")
            tag.substringAfterLast('.').toIntOrNull()
        }
    } catch (_: Exception) {
        null
    }
}

@Composable
fun AboutSection(modifier: Modifier = Modifier) {
    var showGuide by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    var updateStatus by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .cyberCard(cornerRadius = 16.dp, borderColor = NeonCyan, backgroundColor = CyberSurfaceGlass)
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.life_easy_logo),
                contentDescription = "LIFE EASY logo",
                modifier = Modifier.size(84.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = AppInfo.BRAND,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 3.sp
            )
            Text(text = "Version ${BuildConfig.VERSION_NAME}", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Developed by", fontSize = 10.sp, color = TextSecondary)
            Text(
                text = AppInfo.DEVELOPER,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = if (showGuide) "Hide guide ▲" else "How to use ▼",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple,
                    modifier = Modifier
                        .clickable { showGuide = !showGuide }
                        .padding(8.dp)
                )
                if (AppInfo.PRIVACY_POLICY_URL.isNotBlank()) {
                    Text(
                        text = "Privacy policy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier
                            .clickable { uriHandler.openUri(AppInfo.PRIVACY_POLICY_URL) }
                            .padding(8.dp)
                    )
                }
            }

            if (BuildConfig.UPDATE_REPO.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Check for update",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier
                        .clickable {
                            updateStatus = "Checking..."
                            scope.launch {
                                val latest = latestReleaseNumber(BuildConfig.UPDATE_REPO)
                                if (latest == null) {
                                    updateStatus = "Could not check. Please try again later."
                                } else if (latest > BuildConfig.VERSION_CODE) {
                                    updateStatus = "New version available. Opening download..."
                                    uriHandler.openUri("https://github.com/${BuildConfig.UPDATE_REPO}/releases/latest/download/LIFE-EASY.apk")
                                } else {
                                    updateStatus = "You are using the latest version."
                                }
                            }
                        }
                        .padding(8.dp)
                )
                if (updateStatus.isNotEmpty()) {
                    Text(text = updateStatus, fontSize = 11.sp, color = TextSecondary)
                }
            }

            if (showGuide) {
                Spacer(modifier = Modifier.height(6.dp))
                howToSteps.forEachIndexed { index, (title, text) ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                        Text(
                            text = "${index + 1}. $title",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(text = text, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
