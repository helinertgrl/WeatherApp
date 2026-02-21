package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

// Anlık Hava Durumu İçin
data class WeatherResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("wind") val wind: WindData,
    @SerializedName("dt") val dt: Long // Güncelleme zamanı için
)

// 5 Günlük Tahmin Listesi İçin
data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: CityInfo
)

data class CityInfo(
    @SerializedName("name") val name: String
)

data class MainData(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("feels_like") val feelsLike: Double
)

data class WeatherDescription(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class WindData(
    @SerializedName("speed") val speed: Double
)

data class ForecastItem(
    @SerializedName("dt_txt") val dtTxt: String,
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherDescription>
)