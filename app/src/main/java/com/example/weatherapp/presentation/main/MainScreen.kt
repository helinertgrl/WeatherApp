package com.example.weatherapp.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.presentation.components.FloatingBottomBar
import com.example.weatherapp.presentation.navigation.WeatherNavGraph
import com.example.weatherapp.presentation.weather.WeatherViewModel

@Composable
fun MainScreen(weatherViewModel: WeatherViewModel) {
    val navController = rememberNavController()

    val currentIconCode = weatherViewModel.weatherData?.weather?.get(0)?.icon ?: ""
    val isNight = currentIconCode.endsWith("n")

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)) {

            WeatherNavGraph(
                navController = navController,
                weatherViewModel = weatherViewModel,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                FloatingBottomBar(
                    navController = navController,
                    isNight = isNight
                )
            }
        }
    }
}