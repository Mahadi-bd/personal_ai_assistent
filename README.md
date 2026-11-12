<div align="center">
  <img src="app/src/main/res/drawable-nodpi/life_easy_logo.png" width="140" alt="LIFE EASY logo"/>

# LIFE EASY

AI assistant, tasks, notes and weather in one Android app – English & বাংলা.

**Developed by MAHADI HASAN JEWEL**
</div>

## Features
- AI chat (Google Gemini) with optional deep-thinking mode
- Real voice input (English / Bangla) using Android speech recognition
- Tasks, notes, income/expense list – stored locally with Room, no account
- Live weather by city (Open-Meteo, no key)
- Persona settings: assistant name, role, tone, response language

## Tech
Kotlin · Jetpack Compose · Room · OkHttp · Gemini REST API · Open-Meteo

## Build
1. Open in Android Studio (JDK 17+ / 21).
2. Copy `.env.example` to `.env` and set `GEMINI_API_KEY`.
3. Run on a device, or use the GitHub Actions workflows in `.github/workflows`.

Release/Play Store steps: see [`docs/GITHUB_AND_PLAY_STORE_GUIDE.md`](docs/GITHUB_AND_PLAY_STORE_GUIDE.md).
Privacy policy: [`docs/privacy-policy.md`](docs/privacy-policy.md).

© MAHADI HASAN JEWEL
