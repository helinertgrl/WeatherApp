package com.example.weatherapp.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.presentation.components.*
import com.example.weatherapp.util.LocationUtil

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val context = LocalContext.current
    val weatherCondition = viewModel.weatherData?.weather?.get(0)?.main
    val globalTextColor = Color.Black

    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && LocationUtil.isLocationEnabled(context)) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { viewModel.fetchWeatherByLocation(it.latitude, it.longitude, context) }
                }
            } catch (e: SecurityException) { /* Handle error */ }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val weatherColor = when (weatherCondition) {
        "Sunny", "Clear" -> Color(0xFFFFF176)
        "Cloudy", "Clouds", "Mist", "Fog" -> Color(0xFF90A4AE)
        "Rainy", "Drizzle", "Thunderstorm", "Rain" -> Color(0xFF4FC3F7)
        "Snowy", "Snow" -> Color(0xFFE1F5FE)
        else -> Color.White
    }

    val animatedColor by animateColorAsState(targetValue = weatherColor, label = "BG")

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(
        colors = listOf(animatedColor, animatedColor.copy(alpha = 0.7f))
    ))) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            CitynameTitle(cityName = viewModel.weatherData?.name ?: "Konum Bekleniyor...", tint = globalTextColor)

            Searchbar(onSearchClick = { query -> viewModel.fetchWeather(query, context) }, contentColor = globalTextColor)

            if (viewModel.errorMessage != null) {
                ErrorMessageCard(viewModel.errorMessage!!)
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp), color = globalTextColor)
            } else if (viewModel.weatherData != null) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    MainWeatherCard(temp = viewModel.weatherData!!.main.temp.toInt(), condition = weatherCondition ?: "", textColor = globalTextColor)

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        WeatherDetailItem(Icons.Default.WaterDrop, "${viewModel.weatherData!!.main.humidity}%", "Nem", globalTextColor)
                        WeatherDetailItem(Icons.Default.Air, "${viewModel.weatherData!!.wind.speed} m/s", "Rüzgar", globalTextColor)
                        WeatherDetailItem(Icons.Default.Thermostat, "${viewModel.weatherData!!.main.feelsLike.toInt()}°", "Hissedilen", globalTextColor)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "5 Günlük Tahmin", color = globalTextColor, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 24.dp).align(Alignment.Start))
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(viewModel.forecastData) { item ->
                            ForecastSmallCard(item, globalTextColor)
                        }
                    }
                }
            }
        }
    }
}