package com.example.weatherapp.presentation.favorites

import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository

class FavoritesViewModel : ViewModel() {
    val favoriteCities: List<WeatherResponse> = WeatherRepository.favoriteCities
}