package com.example.weatherapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.remote.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_app_prefs")

object WeatherRepository {
    private val apiService = RetrofitClient.apiService.value
    private const val API_KEY = "96625d4a2c647965c7520d944c6a8c61"

    private val FAVORITE_CITIES_KEY = stringPreferencesKey("favorite_cities_json")
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _favoriteCities = mutableStateListOf<WeatherResponse>()
    val favoriteCities: List<WeatherResponse> = _favoriteCities

    private var dataStore: DataStore<Preferences>? = null

    fun init(context: Context) {
        dataStore = context.dataStore

        scope.launch {
            dataStore?.data?.map { preferences ->
                preferences[FAVORITE_CITIES_KEY] ?: ""
            }?.collect { jsonString ->
                if (jsonString.isNotEmpty()) {
                    try {
                        val type = object : TypeToken<List<WeatherResponse>>() {}.type
                        val savedList: List<WeatherResponse> = gson.fromJson(jsonString, type)

                        withContext(Dispatchers.Main) {
                            _favoriteCities.clear()
                            _favoriteCities.addAll(savedList)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    suspend fun getCurrentWeather(city: String): WeatherResponse {
        return apiService.getCurrentWeather(city, API_KEY)
    }

    suspend fun getWeatherByCoords(lat: Double, lon: Double): WeatherResponse {
        return apiService.getWeatherByCoords(lat, lon, API_KEY)
    }

    suspend fun getFiveDayForecast(city: String): ForecastResponse {
        return apiService.getFiveDayForecast(city, API_KEY)
    }

    fun toggleFavorite(weather: WeatherResponse) {
        val existing = _favoriteCities.find { it.name == weather.name }
        if (existing != null) {
            _favoriteCities.remove(existing)
        } else {
            _favoriteCities.add(weather)
        }

        val listToSave = ArrayList(_favoriteCities)
        saveFavoritesToDataStore(listToSave)
    }

    fun isFavorite(cityName: String?): Boolean {
        return _favoriteCities.any { it.name == cityName }
    }

    private fun saveFavoritesToDataStore(list: List<WeatherResponse>) {
        scope.launch {
            try {
                val jsonString = gson.toJson(list)

                dataStore?.edit { preferences ->
                    preferences[FAVORITE_CITIES_KEY] = jsonString
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}