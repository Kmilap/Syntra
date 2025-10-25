package me.camilanino.syntra.ui.screens



import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import me.camilanino.syntra.R

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    // Estado animado para simular parpadeo suave en los puntos
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alphaAnim = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Lanzar efecto que espera unos segundos y luego pasa al Welcome
    LaunchedEffect(Unit) {
        delay(4000) // duración de la pantalla de carga (4 s)
        onSplashFinished()
    }

    // Fondo del mapa con animación de brillo
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.mapita), // tu imagen del mapa
            contentDescription = "Mapa animado",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Capa animada de brillo tipo “puntos encendidos”
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = alphaAnim.value)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00FF7F).copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(400f, 700f),
                        radius = 500f
                    )
                )
        )
    }
}
