package com.example.weatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.WeatherResponse

@Composable
fun MainWeatherCard(temp: Int, condition: String, iconCode: String, textColor: Color) {

    val isNight = iconCode.endsWith("n")
    val desc = condition.lowercase()

    val isRainy = desc.contains("rain") || desc.contains("yağmur")
            || desc.contains("drizzle") || desc.contains("sağanak")
    val isSnowy = desc.contains("snow") || desc.contains("kar")
    val isCloudy = desc.contains("cloud") || desc.contains("bulut")
            || desc.contains("kapalı") || desc.contains("parçalı")
            || desc.contains("mist") || desc.contains("fog") || desc.contains("sis")
    val isClear = desc.contains("clear") || desc.contains("sunny")
            || desc.contains("açık") || desc.contains("güneş")

    val lottieRawRes = when {
        isClear && isNight -> R.raw.weather_night
        isClear -> R.raw.weather_sunny

        isCloudy -> R.raw.weather_cloudy

        isRainy -> R.raw.weather_rainy
        isSnowy -> R.raw.weather_snow_night

        else -> if (isNight) R.raw.weather_night else R.raw.weather_sunny
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRawRes))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f)),
        modifier = Modifier.padding(16.dp).fillMaxWidth(0.85f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(composition = composition, progress = { progress }, modifier = Modifier.size(180.dp))
            Text(text = "$temp°", fontSize = 90.sp, fontWeight = FontWeight.Black, color = textColor)
            Text(
                text = condition.replaceFirstChar { it.uppercase() },
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ForecastSmallCard(item: ForecastItem, tint: Color, isHourly: Boolean) {
    val displayTime = if (isHourly) {
        item.dtTxt.substring(11, 16)
    } else {
        item.dtTxt.substring(5, 10).replace("-", "/")
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = displayTime, color = tint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Outlined.Cloud, contentDescription = null,
                tint = tint,
                modifier = Modifier.padding(vertical = 8.dp).size(28.dp))
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
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.Black.copy(alpha = 0.1f),
            focusedContainerColor = Color.Black.copy(alpha = 0.15f),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
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
        Icon(Icons.Filled.LocationOn,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp))
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

@Composable
fun TopSearchBar(
    globalTextColor: Color,
    weatherData: WeatherResponse?,
    isFavorite: Boolean,
    onSearch: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Searchbar(
                onSearchClick = onSearch,
                contentColor = globalTextColor
            )
        }
        if (weatherData != null) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(50.dp)
                    .background(Color.Black.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Fav",
                    tint = if (isFavorite) Color.Red else globalTextColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun WeatherDisplayContent(
    weatherData: WeatherResponse,
    hourlyData: List<ForecastItem>,
    forecastData: List<ForecastItem>,
    globalTextColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CitynameTitle(cityName = weatherData.name ?: "", tint = globalTextColor)

        MainWeatherCard(
            temp = weatherData.main.temp.toInt(),
            condition = weatherData.weather[0].description,
            iconCode = weatherData.weather[0].icon,
            textColor = globalTextColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(Icons.Default.WaterDrop, "${weatherData.main.humidity}%", "Nem", globalTextColor)
            WeatherDetailItem(Icons.Default.Air, "${weatherData.wind.speed} m/s", "Rüzgar", globalTextColor)
            WeatherDetailItem(Icons.Default.Thermostat, "${weatherData.main.feelsLike.toInt()}°", "Hissedilen", globalTextColor)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Bugün (3 Saatlik)", color = globalTextColor, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 24.dp).align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(hourlyData) { item -> ForecastSmallCard(item, globalTextColor, isHourly = true) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("5 Günlük Tahmin", color = globalTextColor, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 24.dp).align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(forecastData) { item -> ForecastSmallCard(item, globalTextColor, isHourly = false) }
        }
    }
}