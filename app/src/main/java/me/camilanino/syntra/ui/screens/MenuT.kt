package me.camilanino.syntra.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/* ====== PALETA ====== */
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraMint  = Color(0xFF8FD6AE)
private val SyntraGray = Color(0xFF6C7278)
private val BackgroundGray = Color(0xFFECECEC)
private val TextDark = Color(0xFF2B2B2B)

/* ====== TARJETAS ====== */

@Composable
private fun CardItem(title: String, onClick: () -> Unit) {
    // Ícono según el título
    val icon = when (title) {
        "Hacer reportes" -> Icons.Outlined.AssignmentTurnedIn
        "Mapa" -> Icons.Outlined.Map
        "Historial" -> Icons.Outlined.History
        "Estadísticas" -> Icons.Outlined.Insights
        else -> Icons.Outlined.AssignmentTurnedIn
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .shadow(4.dp, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF9F9F9))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SyntraGreen,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 12.dp)
            )

            Text(
                text = title,
                color = TextDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}


/* ====== BARRA DE BÚSQUEDA ====== */
@Composable
private fun CustomSearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Busca servicios", color = SyntraGray.copy(alpha = 0.7f)) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = SyntraGray)
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(4.dp, RoundedCornerShape(25.dp))
    )
}

/* ====== PANTALLA PRINCIPAL (MODO TRÁNSITO) ====== */
@Composable
fun MenuT(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // HEADER degradado verde → menta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SyntraGreen, SyntraMint)
                    )
                )
        )

        // CONTENIDO DEL HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            // Fila superior: flecha y perfil
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 🔹 Flecha de regreso al Main Page del agente
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.navigate("main_page/agente") }
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable { navController.navigate("profile_transito?fromMenu=true") }, // 👈 importante
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Perfil tránsito",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "Hola, Agente :)",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )

            Spacer(Modifier.height(24.dp))
            CustomSearchBar()
        }

        // CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 280.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    CardItem("Hacer reportes") {
                        navController.navigate("report_screen/agente?fromMenu=true")
                    }
                }
                item {
                    CardItem("Mapa") {
                        navController.navigate("mapa_screen/agente?fromMenu=true")
                    }
                }
                item {
                    CardItem("Historial") {
                        navController.navigate("history_screen/agente?fromMenu=true")
                    }

                }
                item {
                    CardItem("Estadísticas") {
                        navController.navigate("estadisticas_screen/agente")

                    }
                }
            }
        }

        // ===== DEGRADADO INFERIOR =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00FFFFFF),                         // transparente arriba
                            SyntraGreen.copy(alpha = 0.85f),           // verde medio intenso
                            SyntraMint.copy(alpha = 0.9f),             // menta claro
                            Color(0xFFF2FFF8)                          // base blanca verdosa
                        )
                    )
                )
        )

        // ===== SOMBRA SUTIL =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ===== CURVA INFERIOR =====
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
        ) {
            val curveHeight = 60f
            val path = Path().apply {
                moveTo(0f, 0f)
                quadraticBezierTo(size.width / 2, curveHeight * 2, size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, Color.White)
        }

        // ===== TAGLINE =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Gestiona, consulta y optimiza el tránsito",
                color = SyntraGray.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 30.dp)
            )
        }
    }
}

