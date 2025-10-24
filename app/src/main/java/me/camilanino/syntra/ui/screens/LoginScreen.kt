package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.text.KeyboardOptions as FKeyboardOptions

/* ====== PALETA (tus colores) ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xD9E74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)

/* ====== TEXT FIELD REUTILIZABLE ====== */
@Composable
private fun LabeledTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = SyntraGray) },
            placeholder = { Text(placeholder, color = SyntraGray.copy(alpha = 0.7f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = SyntraWhite.copy(alpha = 0f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = SyntraBlue
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            keyboardOptions = FKeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                imeAction = imeAction
            ),
            trailingIcon = {
                if (isPassword) {
                    val icon = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility
                    val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = desc, tint = SyntraGray)
                    }
                }
            },
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

/* ====== HEADER CON OLA ====== */
@Composable
private fun HeaderWithWave(
    title: String,
    subtitle: String,
    waveHeightDp: Int = 56
) {
    val waveHeightPx = with(LocalDensity.current) { waveHeightDp.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SyntraSalmon)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        // Contenido del header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        // OLA: dibujada al fondo, pegada a la parte inferior del header
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(waveHeightDp.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, 0f)
                // Curva suave tipo seno. Ajusta control points para variar la ola.
                quadraticBezierTo(
                    width * 0.25f, height * 0.9f,
                    width * 0.5f, height * 0.6f
                )
                quadraticBezierTo(
                    width * 0.75f, height * 0.3f,
                    width * 1.0f, height * 0.8f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path = path, color = SyntraSalmon)
        }
    }
}

/* ====== PANTALLA LOGIN A PANTALLA COMPLETA ====== */
@Composable
fun LoginScreen(
    onLogin: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite) // Fondo general
    ) {
        // Header con ola
        HeaderWithWave(
            title = "Ingresa en tu cuenta",
            subtitle = "Introduce tu email y contraseña para iniciar sesión",
            waveHeightDp = 64
        )

        // Cuerpo blanco (resto de la pantalla)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f), // mantiene 40% header, 60% cuerpo
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 150.dp),
            color = SyntraWhite,
            tonalElevation = 2.dp // le da un leve realce
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                // Usuario (elige Email o Text según tu caso)
                LabeledTextField(
                    label = "Usuario",
                    placeholder = "usuario@correo.com",
                    value = user,
                    onValueChange = { user = it },
                    leadingIcon = Icons.Outlined.Email,
                    isPassword = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(Modifier.height(14.dp))

                // Contraseña
                LabeledTextField(
                    label = "Contraseña",
                    placeholder = "••••••••",
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = SyntraBlue)
                    )
                    Text("Acuérdate de mí", color = SyntraGray, modifier = Modifier.weight(1f))
                    Text(
                        "¿Has olvidado tu contraseña?",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onForgotPassword() }
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { onLogin(user.trim(), password, rememberMe) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraSalmon),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SyntraBlue)
                ) {
                    Text("Ingresa", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("¿No tienes una cuenta? ", color = SyntraGray)
                    Text(
                        "registrarse",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onRegister() }
                    )
                }
            }
        }
    }
}