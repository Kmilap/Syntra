package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.camilanino.syntra.R

/* ====== FUENTES ====== */
private val SfPro = FontFamily(Font(R.font.sf_pro))
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))

/* ====== PALETA SINTRA ====== */
private val SyntraRed = Color(0xFFE74C3C)
private val SyntraYellow = Color(0xFFE3C04D)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraGray = Color(0xFF6C7278)

@Composable
fun MapaScreen(onBackClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        // === Fondo del mapa ===
        Image(
            painter = painterResource(id = R.drawable.mapita2), // cambia por el nombre real del archivo
            contentDescription = "Mapa base",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // === Flecha de retroceso ===
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(start = 14.dp, top = 24.dp)
                .size(38.dp)
                .align(Alignment.TopStart)
                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Volver",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }

        // === Tarjeta inferior ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .border(0.8.dp, Color(0xFFE0E0E0), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // === Estados de semáforos ===
                EstadoItem(color = SyntraRed, estado = "Falla crítica", ubicacion = "Av. Libertad y Calle 20")
                EstadoItem(color = SyntraYellow, estado = "En inspección", ubicacion = "Calle 5 y Calle 12")
                EstadoItem(color = SyntraGreen, estado = "Operativo", ubicacion = "Av. Juárez y Calle 1")

                Spacer(Modifier.height(12.dp))

                // === Botones inferiores ===
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { /* acción reportar */ },
                        colors = ButtonDefaults.buttonColors(containerColor = SyntraGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "Reportar falla",
                            fontFamily = SfPro,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = { /* acción reportes recientes */ },
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "Reportes Rec",
                            fontFamily = SfPro,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

/* ====== ITEM DE ESTADO ====== */
@Composable
fun EstadoItem(color: Color, estado: String, ubicacion: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Column {
            Text(
                text = estado,
                fontFamily = SfPro,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = ubicacion,
                fontFamily = SfProRounded,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = SyntraGray.copy(alpha = 0.8f)
            )
        }
    }
}
