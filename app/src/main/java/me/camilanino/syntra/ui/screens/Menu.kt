package me.camilanino.syntra.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/* ====== PALETA ====== */
private val SyntraCoral = Color(0xFFE64A3D)
private val SyntraSalmon = Color(0xFFFF8066)
private val SyntraGray = Color(0xFF6C7278)
private val BackgroundGray = Color(0xFFECECEC)
private val TextDark = Color(0xFF2B2B2B)

/* ====== TARJETAS ====== */

@Composable
private fun CardItem(title: String, navController: NavController, role: String) {
    var pressed by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = if (pressed) 10.dp else 4.dp,
        animationSpec = spring(dampingRatio = 0.7f)
    )

    val icon = when (title) {
        "Hacer reportes" -> Icons.Outlined.AssignmentTurnedIn
        "Mapa" -> Icons.Outlined.Map
        "Chatbot Syntra" -> Icons.Outlined.SmartToy
        "Feedback" -> Icons.Outlined.RateReview
        else -> Icons.Outlined.AssignmentTurnedIn
    }


    val destination = when (title) {
        "Hacer reportes" -> "report_screen/$role?fromMenu=true"
        "Mapa" -> "mapa_screen/$role?fromMenu=true"
        "Chatbot Syntra" -> "chatbot_screen/$role?fromMenu=true"
        "Feedback" -> "feedback_screen/$role?fromMenu=true"
        else -> ""
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = !pressed
                if (destination.isNotEmpty()) {
                    navController.navigate(destination) {
                        popUpTo("menu_user") { inclusive = false }
                    }
                }

            }
            .shadow(elevation, RoundedCornerShape(24.dp))
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
                tint = SyntraCoral,
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
private fun CustomSearchBar1(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val focus = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Busca servicios",
                color = SyntraGray.copy(alpha = 0.7f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = SyntraGray
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                        focus.clearFocus()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar",
                        tint = SyntraGray
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focus.clearFocus()
                onSearch()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(4.dp, RoundedCornerShape(25.dp))
    )
}

/* ====== PANTALLA PRINCIPAL ====== */
@Composable
fun Menu(navController: NavController, role: String = "usuario") {
    // --- Estado de búsqueda ---
    var query by rememberSaveable { mutableStateOf("") }
    var lastSearched by rememberSaveable { mutableStateOf("") }

    // Lista base
    val allItems = remember { listOf("Hacer reportes", "Mapa", "Chatbot Syntra", "Feedback") }

    // Filtrado en vivo
    val filtered = remember(query) {
        if (query.isBlank()) allItems
        else allItems.filter { it.contains(query.trim(), ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SyntraCoral, SyntraSalmon)
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
                // Flecha de retroceso
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.navigate("main_page/usuario") }
                )

                // Perfil
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                                navController.navigate("profile_user?fromMenu=true")
                            }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // Título
            Text(
                text = "Menú",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                modifier = Modifier.padding(start = 5.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Barra de búsqueda funcional
            CustomSearchBar1(
                query = query,
                onQueryChange = { query = it },
                onSearch = { lastSearched = query }
            )
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
                // Ítems filtrados
                items(filtered.size) { idx ->
                    when (filtered[idx]) {
                        "Hacer reportes" -> CardItem("Hacer reportes", navController, role)
                        "Mapa" -> CardItem("Mapa", navController, role)
                        "Chatbot Syntra" -> CardItem("Chatbot Syntra", navController, role)
                        "Feedback" -> CardItem("Feedback", navController, role)
                    }
                }

                // Estado vacío
                if (filtered.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin resultados para “$query”",
                                color = SyntraGray.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // DEGRADADO INFERIOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00FFFFFF),
                            Color(0xFFF66B54).copy(alpha = 0.85f),
                            Color(0xFFFF9E85).copy(alpha = 0.9f),
                            Color(0xFFFFF2EF)
                        )
                    )
                )
        )

        // SOMBRA SUTIL
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

        // CURVA INFERIOR
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

        // TAGLINE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Reporta, consulta y contribuye con tu ciudad",
                color = SyntraGray.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 30.dp)
            )
        }
    }
}