package com.example.data.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class WeatherDay(val label: String, val maxTempC: Int, val code: Int)

data class WeatherInfo(
    val city: String,
    val tempC: Int,
    val code: Int,
    val days: List<WeatherDay>
)

/** Live weather from Open-Meteo (https://open-meteo.com, no API key needed). */
class WeatherService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun get(url: String): String? {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { r ->
            return if (r.isSuccessful) r.body?.string() else null
        }
    }

    suspend fun fetch(city: String): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val q = URLEncoder.encode(city, "UTF-8")
            val geo = get("https://geocoding-api.open-meteo.com/v1/search?name=$q&count=1") ?: return@withContext null
            val place = JSONObject(geo).optJSONArray("results")?.optJSONObject(0) ?: return@withContext null
            val lat = place.getDouble("latitude")
            val lon = place.getDouble("longitude")
            val name = place.optString("name", city)

            val forecast = get(
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
                        "&current=temperature_2m,weather_code" +
                        "&daily=weather_code,temperature_2m_max&timezone=auto&forecast_days=5"
            ) ?: return@withContext null

            val root = JSONObject(forecast)
            val current = root.getJSONObject("current")
            val daily = root.getJSONObject("daily")
            val dates = daily.getJSONArray("time")
            val maxes = daily.getJSONArray("temperature_2m_max")
            val codes = daily.getJSONArray("weather_code")

            val parse = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val show = SimpleDateFormat("EEE", Locale.getDefault())
            val days = (0 until dates.length()).map { i ->
                val label = try {
                    show.format(parse.parse(dates.getString(i))!!).uppercase()
                } catch (_: Exception) {
                    dates.getString(i).takeLast(5)
                }
                WeatherDay(label, Math.round(maxes.getDouble(i)).toInt(), codes.getInt(i))
            }

            WeatherInfo(
                city = name,
                tempC = Math.round(current.getDouble("temperature_2m")).toInt(),
                code = current.getInt("weather_code"),
                days = days
            )
        } catch (_: Exception) {
            null
        }
    }
}

fun weatherCodeToText(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Fog"
    in 51..57 -> "Drizzle"
    in 61..67 -> "Rain"
    in 71..77 -> "Snow"
    in 80..82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95, 96, 99 -> "Thunderstorm"
    else -> "Cloudy"
}
