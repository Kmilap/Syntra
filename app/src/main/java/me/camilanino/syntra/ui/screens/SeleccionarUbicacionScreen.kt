package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng

@Composable
fun SeleccionarUbicacionScreen(navController: NavController) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // Configuración inicial del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.119349, -73.122741), 13f) // Bucaramanga
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa interactivo
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Ubicación seleccionada"
                )
            }
        }

        /// Botón de confirmación
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    selectedLocation?.let { latLng ->
                        // Guardar coordenadas en el back stack
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.apply {
                                set("selected_lat", latLng.latitude)
                                set("selected_lng", latLng.longitude)
                            }


                        navController.popBackStack()
                    }
                },
                enabled = selectedLocation != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4D81E7),
                    disabledContainerColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (selectedLocation == null)
                        "Toca el mapa para seleccionar"
                    else
                        "Usar esta ubicación",
                    color = Color.White
                )
            }
        }

    }
}
