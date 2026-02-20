package com.example.weatherapp

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.LocationUtil
import com.example.weatherapp.model.NetworkUtil
import com.example.weatherapp.model.RetrofitClient
import com.example.weatherapp.ui.theme.Cloudy
import com.example.weatherapp.ui.theme.Rainy
import com.example.weatherapp.ui.theme.Snowy
import com.example.weatherapp.ui.theme.Sunny
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun WeatherScreen() {
    val coroutinescope = rememberCoroutineScope()
    val YOUR_API_KEY = "96625d4a2c647965c7520d944c6a8c61"

    var isError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var cityName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var temperature by remember { mutableStateOf<Int?>(null) }
    var weathercondition by remember { mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current //GPS servisi başlatmak için
    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    //Koordinatları Tutacak State'ler
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }


    fun performSearch() {
        //1. Önce interneti dışarda kontrol et.
        if (NetworkUtil.isInternetAvailable(context)) {
            isLoading = true //2.İnternet varsa çarkı döndür.
            isError = false //3. Önceki hataları temizle

            coroutinescope.launch {
                try {
                    val response = RetrofitClient.apiService.value.getWeatherData(
                        cityName,
                        YOUR_API_KEY,
                    )
                    cityName = response.name
                    weathercondition = response.weather[0].main
                    temperature = response.main.temp.toInt()
                    isLoading = false
                } catch (e: Exception) {
                    isLoading = false
                    isError = true

                    val error = when (e) {
                        is HttpException -> {
                            when (e.code()) {
                                404 -> "Şehir bulunamadı.Lütfen ismi kontrol edin."
                                401 -> "API Anahtarı geçersiz."
                                500 -> "Sunucu hatası. Daha sonra tekrar deneyin."
                                else -> "Beklenmedik bir sunucu hatası (Kod: ${e.code()})"
                            }
                        }

                        else -> "Bir ağ hatası oluştu veya İnternet bağlantınız kesildi."
                    }

                    coroutinescope.launch {
                        snackbarHostState.showSnackbar(
                            message = error,
                            actionLabel = "Kapat"
                        )
                    }
                }
            }
        } else {
            coroutinescope.launch {
                snackbarHostState.showSnackbar("İnternet bağlantısı bulunamadı!")
            }
        }
    }


    //Koordinatları çekmek için
    fun fetchLocation(snackbarHostState: SnackbarHostState) {
        //GPS kontrolü
        if (!LocationUtil.isLocationEnabled(context)) {
            //Kullanıcıya mesajı göster
            coroutinescope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Konum servisleri kapalı.",
                    actionLabel = "Ayarlar"
                )
                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    //Kullanıcı "Ayarlar" butonuna bastıysa, cihazın konum ayarlarına gönder
                    val intent =
                        android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
            }
            return // GPS kapalıysa işlem devam etmesin.
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        } catch (e: SecurityException) {
            isError = true
        }
    }


    val locationPermissionLauncher = rememberLauncherForActivityResult( //Konum izni sorma
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation(snackbarHostState)
            //İzin verildi konumu çekeceğiz.
        }
    }


    LaunchedEffect(Unit) { // Unit şu demek ekran ilk açıldığında bunu sadece bir kere yap.
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }


    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            //Koordinatlar geldi şimdi API çağrısı başlatalım.

            isLoading = true
            try {
                val response = RetrofitClient.apiService.value.getWeatherDataByCoords(
                    lat = latitude!!,
                    lon = longitude!!,
                    apiKey = YOUR_API_KEY
                )

                cityName = response.name  //GPS'ten gelen şehrini adını yaz
                weathercondition = response.weather[0].main  //Durum
                temperature = response.main.temp.toInt()  //Sıcaklık
                isError = false
                isLoading = false
            } catch (e: Exception) {
                isError = true
                isLoading = false
                snackbarHostState.showSnackbar("Konum hava durumu alınamadı.")
            }
        }
    }


    var weathercolor = when (weathercondition) {  //arkaplan rengi
        "Sunny", "Clear" -> Sunny
        "Cloudy", "Clouds", "Mist", "Fog" -> Cloudy
        "Rainy", "Drizzle", "Thunderstorm" -> Rainy
        "Snowy", "Snow" -> Snowy
        else -> Color.White
    }

    val animatedColor by animateColorAsState(  //arkaplan rengi animasyon geçişi
        targetValue = weathercolor,
        label = "BoxColorAnimation"   //hata ayıklama için
    )


    val animatedtemperature by animateIntAsState(  //animasyonlu sıcaklık değişimi
        targetValue = temperature ?: 0, //Eğer null ise animasyon 0'dan başlasın.
        label = "TemperatureAnimation"
    )




    Box(
        modifier = Modifier
            .fillMaxSize()   //Box ile main texti ortaladık ve arkaplan rengi aynı oldu.
            .background(animatedColor)
    )   //arkaplan rengini animatedColor'a verdik.

    {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (!isLoading) {
            when {
                //Durum 1: Eğer bir hata oluştuysa ekranda bu yazsın
                isError -> {
                    Text(
                        text = "Bir şeyler ters gitti. Lütfen tekrar deneyin.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                //Durum 2: Hata yoksa ve sıcaklık geldiyse hava durumunu göster
                temperature != null -> {
                    MainWeatherText(
                        modifier = Modifier.align(Alignment.Center),
                        temperature = animatedtemperature.toString() + "°",
                        weathercondition = weathercondition ?: ""
                    )
                }

                //Durum 3: Uygulama yeni açıldı, henüz hiçbir şey yoksa bunu göster
                else -> {
                    Text(
                        text = "Hava durumunu öğrenmek için bir şehir arayın veya konumu açın",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            }

            //Şehir ismi ve arama barı her zaman en üstte kalsın diye Column burada devam eder.
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                CitynameTitle(cityName = cityName)
                Searchbar(
                    onCitySelected = { cityName = it },
                    onSearchClick = { performSearch() }
                )
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CitynameTitle(cityName: String) {  //Üstteki konum bilgisi

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn, contentDescription = "location",
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = cityName,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Searchbar(
    onCitySelected: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    var textfortextfield = remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),//SONRADAN EKLENDİ
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = textfortextfield.value,
            onValueChange = {
                textfortextfield.value = it
                onCitySelected(it)
            },
            placeholder = { Text("Şehir Ara...") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                androidx.compose.material3.IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
    }

}

@Composable
fun MainWeatherText(
    temperature: String,
    weathercondition: String,
    modifier: Modifier = Modifier
) {
    val weatherIcon: ImageVector = when (weathercondition) {
        "Sunny", "Clear" -> Icons.Outlined.WbSunny
        "Cloudy", "Clouds" -> Icons.Outlined.Cloud
        "Rainy", "Rain", "Drizzle" -> Icons.Outlined.WaterDrop
        "Snowy", "Snow" -> Icons.Outlined.AcUnit
        else -> Icons.Outlined.HeartBroken
    }

    Box(
        modifier = modifier
            .padding(24.dp)
            .background(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(vertical = 40.dp, horizontal = 32.dp)
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = temperature.toString(),
                fontSize = 80.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Text(
                text = weathercondition,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(0.7f)
            )

            Icon(
                imageVector = weatherIcon,
                contentDescription = weatherIcon.toString(),
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 16.dp),
                tint = Color.DarkGray
            )
        }
    }
}
