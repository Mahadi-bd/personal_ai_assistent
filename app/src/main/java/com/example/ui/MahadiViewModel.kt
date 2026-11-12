package com.example.ui

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.ai.ParsedIntent
import com.example.data.ai.VoiceIntentParser
import com.example.data.local.LedgerEntity
import com.example.data.local.MahadiDatabase
import com.example.data.local.SecureNoteEntity
import com.example.data.local.TaskEntity
import com.example.data.repository.MahadiRepository
import com.example.data.weather.WeatherInfo
import com.example.data.weather.WeatherService
import com.example.data.weather.weatherCodeToText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppTab {
    HUD_HOME,
    CHAT,
    TOOLS,
    PERSONA
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: String = "Now",
    val attachmentName: String? = null,
    val isThinking: Boolean = false
)

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val info: WeatherInfo) : WeatherUiState
    data object Error : WeatherUiState
}

class MahadiViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("life_easy_prefs", Context.MODE_PRIVATE)

    private val database = MahadiDatabase.getDatabase(application)
    private val repository = MahadiRepository(
        taskDao = database.taskDao(),
        secureNoteDao = database.secureNoteDao(),
        ledgerDao = database.ledgerDao()
    )
    private val aiService = GeminiAiService(application)
    private val weatherService = WeatherService()

    private val voice = VoiceRecognizer(
        context = application,
        onPartial = { partial -> _listeningQuery.value = partial },
        onResult = { text -> submitVoiceQuery(text, fromVoice = true) },
        onFailure = { message ->
            _isListening.value = false
            _listeningQuery.value = ""
            _hologramSpeech.value = message
        }
    )

    // Navigation
    private val _currentTab = MutableStateFlow(AppTab.HUD_HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Persona & configuration
    private val _assistantName = MutableStateFlow(prefs.getString("assistant_name", "Ava") ?: "Ava")
    val assistantName: StateFlow<String> = _assistantName.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Friend") ?: "Friend")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _personaRole = MutableStateFlow(prefs.getString("persona_role", "Executive Assistant") ?: "Executive Assistant")
    val personaRole: StateFlow<String> = _personaRole.asStateFlow()

    private val _voiceTone = MutableStateFlow(prefs.getString("voice_tone", "Professional") ?: "Professional")
    val voiceTone: StateFlow<String> = _voiceTone.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString("language", "English (US)") ?: "English (US)")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _weatherCity = MutableStateFlow(prefs.getString("weather_city", "Khulna") ?: "Khulna")
    val weatherCity: StateFlow<String> = _weatherCity.asStateFlow()

    // Voice reply style: "female" or "male" (changes the pitch of the phone's text-to-speech voice)
    private val _voiceStyle = MutableStateFlow(prefs.getString("voice_style", "female") ?: "female")
    val voiceStyle: StateFlow<String> = _voiceStyle.asStateFlow()

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var speakNext = false

    // Speech & holographic dialogue
    private val _hologramSpeech = MutableStateFlow("আজ আপনার দিনটি সাফল্যমণ্ডিত হোক!")
    val hologramSpeech: StateFlow<String> = _hologramSpeech.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _listeningQuery = MutableStateFlow("")
    val listeningQuery: StateFlow<String> = _listeningQuery.asStateFlow()

    // High thinking mode toggle
    private val _isThinkingMode = MutableStateFlow(false)
    val isThinkingMode: StateFlow<Boolean> = _isThinkingMode.asStateFlow()

    // Weather
    private val _weather = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weather: StateFlow<WeatherUiState> = _weather.asStateFlow()

    // Database reactive streams
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notes: StateFlow<List<SecureNoteEntity>> = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val ledgerEntries: StateFlow<List<LedgerEntity>> = repository.allLedgerEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /** Percentage (0-100) of tasks marked done. */
    val completionPercent: StateFlow<Int> = tasks
        .map { list -> if (list.isEmpty()) 0 else list.count { it.isCompleted } * 100 / list.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Chat conversation
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                isUser = false,
                text = "Hi! I'm your LIFE EASY assistant. Ask me anything, or tell me to add a task, e.g. \"add task buy groceries\"."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    init {
        refreshWeather()
        tts = TextToSpeech(application) { status -> ttsReady = status == TextToSpeech.SUCCESS }
    }

    fun setVoiceStyle(style: String) {
        _voiceStyle.value = style
        prefs.edit().putString("voice_style", style).apply()
        speak(if (style == "male") "আমি পুরুষ কণ্ঠে কথা বলব।" else "আমি নারী কণ্ঠে কথা বলব।")
    }

    /** Reads text aloud using the phone's own text-to-speech engine. */
    fun speak(text: String) {
        val engine = tts
        if (engine == null || !ttsReady) {
            _hologramSpeech.value = "ভয়েস এখনো প্রস্তুত নয়। ফোনের Text-to-speech সেটিং দেখুন।"
            return
        }
        val bengali = _language.value.contains("Bengali", ignoreCase = true)
        val result = engine.setLanguage(if (bengali) Locale("bn", "BD") else Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            _hologramSpeech.value = "এই ভাষার ভয়েস আপনার ফোনে ইনস্টল করা নেই (Settings → Text-to-speech)।"
            return
        }
        engine.setPitch(if (_voiceStyle.value == "male") 0.75f else 1.15f)
        engine.setSpeechRate(1.0f)
        engine.speak(text.take(500), TextToSpeech.QUEUE_FLUSH, null, "life_easy_tts")
    }

    fun speakCurrent() = speak(_hologramSpeech.value)

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleThinkingMode() {
        _isThinkingMode.value = !_isThinkingMode.value
    }

    fun updatePersona(name: String, role: String, tone: String, lang: String) {
        _assistantName.value = name
        _personaRole.value = role
        _voiceTone.value = tone
        _language.value = lang
        prefs.edit()
            .putString("assistant_name", name)
            .putString("persona_role", role)
            .putString("voice_tone", tone)
            .putString("language", lang)
            .apply()
        _hologramSpeech.value = "Settings updated. Operating as $role with a $tone tone."
    }

    fun updateProfile(userName: String, city: String) {
        val cleanName = userName.trim().ifEmpty { "Friend" }
        _userName.value = cleanName
        prefs.edit().putString("user_name", cleanName).apply()
        val cleanCity = city.trim()
        if (cleanCity.isNotEmpty() && !cleanCity.equals(_weatherCity.value, ignoreCase = true)) {
            _weatherCity.value = cleanCity
            prefs.edit().putString("weather_city", cleanCity).apply()
            refreshWeather()
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _weather.value = WeatherUiState.Loading
            val info = weatherService.fetch(_weatherCity.value)
            _weather.value = if (info != null) WeatherUiState.Success(info) else WeatherUiState.Error
        }
    }

    fun toggleTask(id: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(id, !currentCompleted)
        }
    }

    fun addTask(title: String, timeString: String, duration: Int, category: String) {
        viewModelScope.launch {
            repository.addTask(
                TaskEntity(
                    title = title,
                    timeString = timeString.ifEmpty { "15:00" },
                    durationMinutes = duration,
                    category = category,
                    isCompleted = false
                )
            )
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch { repository.deleteTask(id) }
    }

    fun addNote(title: String, content: String, tag: String) {
        viewModelScope.launch {
            repository.addNote(SecureNoteEntity(title = title, content = content, tag = tag))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { repository.deleteNote(id) }
    }

    fun addLedger(title: String, amount: Double, isExpense: Boolean, category: String) {
        viewModelScope.launch {
            repository.addLedgerEntry(
                LedgerEntity(title = title, amount = amount, isExpense = isExpense, category = category)
            )
        }
    }

    fun deleteLedger(id: Long) {
        viewModelScope.launch { repository.deleteLedgerEntry(id) }
    }

    // ---- Voice input (real microphone) ----

    private fun speechLanguageTag(): String =
        if (_language.value.contains("Bengali", ignoreCase = true)) "bn-BD" else "en-US"

    fun startListening() {
        if (_isListening.value) return
        if (!voice.isAvailable()) {
            _hologramSpeech.value = "Voice recognition is not available on this device."
            return
        }
        _isListening.value = true
        _listeningQuery.value = ""
        voice.start(speechLanguageTag())
    }

    /** User tapped stop: finish recording, the final text arrives through submitVoiceQuery. */
    fun stopListening() {
        voice.stopListening()
    }

    fun cancelListening() {
        voice.destroy()
        _isListening.value = false
        _listeningQuery.value = ""
    }

    fun onMicPermissionDenied() {
        _hologramSpeech.value = "Microphone permission is needed for voice input. You can still type your request."
    }

    override fun onCleared() {
        voice.destroy()
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    // ---- Chat & intents ----

    private fun reply(text: String) {
        _hologramSpeech.value = text
        _chatMessages.value = _chatMessages.value + ChatMessage(isUser = false, text = text)
        if (speakNext) speak(text)
    }

    fun submitVoiceQuery(query: String, fromVoice: Boolean = false) {
        speakNext = fromVoice
        _isListening.value = false
        _listeningQuery.value = ""
        if (query.isBlank()) return

        when (val intent = VoiceIntentParser.parse(query)) {
            is ParsedIntent.ListTasks -> {
                _chatMessages.value = _chatMessages.value + ChatMessage(isUser = true, text = query)
                val current = tasks.value
                if (current.isEmpty()) {
                    reply(
                        if (intent.isBengali) "আপনার কোনো টাস্ক নেই। \"নতুন টাস্ক\" বলে যোগ করতে পারেন।"
                        else "You have no tasks yet. Say \"add task\" followed by the task name to create one."
                    )
                } else {
                    reply(VoiceIntentParser.generateTaskSummaryResponse(current, intent.isBengali))
                }
            }
            is ParsedIntent.AddTask -> {
                _chatMessages.value = _chatMessages.value + ChatMessage(isUser = true, text = query)
                addTask(intent.title, intent.time, 30, "General")
                reply("Task \"${intent.title}\" has been scheduled for ${intent.time}.")
            }
            is ParsedIntent.ShowWeather -> {
                _chatMessages.value = _chatMessages.value + ChatMessage(isUser = true, text = query)
                val state = _weather.value
                reply(
                    if (state is WeatherUiState.Success) {
                        "${state.info.city}: ${state.info.tempC}°C, ${weatherCodeToText(state.info.code)}."
                    } else {
                        "I couldn't load the weather right now. Please check your internet connection."
                    }
                )
            }
            is ParsedIntent.ShowScore -> {
                _chatMessages.value = _chatMessages.value + ChatMessage(isUser = true, text = query)
                val all = tasks.value
                val done = all.count { it.isCompleted }
                reply(
                    if (all.isEmpty()) "No tasks yet, so there is no progress to report."
                    else "You have completed $done of ${all.size} tasks (${completionPercent.value}%)."
                )
            }
            is ParsedIntent.CreateNote, is ParsedIntent.GeneralAiQuery -> sendChatMessage(query, speakReply = fromVoice)
        }
    }

    fun sendChatMessage(text: String, speakReply: Boolean = false) {
        if (text.isBlank() || _isAiGenerating.value) return

        _chatMessages.value = _chatMessages.value + ChatMessage(isUser = true, text = text)
        _isAiGenerating.value = true
        _chatMessages.value = _chatMessages.value + ChatMessage(
            isUser = false,
            text = "Thinking...",
            isThinking = _isThinkingMode.value
        )

        viewModelScope.launch {
            var fullResponse = ""
            try {
                aiService.streamAiResponse(
                    prompt = text,
                    isThinkingMode = _isThinkingMode.value,
                    personaRole = _personaRole.value,
                    voiceTone = _voiceTone.value,
                    targetLanguage = _language.value
                ).collect { partialText ->
                    fullResponse = partialText
                    _chatMessages.value = _chatMessages.value.dropLast(1) + ChatMessage(
                        isUser = false,
                        text = fullResponse,
                        isThinking = _isThinkingMode.value
                    )
                }
            } finally {
                _isAiGenerating.value = false
            }
            _hologramSpeech.value = fullResponse.take(120) + if (fullResponse.length > 120) "..." else ""
            if (speakReply && fullResponse.isNotBlank() && !fullResponse.startsWith("⚠")) speak(fullResponse)
        }
    }
}
