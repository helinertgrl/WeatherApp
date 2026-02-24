package com.example.weatherapp.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.presentation.weather.WeatherViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WeatherRepository.init(applicationContext)
        setContent {
            WeatherAppTheme {
                val viewModel: WeatherViewModel = viewModel()
                MainScreen(weatherViewModel = viewModel)
            }
        }
    }
}