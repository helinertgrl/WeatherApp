package com.example.weatherapp.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.model.LocationUtil

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && LocationUtil.isLocationEnabled(context)) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { viewModel.fetchWeatherByLocation(it.latitude, it.longitude) }
                }
            } catch (e: SecurityException) { /* Handle error */ }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val weatherCondition = viewModel.weatherData?.weather?.get(0)?.main
    val weatherColor = when (weatherCondition) {
        "Sunny", "Clear" -> Color(0xFFFFB300)
        "Cloudy", "Clouds", "Mist", "Fog" -> Color(0xFF90A4AE)
        "Rainy", "Drizzle", "Thunderstorm" -> Color(0xFF4FC3F7)
        "Snowy", "Snow" -> Color(0xFFE1F5FE)
        else -> Color(0xFF64B5F6)
    }

    val animatedColor by animateColorAsState(targetValue = weatherColor, label = "ColorAnim")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(animatedColor, animatedColor.copy(alpha = 0.6f))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            CitynameTitle(cityName = viewModel.weatherData?.name ?: "Şehir Bekleniyor...")

            Searchbar(onSearchClick = { query -> viewModel.fetchWeather(query) })

            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 50.dp),
                    color = Color.White
                )
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                )
            } else if (viewModel.weatherData != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainWeatherCard(
                        temp = viewModel.weatherData!!.main.temp.toInt(),
                        condition = weatherCondition ?: ""
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detay Kartları (Nem, Rüzgar, Hissedilen)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WeatherDetailItem(Icons.Default.WaterDrop, "${viewModel.weatherData!!.main.humidity}%", "Nem")
                        WeatherDetailItem(Icons.Default.Air, "${viewModel.weatherData!!.wind.speed} km/h", "Rüzgar")
                        WeatherDetailItem(Icons.Default.Thermostat, "${viewModel.weatherData!!.main.feelsLike.toInt()}°", "Hissedilen")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 5 Günlük Tahmin Başlığı
                    Text(
                        text = "5 Günlük Tahmin",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp).align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tahmin Listesi
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.forecastData) { item ->
                            ForecastSmallCard(item)
                        }
                    }
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun MainWeatherCard(temp: Int, condition: String) {
    val animatedTemp by animateIntAsState(targetValue = temp, label = "TempAnim")

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.padding(16.dp).fillMaxWidth(0.85f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$animatedTemp°", fontSize = 90.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = condition, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun ForecastSmallCard(item: ForecastItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item.dtTxt.substring(5, 10), color = Color.White, fontSize = 12.sp)
            Icon(Icons.Outlined.Cloud, contentDescription = null, tint = Color.White, modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "${item.main.temp.toInt()}°", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Searchbar(onSearchClick: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = { textState = it },
        placeholder = { Text("Şehir Ara...", color = Color.White.copy(alpha = 0.6f)) },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
            focusedContainerColor = Color.White.copy(alpha = 0.2f),
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        trailingIcon = {
            IconButton(onClick = { onSearchClick(textState) }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }
        }
    )
}

@Composable
fun CitynameTitle(cityName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = cityName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}