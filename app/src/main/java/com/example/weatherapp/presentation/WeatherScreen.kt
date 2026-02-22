package com.example.weatherapp.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.model.ForecastItem

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val context = LocalContext.current
    val weatherCondition = viewModel.weatherData?.weather?.get(0)?.main


    val globalTextColor = Color.Black

    val weatherColor = when (weatherCondition) {
        "Sunny", "Clear" -> Color(0xFFFFF176)
        "Cloudy", "Clouds", "Mist", "Fog" -> Color(0xFF90A4AE)
        "Rainy", "Drizzle", "Thunderstorm","Rain" -> Color(0xFF4FC3F7)
        "Snowy", "Snow" -> Color(0xFFE1F5FE)
        else -> Color.White
    }

    val animatedColor by animateColorAsState(targetValue = weatherColor, label = "BG")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(animatedColor, animatedColor.copy(alpha = 0.7f))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            CitynameTitle(
                cityName = viewModel.weatherData?.name ?: "Konum Bekleniyor...",
                tint = globalTextColor
            )

            Searchbar(
                onSearchClick = { query -> viewModel.fetchWeather(query, context) },
                contentColor = globalTextColor
            )

            if (viewModel.errorMessage != null) {
                ErrorMessageCard(viewModel.errorMessage!!)
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp),
                    color = globalTextColor
                )
            } else if (viewModel.weatherData != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainWeatherCard(
                        temp = viewModel.weatherData!!.main.temp.toInt(),
                        condition = weatherCondition ?: "",
                        textColor = globalTextColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WeatherDetailItem(Icons.Default.WaterDrop, "${viewModel.weatherData!!.main.humidity}%", "Nem", globalTextColor)
                        WeatherDetailItem(Icons.Default.Air, "${viewModel.weatherData!!.wind.speed} m/s", "Rüzgar", globalTextColor)
                        WeatherDetailItem(Icons.Default.Thermostat, "${viewModel.weatherData!!.main.feelsLike.toInt()}°", "Hissedilen", globalTextColor)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "5 Günlük Tahmin",
                        color = globalTextColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp).align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.forecastData) { item ->
                            ForecastSmallCard(item, globalTextColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainWeatherCard(temp: Int, condition: String, textColor: Color) {
    val lottieRawRes = when (condition) {
        "Sunny", "Clear" -> R.raw.weather_sunny
        "Cloudy", "Clouds" -> R.raw.weather_cloudy
        "Rainy", "Drizzle", "Thunderstorm" -> R.raw.weather_rainy
        "Snowy", "Snow" -> R.raw.weather_snow_night
        else -> R.raw.weather_sunny
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRawRes))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
        modifier = Modifier.padding(16.dp).fillMaxWidth(0.85f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(composition = composition, progress = { progress }, modifier = Modifier.size(180.dp))
            Text(text = "$temp°", fontSize = 90.sp, fontWeight = FontWeight.Black, color = textColor)
            Text(text = condition, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ForecastSmallCard(item: ForecastItem, tint: Color) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item.dtTxt.substring(5, 10).replace("-", "/"), color = tint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Outlined.Cloud, contentDescription = null, tint = tint, modifier = Modifier.padding(vertical = 8.dp).size(28.dp))
            Text(text = "${item.main.temp.toInt()}°", color = tint, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        Text(text = value, color = tint, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, color = tint.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun Searchbar(onSearchClick: (String) -> Unit, contentColor: Color) {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = { textState = it },
        placeholder = { Text("Şehir Ara...", color = contentColor.copy(alpha = 0.6f)) },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.Black.copy(alpha = 0.1f),
            focusedContainerColor = Color.Black.copy(alpha = 0.1f),
            focusedBorderColor = contentColor,
            unfocusedBorderColor = contentColor.copy(alpha = 0.4f),
            focusedTextColor = contentColor,
            unfocusedTextColor = contentColor
        ),
        trailingIcon = {
            IconButton(onClick = { onSearchClick(textState) }) {
                Icon(Icons.Default.Search, contentDescription = null, tint = contentColor)
            }
        }
    )
}

@Composable
fun CitynameTitle(cityName: String, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = cityName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = tint)
    }
}

@Composable
fun ErrorMessageCard(message: String) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = message, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}