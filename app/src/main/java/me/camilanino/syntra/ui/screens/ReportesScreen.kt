package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.camilanino.syntra.R

/* ====== FUENTES Y COLORES ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))
private val SyntraBlue = Color(0xFF4D81E7)
private val SyntraWhite = Color(0xFFF1F2F8)
private val SyntraGray = Color(0xFF6C7278)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraYellow = Color(0xFFFFC048)
private val SyntraRed = Color(0xFFE74C3C)
private val SyntraDarkBlue = Color(0xFF273746)

/* ====== REPORTES SCREEN ====== */
@Composable
fun ReportesScreen(
    navController: NavController,
    role: String,
    fromMenu: Boolean = false,
    fromMap: Boolean = false) {
    var selectedEstado by remember { mutableStateOf("Operativo") }

    // Solo para depuración (ver en logcat)
    LaunchedEffect(role) {
        println("Rol detectado en ReportesScreen: $role")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        /* ====== HEADER ====== */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.Black,
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        when {
                            // 🔹 Si vino desde el mapa, regresa al mapa
                            fromMap -> {
                                navController.navigate("mapa_screen/$role?fromMenu=false&fromMap=true")
                            }

                            // 🔹 Si vino directamente del menú, regresa al menú
                            fromMenu -> {
                                if (role == "usuario") {
                                    navController.navigate("menu_user")
                                } else {
                                    navController.navigate("menu_transito")
                                }
                            }

                            // 🔹 En cualquier otro caso, regresa al MainPage según rol
                            else -> {
                                if (role == "usuario") {
                                    navController.navigate("main_page/usuario")
                                } else {
                                    navController.navigate("main_page/agente")
                                }
                            }
                        }
                    }
            )


            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Reportar semáforo",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SfProRounded
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        /* ====== IMAGEN ====== */
        Image(
            painter = painterResource(id = R.drawable.ic_traffic_light),
            contentDescription = "Semáforo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(180.dp)
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        /* ====== CAMPO UBICACIÓN ====== */
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "Ubicación",
                    fontFamily = SfPro,
                    color = SyntraGray.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SyntraBlue
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = SyntraGray.copy(alpha = 0.4f)
            )
        )

        Spacer(modifier = Modifier.height(22.dp))

        /* ====== BOTONES DE ESTADO ====== */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EstadoButton("Operativo", SyntraGreen, selectedEstado) { selectedEstado = it }
            EstadoButton("Inspección", SyntraYellow, selectedEstado) { selectedEstado = it }
            EstadoButton("Falla crítico", SyntraRed, selectedEstado) { selectedEstado = it }
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* ====== CAMPO DESCRIPCIÓN ====== */
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "Describe en detalle el problema del semáforo actualmente",
                    fontFamily = SfPro,
                    fontSize = 14.sp,
                    color = SyntraGray.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Attachment,
                    contentDescription = null,
                    tint = SyntraGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = SyntraGray.copy(alpha = 0.4f)
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        /* ====== BOTÓN REPORTAR ====== */
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SyntraBlue),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Reportar",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SfPro,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/* ====== COMPONENTE DE BOTONES ====== */
@Composable
fun EstadoButton(label: String, color: Color, selected: String, onSelect: (String) -> Unit) {
    val isSelected = selected == label
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else SyntraGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect(label) }
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) color else Color.Black,
            fontFamily = SfPro,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
