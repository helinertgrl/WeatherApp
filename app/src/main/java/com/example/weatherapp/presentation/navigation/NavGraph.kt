package com.example.weatherapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.presentation.activity.ActivityScreen
import com.example.weatherapp.presentation.favorites.FavoritesScreen
import com.example.weatherapp.presentation.weather.WeatherScreen
import com.example.weatherapp.presentation.weather.WeatherViewModel

@Composable
fun WeatherNavGraph(
    navController: NavHostController,
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val currentIconCode = weatherViewModel.weatherData?.weather?.get(0)?.icon ?: ""
    val currentDesc = weatherViewModel.weatherData?.weather?.get(0)?.description ?: ""

    NavHost(
        navController = navController,
        startDestination = Screen.Weather,
        modifier = modifier
    ) {
        composable<Screen.Weather> {
            WeatherScreen(viewModel = weatherViewModel)
        }

        composable<Screen.Activity> {
            ActivityScreen(
                currentWeatherDesc = currentDesc,
                currentIconCode = currentIconCode
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen(
                currentWeatherDesc = currentDesc,
                currentIconCode = currentIconCode,
                onCityClick = { selectedCity ->
                    weatherViewModel.loadCityFromFavorites(selectedCity)
                    navController.navigate(Screen.Weather) {
                        popUpTo(Screen.Weather) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}