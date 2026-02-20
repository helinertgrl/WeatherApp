package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

//API'den gelen Json nesnesi
data class WeatherResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("main") //Sıcaklık ve nem gibi ana veriler
    val main: Main,

    @SerializedName("weather")
    val weather: List<Weather>
)

data class Main(
    @SerializedName("temp")
    val temp: Double
)

data class Weather(
    @SerializedName("main")  //ana durum yani clear, clouds
    val main: String,

    @SerializedName("description") //Detaylı durum yani parçalı bulutlu
    val description: String,

    @SerializedName("icon")
    val icon: String
)

