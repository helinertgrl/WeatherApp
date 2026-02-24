package com.example.weatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weatherapp.presentation.navigation.Screen

@Composable
fun FloatingBottomBar(
    navController: NavController,
    isNight: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val containerColor = if (isNight) Color(0xFF1A237E).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f)
    val contentColor = if (isNight) Color.White else Color.Black
    val indicatorColor = if (isNight) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f)

    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomNavItem(
                isSelected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Weather>() } == true,
                onClick = {
                    navController.navigate(Screen.Weather) {
                        popUpTo(Screen.Weather) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = if (currentDestination?.hierarchy?.any { it.hasRoute<Screen.Weather>() } == true) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Hava",
                selectedColor = contentColor,
                unselectedColor = contentColor.copy(alpha = 0.4f),
                indicatorColor = indicatorColor
            )

            CustomNavItem(
                isSelected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Favorites>() } == true,
                onClick = {
                    navController.navigate(Screen.Favorites) {
                        popUpTo(Screen.Weather) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = if (currentDestination?.hierarchy?.any { it.hasRoute<Screen.Favorites>() } == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                label = "Favoriler",
                selectedColor = contentColor,
                unselectedColor = contentColor.copy(alpha = 0.4f),
                indicatorColor = indicatorColor
            )

            CustomNavItem(
                isSelected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Activity>() } == true,
                onClick = {
                    navController.navigate(Screen.Activity) {
                        popUpTo(Screen.Weather) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = if (currentDestination?.hierarchy?.any { it.hasRoute<Screen.Activity>() } == true) Icons.Filled.Event else Icons.Outlined.Event,
                label = "Öneri",
                selectedColor = contentColor,
                unselectedColor = contentColor.copy(alpha = 0.4f),
                indicatorColor = indicatorColor
            )
        }
    }
}

@Composable
fun CustomNavItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    selectedColor: Color,
    unselectedColor: Color,
    indicatorColor: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) indicatorColor else Color.Transparent)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if(isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(28.dp)
        )
    }
}