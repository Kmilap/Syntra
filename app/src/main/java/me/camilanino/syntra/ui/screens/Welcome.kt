package me.camilanino.syntra.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import me.camilanino.syntra.R

@Composable
fun WelcomeScreen() {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER PRINCIPAL ---
        CoralHeader()

        Spacer(modifier = Modifier.height(28.dp))

        // --- CUADRO DE OPCIONES ---
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .shadow(10.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                OptionItem(
                    title = "Tránsito",
                    selected = selectedOption == "Tránsito",
                    onClick = { selectedOption = if (selectedOption == "Tránsito") null else "Tránsito" },
                    divider = true
                )
                OptionItem(
                    title = "Usuario",
                    selected = selectedOption == "Usuario",
                    onClick = { selectedOption = if (selectedOption == "Usuario") null else "Usuario" },
                    divider = false
                )
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        // --- BOTÓN DE INICIO ---
        Button(
            onClick = { /* Navegación futura */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(55.dp),
            shape = RoundedCornerShape(40.dp)
        ) {
            Text(
                "Iniciar sesión",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CoralHeader() {
    // --- Animación de brillo diagonal sutil ---
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shimmer = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
            .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            .background(Color(0xFFE74C3C))
            .drawWithCache {
                val gradient = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0f),
                        Color.White.copy(alpha = 0.18f),
                        Color.White.copy(alpha = 0f)
                    ),
                    start = Offset(size.width * shimmer.value, 0f),
                    end = Offset(size.width * shimmer.value - size.width / 2, size.height)
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(gradient, blendMode = BlendMode.Lighten)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // --- Textura de puntitos sutil ---
        Canvas(modifier = Modifier.matchParentSize()) {
            val spacing = 24.dp.toPx()
            val radius = 1.2.dp.toPx()
            for (x in 0..(size.width / spacing).toInt()) {
                for (y in 0..(size.height / spacing).toInt()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.16f),
                        radius = radius,
                        center = Offset(x * spacing, y * spacing)
                    )
                }
            }
        }

        // --- Contenido central del Header ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_syntra),
                contentDescription = "Logo Syntra",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Syntra",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Elige cómo quieres iniciar sesión",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.95f)
            )
        }
    }
}

@Composable
fun OptionItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    divider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (selected) Color(0xFFE74C3C) else Color(0xFF2D2D2D),
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFE74C3C),
                    unselectedColor = Color(0xFF505050)
                )
            )
        }

        if (divider) {
            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
