package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: CityInfo
)

data class CityInfo(
    @SerializedName("name") val name: String
)

data class ForecastItem(
    @SerializedName("dt_txt") val dtTxt: String,
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherDescription>
)