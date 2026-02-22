package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("wind") val wind: WindData,
    @SerializedName("dt") val dt: Long
)

data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: CityInfo
)

data class CityInfo(@SerializedName("name") val name: String)

data class MainData(
    val temp: Double,
    val humidity: Int,
    val pressure: Int,
    @SerializedName("feels_like") val feelsLike: Double
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)

data class WindData(val speed: Double)