package com.example.weatherapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.util.NetworkUtil
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeatherViewModel : ViewModel() {
    var weatherData by mutableStateOf<WeatherResponse?>(null)
        private set

    var forecastData by mutableStateOf<List<ForecastItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val API_KEY = "96625d4a2c647965c7520d944c6a8c61"

    fun fetchWeather(city: String, context: android.content.Context) {
        if (!NetworkUtil.isInternetAvailable(context)) {
            errorMessage = "İnternet bağlantısı yok."
            return
        }

        viewModelScope.launch {
            startLoading()
            try {
                val response = RetrofitClient.apiService.value.getCurrentWeather(city, API_KEY)
                weatherData = response
                fetchForecast(city)
                errorMessage = null
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double, context: android.content.Context) {
        if (!NetworkUtil.isInternetAvailable(context)) {
            errorMessage = "İnternet bağlantısı yok."
            return
        }

        viewModelScope.launch {
            startLoading()
            try {
                val response = RetrofitClient.apiService.value.getWeatherByCoords(lat, lon, API_KEY)
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

    private suspend fun fetchForecast(city: String) {
        try {
            val response = RetrofitClient.apiService.value.getFiveDayForecast(city, API_KEY)
            forecastData = response.list.filter { it.dtTxt.contains("12:00:00") }
        } catch (e: Exception) {
            forecastData = emptyList()
        }
    }

    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

    private fun handleError(e: Exception) {
        errorMessage = when (e) {
            is HttpException -> when (e.code()) {
                404 -> "Şehir bulunamadı. Lütfen ismi kontrol edin."
                401 -> "API Anahtarı geçersiz. Lütfen servisi kontrol edin."
                else -> "Sunucu hatası: ${e.code()}"
            }
            else -> "İnternet bağlantınızı kontrol edin."
        }
    }
}