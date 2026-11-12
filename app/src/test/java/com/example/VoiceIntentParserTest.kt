package com.example

import com.example.data.ai.ParsedIntent
import com.example.data.ai.VoiceIntentParser
import com.example.data.local.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceIntentParserTest {

    @Test
    fun `test bengali task query intent`() {
        val intent = VoiceIntentParser.parse("আজ আমার কি কি টাস্ক আছে?")
        assertTrue(intent is ParsedIntent.ListTasks)
        assertEquals(true, (intent as ParsedIntent.ListTasks).isBengali)
    }

    @Test
    fun `test english task query intent`() {
        val intent = VoiceIntentParser.parse("What is my schedule today?")
        assertTrue(intent is ParsedIntent.ListTasks)
        assertEquals(false, (intent as ParsedIntent.ListTasks).isBengali)
    }

    @Test
    fun `test bengali task summary response generation`() {
        val sampleTasks = listOf(
            TaskEntity(title = "Team Stand-up", timeString = "09:00", isCompleted = true),
            TaskEntity(title = "Project Review", timeString = "10:30", isCompleted = false)
        )
        val response = VoiceIntentParser.generateTaskSummaryResponse(sampleTasks, isBengali = true)
        assertTrue(response.contains("আজ আপনার মোট 2টি শিডিউল আইটেম আছে"))
        assertTrue(response.contains("Team Stand-up (সম্পন্ন)"))
    }
}
