package me.camilanino.syntra.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Traffic
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
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import me.camilanino.syntra.R

@Composable
fun WelcomeScreen(
    onLoginUser: () -> Unit,
    onLoginTransito: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER PRINCIPAL ---
        CoralHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // --- CARDS INDEPENDIENTES (Card Stack) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OptionCard(
                title = "Tránsito",
                selected = selectedOption == "Tránsito",
                onClick = {
                    selectedOption = if (selectedOption == "Tránsito") null else "Tránsito"
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Traffic,
                        contentDescription = "Tránsito",
                        tint = if (selectedOption == "Tránsito") Color.White else Color(0xFFE74C3C),
                        modifier = Modifier.size(28.dp)
                    )
                },
            )

            Spacer(Modifier.height(14.dp))

            OptionCard(
                title = "Usuario",
                selected = selectedOption == "Usuario",
                onClick = {
                    selectedOption = if (selectedOption == "Usuario") null else "Usuario"
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Usuario",
                        tint = if (selectedOption == "Usuario") Color.White else Color(0xFFE74C3C),
                        modifier = Modifier.size(28.dp)
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- BOTÓN DE INICIO ---
        Button(
            onClick = {
                when (selectedOption) {
                    "Usuario" -> onLoginUser()
                    "Tránsito" -> onLoginTransito()
                }
            },
            enabled = selectedOption != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE74C3C),
                disabledContainerColor = Color(0xFFE74C3C).copy(alpha = 0.45f),
                disabledContentColor = Color.White.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .fillMaxWidth(0.68f)
                .height(56.dp)
                .shadow(10.dp, RoundedCornerShape(40.dp)),
            shape = RoundedCornerShape(40.dp)
        ) {
            Text(
                "Iniciar sesión",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(18.dp))
    }
}

/* ---------- CARD STACK OPTION ---------- */
@Composable
private fun OptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit
) {
    val targetScale = if (selected) 1.02f else 1f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(160, easing = LinearOutSlowInEasing), label = ""
    )

    // Colores y gradiente
    val backgroundBrush = if (selected) {
        Brush.verticalGradient(
            listOf(Color(0xFFFF8E79), Color(0xFFE74C3C))
        )
    } else Brush.verticalGradient(listOf(Color.White, Color.White))

    val borderColor = if (selected) Color(0xFFE74C3C) else Color(0xFFE6E6E6)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .height(90.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(backgroundBrush, RoundedCornerShape(22.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                leadingIcon()
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selected) Color.White else Color(0xFF2D2D2D)
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (selected) Color.White else Color(0xFFE74C3C),
                    unselectedColor = Color(0xFF6F6F6F)
                )
            )
        }
    }
}



/* ---------- HEADER (tu versión, sin cambiar lógica) ---------- */
@Composable
fun CoralHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
            .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFF8066), // salmón claro
                        Color(0xFFE74C3C)  // coral oscuro
                    )
                )
            ),
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

        // --- Contenido central ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_syntra),
                contentDescription = "Logo Syntra",
                modifier = Modifier
                    .size(120.dp)
            )


            Text(
                text = "Syntra",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Elige cómo quieres iniciar sesión",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.95f),
                letterSpacing = 0.3.sp
            )
        }
    }
}