package me.camilanino.syntra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import me.camilanino.syntra.R
import me.camilanino.syntra.ui.screens.ReportRepository
import me.camilanino.syntra.ui.screens.ReportStats

/* ====== FUENTES Y COLORES ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

private val SyntraRed = Color(0xFFC25B48)
private val SyntraRedDark = Color(0xFF9F4B3C)
private val SyntraWhite = Color(0xFFF9FAFC)
private val SyntraGray = Color(0xFF6C7278)
private val SyntraLightGray = Color(0xFFE6E6E6)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraYellow = Color(0xFFE0B94A)
private val SyntraOrange = Color(0xFFE74C3C)

private val SyntraGreenDark = Color(0xFF33B06B)

/* ====== PANTALLA ESTADÍSTICAS ====== */
@Composable
fun EstadisticasScreen(navController: NavController, fromChatbot: Boolean = false) {
    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var stats by remember { mutableStateOf(ReportStats()) }

    // Carga
    LaunchedEffect(Unit) {
        delay(150)
        visible = true
        scope.launch {
            loading = true
            error = null
            val res = ReportRepository.getReportStats()
            if (res.isSuccess) {
                stats = res.getOrThrow()
            } else {
                error = res.exceptionOrNull()?.message ?: "Error desconocido"
            }
            loading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Encabezado
            EstadisticasHeader(
                onBackClick = {
                    if (fromChatbot) {
                        navController.navigate("chatbot_screen/agente?fromMenu=false")
                    } else {
                        navController.navigate("menu_transito")
                    }
                }
            )

            AnimatedVisibility(visible = visible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    // Tarjeta principal
                    ReportesActivosCard(
                        activos = stats.active
                    )

                    Spacer(modifier = Modifier.height(34.dp))

                    Divider(
                        color = SyntraLightGray.copy(alpha = 0.8f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Tarjetas inferiores (dinámicas)
                    EstadisticasCardsRow(
                        fixedPercent = stats.fixedPercent,
                        inspection = stats.inspection,
                        urgent = stats.urgent
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = SyntraRed
            )
        }

        error?.let {
            Text(
                text = "Error cargando estadísticas: $it",
                color = SyntraOrange,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                fontSize = 13.sp
            )
        }
    }
}

/* ====== ENCABEZADO CON FLECHA ====== */
@Composable
fun EstadisticasHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SyntraGreen, SyntraGreenDark)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(95.dp))

            Text(
                text = "Estadísticas",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SfProRounded
            )
        }
    }
}



/* ====== TARJETA PRINCIPAL ====== */
@Composable
fun ReportesActivosCard(activos: Int) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(180)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { pressed = !pressed },
        shape = RoundedCornerShape(24.dp),
        color = SyntraLightGray,
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_traffic_card),
                contentDescription = "Semáforo",
                modifier = Modifier.size(95.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = activos.toString(),
                    fontFamily = SfProRounded,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    color = Color.Black
                )
                Text(
                    text = "Reportes activos",
                    fontFamily = SfPro,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    color = Color.Black
                )
            }
        }
    }
}

/* ====== TARJETAS INFERIORES ====== */
@Composable
fun EstadisticasCardsRow(
    fixedPercent: Int,
    inspection: Int,
    urgent: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Verde — porcentaje de semáforos arreglados
        EstadisticaItem(
            titulo = "Estado de los semáforos",
            valor = "${fixedPercent}%",
            subtitulo = "Arreglados",
            colorValor = SyntraGreen,
            icono = R.drawable.ic_circle_green,
            modifier = Modifier.weight(1f)
        )
        // Amarillo — semáforos en inspección
        EstadisticaItem(
            titulo = "Semáforos en inspección",
            valor = inspection.toString(),
            subtitulo = "Actualmente",
            colorValor = SyntraYellow,
            icono = R.drawable.ic_circle_yellow,
            modifier = Modifier.weight(1f)
        )
        // Rojo — reportes urgentes
        EstadisticaItem(
            titulo = "Reportes de atención urgente",
            valor = urgent.toString(),
            subtitulo = "Activos",
            colorValor = SyntraOrange,
            icono = R.drawable.ic_circle_red,
            modifier = Modifier.weight(1f)
        )
    }
}

/* ====== COMPONENTE DE CADA TARJETA ====== */
@Composable
fun EstadisticaItem(
    titulo: String,
    valor: String,
    subtitulo: String,
    colorValor: Color,
    icono: Int,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(180)
    )

    Surface(
        modifier = modifier
            .height(260.dp)
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { pressed = !pressed },
        shape = RoundedCornerShape(24.dp),
        color = SyntraLightGray,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = titulo,
                color = Color.Black,
                fontFamily = SfPro,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = valor,
                color = colorValor,
                fontFamily = SfProRounded,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitulo,
                color = SyntraGray.copy(alpha = 0.8f),
                fontFamily = SfPro,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))
            Image(
                painter = painterResource(id = icono),
                contentDescription = subtitulo,
                modifier = Modifier.size(65.dp)
            )
        }
    }
}