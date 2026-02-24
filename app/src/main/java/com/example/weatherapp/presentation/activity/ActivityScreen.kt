package com.example.weatherapp.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun ActivityScreen(
    currentWeatherDesc: String,
    currentIconCode: String,
    viewModel: ActivityViewModel = viewModel()
) {
    val description = currentWeatherDesc.lowercase()
    val isNight = currentIconCode.endsWith("n")

    val isRainy = description.contains("rain") || description.contains("yağmur")
            || description.contains("drizzle")
    val isSnowy = description.contains("snow") || description.contains("kar")
    val isCloudy = description.contains("cloud") || description.contains("bulut")
            || description.contains("sis") || description.contains("pus") || description.contains("kapalı")
    val isClear = description.contains("clear") || description.contains("sunny")
            || description.contains("açık") || description.contains("güneş")

    val startColor = when {
        isNight -> Color(0xFF020946)
        isRainy -> Color(0xFF4FC3F7)
        isSnowy -> Color(0xFFE1F5FE)
        isCloudy -> Color(0xFFCFD8DC)
        isClear -> Color(0xFFFFF176)
        else -> Color(0xFFCFD8DC)
    }

    val endColor = if (isNight) Color(0xFF5B68CC) else Color.White
    val textColor = if (isNight) Color.White else Color.Black

    val suggestion = viewModel.getSuggestion(description, isNight)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(startColor, endColor)))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Günün Önerisi",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(suggestion.animRes))
                    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = suggestion.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = suggestion.desc,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = textColor.copy(alpha = 0.8f),
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}