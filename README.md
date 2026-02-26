<div align="center">

<!-- HEADER BLOCK -->
<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=12,20,30&height=200&section=header&text=☁️%20WeatherApp&fontSize=60&fontColor=ffffff&animation=fadeIn&fontAlignY=55&desc=Real-time%20weather%20at%20your%20fingertips&descAlignY=78&descSize=18&descColor=cce0ff" width="100%"/>

<br/>

<!-- BADGES -->
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-Passing-brightgreen?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

> **A beautifully crafted Android weather application** built with 100% Kotlin & Jetpack Compose.  
> Featuring live weather data, smart GPS detection, adaptive theming, animated forecasts, and personalized activity suggestions — all wrapped in a sleek, modern UI.

</div>

---

## 📸 Screenshots

<div align="center">

| 🌤️ Daytime Weather | 🌙 Night / Cloudy | ❤️ Favorites | 🎯 Daily Suggestion |
|:---:|:---:|:---:|:---:|
| <img src="screenshots/weather_day.png" width="180"/> | <img src="screenshots/weather_night.png" width="180"/> | <img src="screenshots/favorites.png" width="180"/> | <img src="screenshots/activity.png" width="180"/> |
| Dynamic gradient UI adapts to current conditions | Night mode with deep navy gradient | Saved cities with weather-colored cards | Smart activity recommendations |

</div>

> 💡 **UI dynamically adapts** — backgrounds, text colors, and gradients all shift based on current weather conditions and day/night state.

---

## ✨ Features

<details open>
<summary><b>🌍 Live Weather</b></summary>

- Real-time weather data via **OpenWeatherMap API**
- Search any city worldwide
- Auto-detect location using device **GPS**
- Displays temperature, humidity, wind speed, and "feels like" temperature
- Full **Turkish language** support for weather descriptions

</details>

<details open>
<summary><b>📅 Forecasts</b></summary>

- **Hourly forecast** — next 24 hours in 3-hour intervals
- **5-day forecast** — daily overview at 12:00 UTC
- Animated weather icons via **Lottie**

</details>

<details open>
<summary><b>❤️ Favorites</b></summary>

- Save unlimited cities to your favorites list
- One-tap to load any saved city's weather
- Persisted locally using **DataStore Preferences** — survives app restarts
- Rich gradient cards colored by each city's current weather

</details>

<details open>
<summary><b>🎯 Activity Suggestions</b></summary>

- Smart suggestions based on current weather conditions
- Covers rain, snow, clear skies, night, and overcast scenarios
- Each suggestion accompanied by a Lottie animation

</details>

<details open>
<summary><b>🎨 Adaptive UI</b></summary>

- Animated color transitions between weather states
- Day/Night detection via icon code (`d` / `n`)
- Floating pill-shaped bottom navigation bar
- Full edge-to-edge display support

</details>

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|---|---|---|
| **Language** | Kotlin | 2.0.21 |
| **UI Toolkit** | Jetpack Compose | BOM Latest |
| **Architecture** | MVVM + Repository Pattern | — |
| **Networking** | Retrofit 2 | 2.11.0 |
| **HTTP Client** | OkHttp + Logging Interceptor | 4.12.0 |
| **JSON Parsing** | Gson Converter | 2.11.0 |
| **Navigation** | Navigation Compose | 2.8.5 |
| **Serialization** | Kotlinx Serialization | 1.7.3 |
| **Animations** | Lottie Compose | 6.4.0 |
| **Persistence** | DataStore Preferences | 1.1.1 |
| **Location** | Google Play Services Location | 21.3.0 |
| **Icons** | Material Icons Extended | 1.7.0 |
| **ViewModel** | Lifecycle ViewModel Compose | 2.8.0 |
| **Build System** | Gradle (Kotlin DSL) | — |
| **Min SDK** | Android 7.0 Nougat | API 24 |
| **Target SDK** | API 36 | — |
| **CI/CD** | GitHub Actions | ✅ Passing |

---

## 🏗️ Architecture

This project follows **MVVM (Model-View-ViewModel)** architecture with a clean separation of concerns across three main layers:

