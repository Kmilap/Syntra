package me.camilanino.syntra.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
private val SyntraLightGray = Color(0xFFE6E6E6)
private val SyntraDarkBlue = Color(0xFF273746)

/* ====== PANTALLA PRINCIPAL ====== */

@Composable
fun ChatbotScreen(navController: NavController, role: String,fromMenu: Boolean = false) {
    // Solo para depuración inicial
    LaunchedEffect(role) {
        println("Rol detectado en ChatbotScreen: $role")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        ChatHeader(navController, role,fromMenu)
        Spacer(Modifier.height(10.dp))
        ChatMessages(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        ChatInputBar()
    }
}

/* ====== ENCABEZADO ====== */
@Composable
fun ChatHeader(navController: NavController, role: String, fromMenu: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.Black,
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        if (fromMenu) {
                            if (role == "usuario") {
                                navController.navigate("menu_user")
                            } else {
                                navController.navigate("menu_transito")
                            }
                        } else {
                            navController.navigate("main_page/$role")
                        }
                    }

            )
            Spacer(Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Asistente de Syntra",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SfProRounded
                )
                Text(
                    text = "Reporta y consulta por chat",
                    color = SyntraGray.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontFamily = SfPro
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_danger),
            contentDescription = "Información",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
}


/* ====== CUERPO DEL CHAT ====== */
@Composable
fun ChatMessages(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SyntraLightGray)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        ChatBubble(text = "Hola, ¿en qué puedo ayudarte?", isUser = false)
        ChatBubble(text = "Tengo un problema con un semáforo que no cambia.", isUser = true)
        ChatBubble(text = "¿Podrías indicarme la ubicación del semáforo?", isUser = false)
    }
}

/* ====== BURBUJAS ====== */
@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    val bubbleColor = if (isUser) SyntraGreen else Color.LightGray.copy(alpha = 0.8f)
    val boxAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val cornerShape = if (isUser)
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    else
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = boxAlignment
    ) {
        Box(
            modifier = Modifier
                .clip(cornerShape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = text,
                color = if (isUser) Color.White else Color.Black,
                fontSize = 14.sp,
                fontFamily = SfPro,
                lineHeight = 18.sp
            )
        }
    }
}

/* ====== INPUT BAR ====== */
@Composable
fun ChatInputBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SyntraLightGray)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de adjuntar
            Icon(
                imageVector = Icons.Outlined.AttachFile,
                contentDescription = "Adjuntar",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Texto del placeholder (sin OutlinedTextField)
            Text(
                text = "Describe el problema",
                color = SyntraGray.copy(alpha = 0.6f),
                fontFamily = SfPro,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Ícono de enviar
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = "Enviar",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}