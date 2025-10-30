package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/* ====== PALETA (misma que tu registro) ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xD9E74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)

/* ====== TextField reutilizable ====== */
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
            fontSize = 13.sp,
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
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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

/* ====== Avatar simple (fallback icon) ====== */
@Composable
private fun ProfileAvatar() {
    Box(
        modifier = Modifier
            .size(112.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDEDEF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = SyntraGray,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}
@Composable
private fun BigHeader(
    title: String = "Perfil",
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(SyntraSalmon)
    ) {
        // Flecha de retroceso
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Título centrado
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        )

        // Ola inferior
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
        ) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, 0f)
                quadraticBezierTo(w * 0.25f, h * 1.1f, w * 0.5f, h * 0.7f)
                quadraticBezierTo(w * 0.75f, h * 0.3f, w, h * 0.9f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path, SyntraSalmon)
        }
    }
}


/* ====== Pantalla Perfil (todo en un solo Box) ====== */
@Composable
fun ProfileScreen(
    initialName: String = "Juan Diego Niño Solano",
    initialUsername: String = "jnino825",
    initialEmail: String = "jnino825@unab.edu.co",
    initialDocument: String = "1235 6478 990",
    onForgotPassword: () -> Unit = {},
    onUpdate: (String, String, String) -> Unit = { _, _, _ -> },
    onSave: (String, String, String) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initialName) }
    var username by remember { mutableStateOf(initialUsername) }
    var email by remember { mutableStateOf(initialEmail) }
    var document by remember { mutableStateOf(initialDocument) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        // Header grande
        BigHeader("Perfil", onBack = { /* navController.popBackStack() */ })

        // Cuerpo principal (sin cards anidadas)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            // Avatar + nombre
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar()
                Spacer(Modifier.height(10.dp))
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )
            }

            Spacer(Modifier.height(18.dp))

            // Campos (directo, sin caja extra)
            LabeledTextField(
                label = "Usuario",
                placeholder = "usuario",
                value = username,
                onValueChange = { username = it },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = SyntraGray) }
            )
            Spacer(Modifier.height(14.dp))

            LabeledTextField(
                label = "Correo",
                placeholder = "usuario@correo.com",
                value = email,
                onValueChange = { email = it },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SyntraGray) },
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(14.dp))

            LabeledTextField(
                label = "Número de documento",
                placeholder = "0000 0000 000",
                value = document,
                onValueChange = { document = it },
                leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = SyntraGray) },
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )

            Spacer(Modifier.height(18.dp))

            // Botones principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onUpdate(username, email, document) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SyntraSalmon),
                    border = BorderStroke(1.dp, SyntraSalmon)
                ) {
                    Text("Actualizar", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onSave(username, email, document) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraSalmon)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Link
            Text(
                text = "¿Has olvidado tu contraseña ?",
                color = SyntraBlue,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onForgotPassword() },
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            // Cerrar sesión
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SyntraBlue)
            ) {
                Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
