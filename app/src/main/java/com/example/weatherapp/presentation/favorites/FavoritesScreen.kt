package com.example.weatherapp.presentation.favorites

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.presentation.components.EmptyStateView
import com.example.weatherapp.presentation.components.WideCityCard

@Composable
fun FavoritesScreen(
    currentWeatherDesc: String,
    currentIconCode: String,
    onCityClick: (WeatherResponse) -> Unit,
    viewModel: FavoritesViewModel = viewModel()
) {
    val isMainNight = currentIconCode.endsWith("n")

    val isMainRainy = currentWeatherDesc.contains("rain") || currentWeatherDesc.contains("yağmur")
            || currentWeatherDesc.contains("drizzle") || currentWeatherDesc.contains("sağanak")
    val isMainSnowy = currentWeatherDesc.contains("snow") || currentWeatherDesc.contains("kar")
    val isMainCloudy = currentWeatherDesc.contains("cloud") || currentWeatherDesc.contains("bulut")
            || currentWeatherDesc.contains("kapalı") || currentWeatherDesc.contains("parçalı")
            || currentWeatherDesc.contains("sis") || currentWeatherDesc.contains("pus")
            || currentWeatherDesc.contains("duman")
    val isMainClear = currentWeatherDesc.contains("clear") || currentWeatherDesc.contains("sunny")
            || currentWeatherDesc.contains("açık") || currentWeatherDesc.contains("güneş")

    val targetColors = when {
        isMainNight -> Pair(Color(0xFF0F172A), Color(0xFF1E293B))
        isMainRainy -> Pair(Color(0xFF4FC3F7), Color(0xFFFFFFFF))
        isMainSnowy -> Pair(Color(0xFFE1F5FE), Color(0xFFFFFFFF))
        isMainCloudy -> Pair(Color(0xFFCFD8DC), Color(0xFFECEFF1))
        isMainClear -> Pair(Color(0xFFFFF176), Color(0xFFFFFFFF))
        else -> Pair(Color(0xFFCFD8DC), Color(0xFFECEFF1))
    }

    val animatedBgStart by animateColorAsState(targetColors.first,
        animationSpec = tween(1000),
        label = "bgStart")
    val animatedBgEnd by animateColorAsState(targetColors.second,
        animationSpec = tween(1000),
        label = "bgEnd")
    val titleColor = if (isMainNight) Color.White else Color(0xFF1E293B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(animatedBgStart, animatedBgEnd)))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Favoriler",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = titleColor,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${viewModel.favoriteCities.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.favoriteCities.isEmpty()) {
                EmptyStateView(titleColor)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(viewModel.favoriteCities) { city ->
                        WideCityCard(
                            city = city,
                            onClick = { onCityClick(city) }
                        )
                    }
                }
            }
        }
    }
}