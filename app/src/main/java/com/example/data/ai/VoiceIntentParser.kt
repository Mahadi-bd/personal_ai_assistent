package com.example.data.ai

import com.example.data.local.TaskEntity

sealed interface ParsedIntent {
    data class ListTasks(val isBengali: Boolean) : ParsedIntent
    data class AddTask(val title: String, val time: String) : ParsedIntent
    data class CreateNote(val title: String, val content: String) : ParsedIntent
    data object ShowWeather : ParsedIntent
    data object ShowScore : ParsedIntent
    data class GeneralAiQuery(val query: String) : ParsedIntent
}

object VoiceIntentParser {

    fun parse(input: String): ParsedIntent {
        val clean = input.trim().lowercase()

        // Bengali Task Queries
        if (clean.contains("টাস্ক") || clean.contains("কাজ") || clean.contains("শিডিউল") || clean.contains("আজ আমার")) {
            if (clean.contains("যোগ") || clean.contains("নতুন") || clean.contains("তৈরি")) {
                val title = clean.replace("নতুন", "").replace("টাস্ক", "").replace("যোগ করো", "").trim()
                return ParsedIntent.AddTask(title.ifEmpty { "New Task" }, "15:00")
            }
            return ParsedIntent.ListTasks(isBengali = true)
        }

        // English Task Queries
        if (clean.contains("task") || clean.contains("schedule") || clean.contains("agenda") || clean.contains("what do i have")) {
            if (clean.contains("add") || clean.contains("create") || clean.contains("new task")) {
                val title = clean.replace("add task", "").replace("create task", "").trim()
                return ParsedIntent.AddTask(title.ifEmpty { "New Task" }, "15:00")
            }
            return ParsedIntent.ListTasks(isBengali = false)
        }

        // Weather Queries
        if (clean.contains("weather") || clean.contains("আবহাওয়া") || clean.contains("বৃষ্টি") || clean.contains("তাপমাত্রা")) {
            return ParsedIntent.ShowWeather
        }

        // Productivity / Score
        if (clean.contains("score") || clean.contains("productivity") || clean.contains("স্কোর") || clean.contains("পারফরম্যান্স")) {
            return ParsedIntent.ShowScore
        }

        return ParsedIntent.GeneralAiQuery(input)
    }

    fun generateTaskSummaryResponse(tasks: List<TaskEntity>, isBengali: Boolean): String {
        val pending = tasks.filter { !it.isCompleted }
        val completed = tasks.filter { it.isCompleted }

        return if (isBengali) {
            val taskListText = tasks.joinToString(", ") { "${it.timeString} - ${it.title}${if (it.isCompleted) " (সম্পন্ন)" else ""}" }
            "আজ আপনার মোট ${tasks.size}টি শিডিউল আইটেম আছে (${completed.size}টি সম্পন্ন এবং ${pending.size}টি বাকি)। সময়সূচী: $taskListText।"
        } else {
            val taskListText = tasks.joinToString(", ") { "${it.timeString} - ${it.title}${if (it.isCompleted) " (done)" else ""}" }
            "Here's your agenda for today. You have ${tasks.size} items scheduled (${completed.size} completed, ${pending.size} pending): $taskListText."
        }
    }
}
