package com.example.weatherapp.presentation.weather

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.util.NetworkUtil
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeatherViewModel : ViewModel() {
    var weatherData by mutableStateOf<WeatherResponse?>(null)
        private set

    var forecastData by mutableStateOf<List<ForecastItem>>(emptyList())
        private set

    var hourlyData by mutableStateOf<List<ForecastItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchWeather(city: String, context: Context) {
        if (!NetworkUtil.isInternetAvailable(context)) {
            errorMessage = "İnternet bağlantısı yok."
            return
        }
        viewModelScope.launch {
            startLoading()
            try {
                val response = WeatherRepository.getCurrentWeather(city)
                weatherData = response
                response.name?.let { fetchForecast(it) }
                errorMessage = null
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double, context: Context) {
        if (!NetworkUtil.isInternetAvailable(context)) {
            errorMessage = "İnternet bağlantısı yok."
            return
        }
        viewModelScope.launch {
            startLoading()
            try {
                val response = WeatherRepository.getWeatherByCoords(lat, lon)
                weatherData = response
                response.name?.let { fetchForecast(it) }
                errorMessage = null
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadCityFromFavorites(weather: WeatherResponse) {
        weatherData = weather
        viewModelScope.launch {
            weather.name?.let { fetchForecast(it) }
        }
    }

    fun toggleFavorite() {
        weatherData?.let { WeatherRepository.toggleFavorite(it) }
    }

    fun isFavorite(name: String?): Boolean {
        return WeatherRepository.isFavorite(name)
    }

    private suspend fun fetchForecast(city: String) {
        try {
            val response = WeatherRepository.getFiveDayForecast(city)
            hourlyData = response.list.take(8)
            forecastData = response.list.filter { it.dtTxt.contains("12:00:00") }
        } catch (e: Exception) {
            forecastData = emptyList()
            hourlyData = emptyList()
        }
    }

    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

    private fun handleError(e: Exception) {
        errorMessage = when (e) {
            is HttpException -> when (e.code()) {
                404 -> "Şehir bulunamadı. İsmi kontrol et."
                401 -> "API Anahtarı hatası."
                else -> "Sunucu hatası: ${e.code()}"
            }
            else -> "Bir hata oluştu: ${e.localizedMessage}"
        }
    }
}