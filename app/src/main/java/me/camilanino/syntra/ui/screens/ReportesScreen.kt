package me.camilanino.syntra.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import me.camilanino.syntra.R
import me.camilanino.syntra.ui.screens.ReportRepository
import java.net.URL
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/* ====== FUENTES Y COLORES ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))
private val SyntraBlue = Color(0xFF4D81E7)
private val SyntraWhite = Color(0xFFF1F2F8)
private val SyntraGray = Color(0xFF6C7278)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraYellow = Color(0xFFFFC048)
private val SyntraRed = Color(0xFFE74C3C)



// Convierte coordenadas a dirección textual usando Google Geocoding API
suspend fun getAddressFromCoordinates(lat: Double, lng: Double, apiKey: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val url =
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=$apiKey"
            val response = URL(url).readText()
            val json = JSONObject(response)
            val results = json.getJSONArray("results")
            if (results.length() > 0) {
                results.getJSONObject(0).getString("formatted_address")
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


/* ====== SCREEN ====== */
@Composable
fun ReportesScreen(
    navController: NavController,
    role: String,
    fromMenu: Boolean = false,
    fromMap: Boolean = false,
    fromChatbot: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedEstado by remember { mutableStateOf("Operativo") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latLng: Pair<Double, Double>? by remember { mutableStateOf(null) } // ubicación standby
    // ✅ Se actualiza cada vez que el backStack cambia
    val currentBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(currentBackStackEntry?.savedStateHandle) {
        currentBackStackEntry?.savedStateHandle?.let { handle ->
            val lat = handle.get<Double>("selected_lat")
            val lng = handle.get<Double>("selected_lng")

            if (lat != null && lng != null) {
                latLng = lat to lng
                //  obtener dirección real
                val apiKey = "AIzaSyDXa5WiX58mev1nBbR6vi1SkpUD20gNoeM"
                val addressText = getAddressFromCoordinates(lat, lng, apiKey)

                address = if (!addressText.isNullOrBlank()) {
                    addressText
                } else {
                    "Coordenadas: %.5f, %.5f".format(lat, lng)
                }
            }
        }
    }



    // fotos seleccionadas
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var loading by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf<String?>(null) }

    // Picker múltiple de imágenes
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedPhotos = uris.take(6)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        /* ====== HEADER ====== */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.Black,
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        when {
                            fromChatbot -> navController.navigate("chatbot_screen/$role?fromMenu=false")
                            fromMap -> navController.navigate("mapa_screen/$role?fromMenu=false&fromMap=true")
                            fromMenu -> if (role == "usuario") {
                                navController.navigate("menu_user")
                            } else {
                                navController.navigate("menu_transito")
                            }
                            else -> if (role == "usuario") {
                                navController.navigate("main_page/usuario")
                            } else {
                                navController.navigate("main_page/agente")
                            }
                        }
                    }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Reportar semáforo",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SfProRounded
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* ====== IMAGEN ====== */
        Image(
            painter = painterResource(id = R.drawable.ic_traffic_light),
            contentDescription = "Semáforo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(180.dp)
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        /* ====== UBICACIÓN (standby) ====== */
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            placeholder = {
                Text(
                    text = "Ubicación (ej. Cabecera, Bucaramanga)",
                    fontFamily = SfPro,
                    color = SyntraGray.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Seleccionar ubicación en mapa",
                    tint = SyntraBlue,
                    modifier = Modifier.clickable {
                        // Navega hacia la pantalla de selección de ubicación
                        navController.navigate("select_location_screen")
                    }
                )
            }
            ,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = SyntraGray.copy(alpha = 0.4f)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        /* ====== ESTADOS ====== */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EstadoButton("Operativo", SyntraGreen, selectedEstado) { selectedEstado = it }
            EstadoButton("Inspección", SyntraYellow, selectedEstado) { selectedEstado = it }
            EstadoButton("Falla crítico", SyntraRed, selectedEstado) { selectedEstado = it }
        }

        Spacer(modifier = Modifier.height(18.dp))

        /* ====== DESCRIPCIÓN (el clip abre el picker) ====== */
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = {
                Text(
                    text = "Describe en detalle el problema del semáforo actualmente",
                    fontFamily = SfPro,
                    fontSize = 14.sp,
                    color = SyntraGray.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Attachment,
                    contentDescription = "Adjuntar fotos",
                    tint = SyntraGray,
                    modifier = Modifier.clickable {
                        // abre el selector de fotos
                        pickImagesLauncher.launch("image/*")
                    }
                )
            },

            trailingIcon = {
                if (selectedPhotos.isNotEmpty()) {
                    Text(
                        text = "${selectedPhotos.size}",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(14.dp)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = SyntraGray.copy(alpha = 0.4f)
            )
        )


        if (selectedPhotos.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                selectedPhotos.forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, SyntraGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(18.dp))

        /* ====== BOTÓN REPORTAR ====== */
        Button(
            onClick = {

                scope.launch {
                    if (address.isBlank() || description.isBlank()) {
                        statusMsg = "Completa la ubicación y la descripción."
                        return@launch
                    }

                    loading = true
                    val res = ReportRepository.createReport(
                        address = address,
                        lat = latLng?.first,
                        lng = latLng?.second,
                        statusUi = selectedEstado,
                        description = description,
                        role = role
                    )

                    if (res.isSuccess) {
                        val reportId = res.getOrNull()!!
                        if (selectedPhotos.isNotEmpty()) {
                            val up = ReportRepository.uploadReportPhotos(reportId, selectedPhotos)
                            up.onFailure { e ->
                                statusMsg = "Reporte creado, pero fotos fallaron: ${e.message}"
                            }
                        }
                        statusMsg = "Reporte enviado ✅"

                        //  lógica de notificación FCM
                        val accessToken = withContext(Dispatchers.IO) {
                            GenerateAccessToken.getAccessToken(context)
                        }

                        if (accessToken != null) {
                            val deviceToken = "c7Fw537QSHKXT4CQ_WTrQT:APA91bGhxDziCKJQTH_WVeg2QB6CKn3FH1KdGjZ7zEsQHJWuHJUxKWWKyrzdg3i16igck4GsBoA_8w2N7O7RLh-NhGgVFf51HgCu0bJSMTXqu0uT17H2rlM" // tu token real
                            withContext(Dispatchers.IO) {
                                FcmHttpSender.sendNotification(accessToken, deviceToken)
                            }
                        }

                        selectedEstado = "Operativo"
                        address = ""
                        description = ""
                        latLng = null
                        selectedPhotos = emptyList()
                    } else {
                        statusMsg = "Error: ${res.exceptionOrNull()?.message}"
                    }

                    loading = false
                }
            },
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SyntraBlue),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = if (loading) "Enviando..." else "Reportar",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SfPro,
                fontSize = 16.sp
            )
        }

        statusMsg?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.DarkGray, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/* ====== COMPONENTE DE BOTONES ====== */
@Composable
fun EstadoButton(label: String, color: Color, selected: String, onSelect: (String) -> Unit) {
    val isSelected = selected == label
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else SyntraGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect(label) }
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) color else Color.Black,
            fontFamily = SfPro,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}