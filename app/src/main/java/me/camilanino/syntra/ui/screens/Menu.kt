package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



/* ====== PALETA (tus colores) ====== */
private val SyntraSalmon = Color(0xD9E74C3C) // Rojo/Salmón del encabezado
private val SyntraYellow = Color(0xFFF7C800) // Amarillo de las tarjetas
private val SyntraGray = Color(0xFF6C7278)
private val MainBackground = Color(0xFFE4F0F7) // Fondo general azul claro/grisáceo


// --- COMPONENTES AUXILIARES ---

@Composable
private fun CardItem(title: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = SyntraSalmon,
        )
    ) {
        Column(Modifier.fillMaxSize()) {

            Spacer(Modifier.weight(1f))

            // Banda amarilla inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Altura de la banda amarilla
                    .background(SyntraYellow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CustomSearchBar() {
    OutlinedTextField(
        value = "", // Sin funcionalidad, valor vacío
        onValueChange = { /* Nada */ },
        placeholder = { Text("Busca servicios", color = SyntraGray.copy(alpha = 0.7f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = SyntraGray
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        shape = RoundedCornerShape(25.dp), // Esquinas muy redondeadas
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

// --- PANTALLA PRINCIPAL ---

@Composable
fun Menu() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        // 1. Contenedor Superior Rojo (Header)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(SyntraSalmon)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
        ) {
            // Fila de Perfil y Notificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Icon vectorial, para el perfil de usuario
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(Color.Gray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Icono de perfil",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Botón de Notificación (Campana)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    // Punto rojo de notificación
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Saludo
            Text(
                text = "Hola, Julian Lizcano",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Búsqueda
            CustomSearchBar()
        }

        // 2. Contenido Principal (Tarjetas)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item { CardItem(title = "Hacer reportes") }
                item { CardItem(title = "Mapa") }
                item { CardItem(title = "Chatbot Syntra") }
                item { CardItem(title = "FeedBack") }
            }

        }


        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar()
        }
    }
}