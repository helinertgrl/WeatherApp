package com.example.weatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.data.model.WeatherResponse

@Composable
fun EmptyStateView(textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "❤\uFE0F", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Henüz favori şehir eklemedin.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ana sayfadaki Kalp ikonuna tıklayarak\nlisteni oluşturabilirsin.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = textColor.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun WideCityCard(city: WeatherResponse, onClick: () -> Unit) {
    val description = city.weather.firstOrNull()?.description?.lowercase() ?: ""
    val iconCode = city.weather.firstOrNull()?.icon ?: ""
    val isNight = iconCode.endsWith("n")

    val isRainy = description.contains("rain") || description.contains("yağmur")
            || description.contains("drizzle") || description.contains("sağanak")
    val isSnowy = description.contains("snow") || description.contains("kar")
    val isCloudy = description.contains("cloud") || description.contains("bulut")
            || description.contains("kapalı") || description.contains("parçalı")
            || description.contains("sis") || description.contains("pus") || description.contains("duman")
    val isClear = description.contains("clear") || description.contains("sunny")
            || description.contains("açık") || description.contains("güneş")

    val gradientColors = when {
        isNight -> listOf(Color(0xFF2C3E50), Color(0xFF3F51B5))
        isClear -> listOf(Color(0xFFFF8008), Color(0xFFFFC107))
        isRainy -> listOf(Color(0xFF4568DC), Color(0xFFB06AB3))
        isSnowy -> listOf(Color(0xFF83a4d4), Color(0xFFE1F5FE))
        else -> listOf(Color(0xFF606c88), Color(0xFF90A4AE))
    }

    val lottieRes = when {
        isClear && isNight -> R.raw.weather_night
        isClear -> R.raw.weather_sunny
        isRainy -> R.raw.weather_rainy
        isSnowy -> R.raw.weather_snow_night
        isCloudy -> R.raw.weather_cloudy
        else -> if (isNight) R.raw.weather_night else R.raw.weather_cloudy
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp, y = 0.dp)
                    .size(160.dp)
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = city.name ?: "Bilinmiyor",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = (city.weather.firstOrNull()?.description ?: "").replaceFirstChar { it.uppercase() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Text(
                    text = "${city.main.temp.toInt()}°",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}