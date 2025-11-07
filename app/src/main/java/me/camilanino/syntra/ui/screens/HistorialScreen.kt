package me.camilanino.syntra.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.camilanino.syntra.R


/* ====== FUENTES ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

/* ====== PALETA SINTRA ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xFFE74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)
private val SyntraGreen  = Color(0xFF63B58D)
private val SyntraDarkBlue = Color(0xFF273746)
private val SyntraYellow = Color(0xFFE3C04D)
private val SyntraBackground = Color(0xFFF2F4F7)

/* ====== DATOS DE EJEMPLO ====== */
data class Reporte(
    val nombre: String,
    val estado: String,
    val colorEstado: Color
)

private val listaReportes = listOf(
    Reporte("Calle 45 x Cra 27", "Operativo", SyntraGreen),
    Reporte("Carrera 36", "Inspección", SyntraYellow),
    Reporte("Coaviconsa", "Falla crítico", SyntraSalmon),
    Reporte("Calle 30 - Quebra", "Operativo", SyntraGreen),
    Reporte("El Tejar", "Operativo", SyntraGreen)
)

/* ====== HISTORIAL SCREEN ====== */
@Composable
fun HistorialScreen(navController: androidx.navigation.NavController,role: String, fromMenu: Boolean = false, fromMap: Boolean = false) {

    LaunchedEffect(role) {
        println("Rol detectado en HistorialScreen: $role")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(36.dp))
            TopHistorialBar(navController, role, fromMenu,fromMap)
            Spacer(Modifier.height(22.dp))
            SearchBar()
            Spacer(Modifier.height(20.dp))
            ReportesList()
            Spacer(modifier = Modifier.height(80.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

        }
    }
}


/* ====== TOP BAR ====== */
@Composable
fun TopHistorialBar(navController: NavController, role: String, fromMenu: Boolean = false,fromMap: Boolean= false ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Volver",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        when {
                            fromMap -> {
                                navController.navigate("mapa_screen/$role?fromMenu=true") {
                                    popUpTo("history_screen/$role") { inclusive = true }
                                }
                            }
                            fromMenu -> {
                                val destino = if (role == "usuario") "menu_user" else "menu_transito"
                                navController.navigate(destino) {
                                    popUpTo("history_screen/$role") { inclusive = true }
                                }
                            }
                            else -> {
                                navController.navigate("main_page/$role") {
                                    popUpTo("history_screen/$role") { inclusive = true }
                                }
                            }
                        }
                    }

            )


            Spacer(Modifier.width(12.dp))
            Text(
                text = "Historial",
                color = Color.Black,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SfPro
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = "Menú",
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
    }
}


/* ====== SEARCH BAR ====== */
@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFDADADA), RoundedCornerShape(50.dp)), // borde fino, limpio
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Buscar",
                tint = Color(0xFF9C9C9C),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Buscar",
                color = Color(0xFF9C9C9C),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SfPro
            )
        }
    }
}

/* ====== LISTA DE REPORTES ====== */
@Composable
fun ReportesList() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items(listaReportes) { reporte ->
            ReporteItem(reporte)
        }
    }
}

/* ====== ITEM DE REPORTE ====== */
@Composable
fun ReporteItem(reporte: Reporte) {
    var visible by remember { mutableStateOf(false) }
    val alpha = animateFloatAsState(if (visible) 1f else 0f)
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .graphicsLayer(alpha = alpha.value)
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp, // más difusa y flotante
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF000000).copy(alpha = 0.10f),
                spotColor = Color(0xFF000000).copy(alpha = 0.10f)
            )
            .background(Color.White, RoundedCornerShape(28.dp))
            .border(0.6.dp, Color(0xFFE6E6E6), RoundedCornerShape(28.dp))
            .padding(horizontal = 22.dp, vertical = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_traffic_card),
                    contentDescription = "Semáforo",
                    modifier = Modifier
                        .size(62.dp) // tamaño más grande
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = reporte.nombre,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontFamily = SfPro
                    )
                    Text(
                        text = "Actualizado hace 3 horas",
                        color = SyntraGray.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        letterSpacing = (-0.1).sp,
                        fontFamily = SfPro
                    )
                }
            }

            // === CHIP DE ESTADO ===
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        when (reporte.estado) {
                            "Operativo" -> SyntraGreen.copy(alpha = 0.4f)
                            "Inspección" -> SyntraYellow.copy(alpha = 0.4f)
                            "Falla crítico" -> SyntraSalmon.copy(alpha = 0.4f)
                            else -> SyntraGray.copy(alpha = 0.3f)
                        }
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = reporte.estado,
                    color = when (reporte.estado) {
                        "Operativo" -> SyntraGreen
                        "Inspección" -> SyntraYellow
                        "Falla crítico" -> SyntraSalmon
                        else -> SyntraGray
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SfProRounded,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

