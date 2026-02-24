package com.example.weatherapp.presentation.activity

import androidx.lifecycle.ViewModel
import com.example.weatherapp.R

class ActivityViewModel : ViewModel() {

    data class Suggestion(val title: String, val desc: String, val animRes: Int)

    fun getSuggestion(description: String, isNight: Boolean): Suggestion {
        val isRainy = description.contains("rain") || description.contains("yağmur")
                || description.contains("drizzle")
        val isSnowy = description.contains("snow") || description.contains("kar")
        val isClear = description.contains("clear") || description.contains("sunny")
                || description.contains("açık") || description.contains("güneş")

        return when {
            isRainy -> Suggestion(
                "Kahve & Kitap Modu",
                "Dışarısı ıslak! En sevdiğin kitabını al, sıcak bir kahve yap ve yağmur sesinin tadını çıkar.",
                R.raw.weather_rainy
            )
            isSnowy -> Suggestion(
                "Sıcak Çikolata Zamanı",
                "Hava buz gibi. Battaniyenin altına girip film izlemek veya sıcak çikolata içmek için harika bir an.",
                R.raw.weather_snow_night
            )
            isClear && isNight -> Suggestion(
                "Yıldızları İzle",
                "Gökyüzü tertemiz. Şehirden uzaklaşıp yıldızları izlemek veya gece yürüyüşü yapmak ruhuna iyi gelecektir.",
                R.raw.weather_night
            )
            isClear && !isNight -> Suggestion(
                "Dışarı Çık & Keşfet",
                "Hava harika! Parkta yürüyüşe çıkmak, bisiklet sürmek veya fotoğraf çekmek için mükemmel bir gün.",
                R.raw.weather_sunny
            )
            else -> Suggestion(
                "Kendine Vakit Ayır",
                "Hava biraz kapalı veya sisli olabilir. Kapalı mekan aktiviteleri, sinema veya spor salonu iyi bir seçenek.",
                R.raw.weather_cloudy
            )
        }
    }
}