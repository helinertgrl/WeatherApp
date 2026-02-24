package com.example.weatherapp.presentation.weather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weatherapp.presentation.components.*
import com.example.weatherapp.util.LocationUtil

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val context = LocalContext.current
    val weatherCondition = viewModel.weatherData?.weather?.get(0)?.main
    val iconCode = viewModel.weatherData?.weather?.get(0)?.icon ?: ""
    val isNight = iconCode.endsWith("n")
    val globalTextColor = if (isNight) Color.White else Color.Black

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && LocationUtil.isLocationEnabled(context)) {
            viewModel.fetchWeatherByLocation(41.0082, 28.9784, context)
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.weatherData == null) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val weatherColor = when {
        isNight -> Color(0xFF020946)
        weatherCondition == "Sunny" || weatherCondition == "Clear" -> Color(0xFFFFF176)
        weatherCondition == "Cloudy" || weatherCondition == "Clouds" || weatherCondition == "Mist"
                || weatherCondition == "Fog" -> Color(0xFFCFD8DC)
        weatherCondition == "Rainy" || weatherCondition == "Drizzle" || weatherCondition == "Thunderstorm"
                || weatherCondition == "Rain" -> Color(0xFF4FC3F7)
        weatherCondition == "Snowy" || weatherCondition == "Snow" -> Color(0xFFE1F5FE)
        else -> Color.White
    }

    val endColor = if (isNight) Color(0xFF5B68CC) else Color.White
    val animatedStartColor by animateColorAsState(targetValue = weatherColor, label = "Start")
    val animatedEndColor by animateColorAsState(targetValue = endColor, label = "End")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(animatedStartColor, animatedEndColor)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            TopSearchBar(
                globalTextColor = globalTextColor,
                weatherData = viewModel.weatherData,
                isFavorite = viewModel.isFavorite(viewModel.weatherData?.name),
                onSearch = { query -> viewModel.fetchWeather(query, context) },
                onToggleFavorite = { viewModel.toggleFavorite() }
            )

            if (viewModel.errorMessage != null) {
                ErrorMessageCard(viewModel.errorMessage!!)
            }

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = globalTextColor)
                }
            } else if (viewModel.weatherData != null) {
                WeatherDisplayContent(
                    weatherData = viewModel.weatherData!!,
                    hourlyData = viewModel.hourlyData,
                    forecastData = viewModel.forecastData,
                    globalTextColor = globalTextColor
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}