```
com.example.weatherapp
│
├── 📂 data
│   ├── model/          → Data classes (WeatherResponse, ForecastItem, etc.)
│   ├── remote/         → Retrofit API service & client
│   └── repository/     → WeatherRepository (single source of truth)
│
├── 📂 presentation
│   ├── main/           → MainActivity, MainScreen (app entry)
│   ├── navigation/     → NavGraph, Screen sealed interface
│   ├── weather/        → WeatherScreen + WeatherViewModel
│   ├── favorites/      → FavoritesScreen + FavoritesViewModel
│   ├── activity/       → ActivityScreen + ActivityViewModel
│   └── components/     → Shared Composables (cards, bars, search, etc.)
│
└── 📂 util
    ├── NetworkUtil.kt  → Internet availability check
    └── LocationUtil.kt → GPS provider check
```

### 🔄 Data Flow

```
User Action
    │
    ▼
ViewModel (state holder)
    │
    ├──► Repository
    │         │
    │         └──► Retrofit API Call (OpenWeatherMap)
    │                   │
    │                   └──► Response mapped to UI State
    │
    └──► DataStore (Favorites persistence)
              │
              └──► mutableStateListOf (reactive UI update)
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or later
- JDK 17
- A free API key from [OpenWeatherMap](https://openweathermap.org/api)

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/WeatherApp.git
cd WeatherApp
```

**2. Add your API key**

Create or edit `local.properties` in the project root:
```properties
WEATHER_API_KEY=your_openweathermap_api_key_here
```

> ⚠️ **Never commit** `local.properties` to version control. It's already in `.gitignore`.  
> The key is injected at build time via `BuildConfig.WEATHER_API_KEY`.

**3. Build & Run**
```bash
./gradlew assembleDebug
```
Or simply press **▶ Run** in Android Studio.

---

## ⚙️ CI/CD Pipeline

This project uses **GitHub Actions** for automated build verification.

```yaml
Trigger: Push & Pull Request → main branch
  │
  ├── ✅ Checkout repository
  ├── ✅ Set up JDK 17
  ├── ✅ Inject WEATHER_API_KEY from GitHub Secrets
  └── ✅ ./gradlew assembleDebug
```

> 🔐 The API key is stored as a **GitHub Secret** (`WEATHER_API_KEY`) and never exposed in the codebase.

---

## 📂 Project Structure at a Glance

```
app/
├── manifests/
│   └── AndroidManifest.xml          # Permissions: INTERNET, LOCATION
├── kotlin+java/
│   └── com.example.weatherapp/
│       ├── data/
│       │   ├── model/               # WeatherResponse, ForecastItem, MainData…
│       │   ├── remote/              # RetrofitClient, WeatherApiService
│       │   └── repository/          # WeatherRepository (DataStore + API)
│       ├── presentation/
│       │   ├── activity/            # Daily suggestion screen
│       │   ├── components/          # Reusable Composables
│       │   ├── favorites/           # Favorites list screen
│       │   ├── main/                # MainActivity, MainScreen
│       │   ├── navigation/          # NavGraph, Screen routes
│       │   └── weather/             # Main weather screen
│       └── util/
│           ├── LocationUtil.kt
│           └── NetworkUtil.kt
└── res/
    └── raw/                         # Lottie animation JSON files
```

---

## 🎨 UI Theming Logic

The app dynamically selects colors and Lottie animations based on weather conditions:

| Condition | Background | Text Color | Animation |
|---|---|---|---|
| ☀️ Clear (Day) | `#FFF176` → White | Black | `weather_sunny` |
| 🌙 Night | `#020946` → `#5B68CC` | White | `weather_night` |
| 🌧️ Rain / Drizzle | `#4FC3F7` → White | Black | `weather_rainy` |
| ❄️ Snow | `#E1F5FE` → White | Black | `weather_snow_night` |
| ☁️ Cloudy / Fog | `#CFD8DC` → White | Black | `weather_cloudy` |

Color transitions are animated using `animateColorAsState` for smooth visual feedback.

---

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

1. Fork the project
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ and ☕ using **Kotlin** & **Jetpack Compose**

<br/>

*If you found this project useful, consider giving it a ⭐ on GitHub!*

</div>
