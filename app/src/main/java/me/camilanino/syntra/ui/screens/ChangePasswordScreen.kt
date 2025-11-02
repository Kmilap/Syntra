package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/* ====== PALETA (idéntica a tu verificación) ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xD9E74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)

/* ====== Header igual al de verificación ====== */
@Composable
private fun HeaderAuthSimple(title: String, subtitle: String = "") {
    Surface(modifier = Modifier.fillMaxWidth(), color = SyntraSalmon) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, bottom = 62.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center
            )
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.94f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/* ====== TextField de contraseña (con ojo) ====== */
@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction
) {
    var visible by remember { mutableStateOf(false) }

    Text(label, color = Color.Black, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.trimStart()) },
        singleLine = true,
        placeholder = { Text("••••••••", color = SyntraGray.copy(alpha = 0.7f)) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = null,
                    tint = SyntraGray
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SyntraBlue,
            unfocusedBorderColor = SyntraWhite.copy(alpha = 0f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = SyntraBlue
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    )
}

/* =============================================================================
 * PANTALLA CAMBIAR CONTRASEÑA (inspirada en VerificationScreen)
 * ===========================================================================*/
@Composable
fun ChangePasswordScreen(
    minLength: Int = 6,
    onChange: suspend (newPass: String) -> Result<Unit>
) {
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val validLen = pass.length >= minLength
    val matches = pass.isNotEmpty() && pass == confirm
    val canSubmit = validLen && matches && !isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        HeaderAuthSimple(
            title = "Cambiar contraseña",
            subtitle = ""
        )

        // Card blanca flotando como en verificación
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-28).dp)
                .wrapContentHeight()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                PasswordField(
                    label = "Contraseña",
                    value = pass,
                    onValueChange = { pass = it; if (errorMsg != null) errorMsg = null },
                    imeAction = ImeAction.Next
                )

                Spacer(Modifier.height(14.dp))

                PasswordField(
                    label = "Confirmar contraseña",
                    value = confirm,
                    onValueChange = { confirm = it; if (errorMsg != null) errorMsg = null },
                    imeAction = ImeAction.Done
                )

                // Validaciones y errores
                Spacer(Modifier.height(8.dp))
                when {
                    pass.isNotEmpty() && !validLen ->
                        Text("Mínimo $minLength caracteres.", color = Color(0xFFB00020), fontSize = 12.sp)
                    confirm.isNotEmpty() && !matches ->
                        Text("Las contraseñas no coinciden.", color = Color(0xFFB00020), fontSize = 12.sp)
                }
                if (errorMsg != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(errorMsg!!, color = Color(0xFFB00020), fontSize = 12.sp)
                }

                Spacer(Modifier.height(16.dp))

                // Botón igual al de verificación (salmon + borde azul)
                Button(
                    onClick = {
                        if (!canSubmit) return@Button
                        isLoading = true
                        errorMsg = null
                        scope.launch {
                            val res = onChange(pass)
                            isLoading = false
                            if (res.isFailure) {
                                errorMsg = res.exceptionOrNull()?.message
                                    ?: "No se pudo cambiar la contraseña."
                            }
                        }
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraSalmon),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SyntraBlue)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text("Cambiar contraseña", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}