package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.camilanino.syntra.R

/* ====== FUENTES PERSONALIZADAS ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

/* ====== PALETA SINTRA ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xFFE74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)
private val SyntraGreen  = Color(0xFF63B58D)
private val SyntraDarkBlue = Color(0xFF273746) // color del botón central



/* ====== MAIN PAGE ====== */
@Composable
fun MainPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        TopSection()
        Spacer(Modifier.height(26.dp))
        MainContent()
        Spacer(Modifier.height(36.dp))
        ReportSummary()
        Spacer(modifier = Modifier.weight(1f))
        BottomNavBar()
    }
}

/* ====== HEADER SUPERIOR ====== */
@Composable
fun TopSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Syntra",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SfPro
            )

            Image(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = "Notificación",
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Hola, Juan Diego",
            color = SyntraGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            fontFamily = SfPro
        )
    }
}

/* ====== CONTENIDO CENTRAL ====== */
@Composable
fun MainContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen del semáforo principal
        Image(
            painter = painterResource(id = R.drawable.ic_traffic_light),
            contentDescription = "Semáforo",
            modifier = Modifier
                .width(210.dp)
                .height(250.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Título principal
        Text(
            text = "Avista y reporta\nsemáforos averiados",
            fontFamily = SfProRounded,
            fontWeight = FontWeight.Medium,
            fontSize = 32.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(Modifier.height(8.dp))

        // Subtítulo
        Text(
            text = "Supervisa el estado de los semáforos de tu ciudad en tiempo real",
            fontFamily = SfPro,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = SyntraGray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(26.dp))

        // Botón Reportar Falla
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SyntraGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_report),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Reportar falla",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SfPro
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botón Ver Reportes
        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.8.dp, Color.Black.copy(alpha = 0.8f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_list),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Ver reportes",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SfPro
                )
            }
        }
    }
}

/* ====== RESUMEN DE REPORTES ====== */
@Composable
fun ReportSummary() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFE6E6E6), // gris más fuerte
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumen actual de reportes",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                fontFamily = SfPro
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem("Activos", "124", showDot = true)
                SummaryItem("En inspección", "7")
                SummaryItem("Falla crítica", "0")
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, showDot: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showDot) {
                Canvas(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 4.dp)
                ) {
                    drawCircle(color = SyntraGreen, style = Fill)
                }
            }
            Text(text = label, color = SyntraGray, fontSize = 13.sp, fontFamily = SfPro)
        }
        Text(
            text = value,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = SfPro
        )
    }
}

/* ====== BARRA DE NAVEGACIÓN INFERIOR ====== */
@Composable
fun BottomNavBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = SyntraGray,
                modifier = Modifier.size(26.dp)
            )

            // Botón central resaltado
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SyntraDarkBlue)
                    .padding(8.dp), // leve respiro interior
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_traffic_center),
                    contentDescription = "Semáforo central",
                    modifier = Modifier.size(70.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_history),
                contentDescription = "History",
                tint = SyntraGray,
                modifier = Modifier.size(26.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                tint = SyntraGray,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}