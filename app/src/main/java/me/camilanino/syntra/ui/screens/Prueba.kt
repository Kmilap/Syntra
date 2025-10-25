package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.text.KeyboardOptions as FKeyboardOptions

/* ====== PALETA (colores consistentes con Syntra) ====== */
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
    leadingIcon: @Composable (() -> Unit),
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
            leadingIcon = leadingIcon,
            placeholder = { Text(placeholder, color = SyntraGray.copy(alpha = 0.7f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = Color.Transparent,
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
                    val icon = if (passwordVisible)
                        Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility
                    val desc = if (passwordVisible)
                        "Ocultar contraseña" else "Mostrar contraseña"
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
private fun HeaderWithWaveRegister(
    title: String = "Registrarse",
    subtitle: String = "Completa los campos para crear tu cuenta",
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
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

        // Ola inferior
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
                quadraticBezierTo(width * 0.25f, height * 0.9f, width * 0.5f, height * 0.6f)
                quadraticBezierTo(width * 0.75f, height * 0.3f, width * 1.0f, height * 0.8f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path = path, color = SyntraSalmon)
        }
    }
}

/* ====== PANTALLA REGISTRO ====== */
@Composable
fun Prueba(
    onRegister: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onLoginNavigate: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        HeaderWithWaveRegister()

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 150.dp),
            color = SyntraWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                // Correo
                LabeledTextField(
                    label = "Correo",
                    placeholder = "usuario@correo.com",
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SyntraGray) }
                )

                Spacer(Modifier.height(14.dp))

                // Documento
                LabeledTextField(
                    label = "Número de documento",
                    placeholder = "0000000000",
                    value = document,
                    onValueChange = { document = it },
                    leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = SyntraGray) },
                    keyboardType = KeyboardType.Number
                )

                Spacer(Modifier.height(14.dp))

                // Usuario
                LabeledTextField(
                    label = "Usuario",
                    placeholder = "NombreUsuario",
                    value = username,
                    onValueChange = { username = it },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = SyntraGray) }
                )

                Spacer(Modifier.height(14.dp))

                // Contraseña
                LabeledTextField(
                    label = "Contraseña",
                    placeholder = "••••••••",
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SyntraGray) },
                    isPassword = true
                )

                Spacer(Modifier.height(26.dp))

                Button(
                    onClick = { onRegister(email, document, username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraSalmon),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SyntraBlue)
                ) {
                    Text("Registrarse", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? ", color = SyntraGray)
                    Text(
                        "Inicia sesión",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onLoginNavigate() }
                    )
                }
            }
        }
    }
}


