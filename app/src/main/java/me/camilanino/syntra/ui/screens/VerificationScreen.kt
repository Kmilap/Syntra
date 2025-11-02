package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ====== PALETA (idéntica a tu login) ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xD9E74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)

/* ====== HEADER CON OLA (simplificado, puedes usar el tuyo) ====== */
@Composable
private fun HeaderWithWaveSimple(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SyntraSalmon,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, bottom = 62.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
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

/* ====== TEXT FIELD OTP (un solo campo de 6 dígitos) ====== */
@Composable
private fun OtpField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = { new ->
            // Solo números y hasta 6
            val filtered = new.filter { it.isDigit() }.take(6)
            onValueChange(filtered)
        },
        singleLine = true,
        placeholder = { Text("000000", color = SyntraGray.copy(alpha = 0.7f)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color(0xFFB00020) else SyntraBlue,
            unfocusedBorderColor = SyntraWhite.copy(alpha = 0f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = SyntraBlue,
            errorBorderColor = Color(0xFFB00020),
        ),
        isError = isError,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    )
}

/* =============================================================================
 * VERIFICATION SCREEN
 * ===========================================================================*/
@Composable
fun VerificationScreen(
    emailMasked: String = "tu****@correo.com",
    onVerify: suspend (code: String) -> Result<Unit>, // devuelve success/failure
    onBack: () -> Unit = {},
    onResend: suspend () -> Result<Unit> = { Result.success(Unit) } // opcional
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // countdown para reenviar
    var secondsLeft by remember { mutableStateOf(30) }
    val scope = rememberCoroutineScope()

    // Temporizador de reenvío
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)

    ) {
        HeaderWithWaveSimple(
            title = "Código de verificación",
            subtitle = "A tu correo $emailMasked te enviamos un código para recuperar tu contraseña. Ingrésalo a continuación."

        )

        // CARD blanca
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-28).dp) // para montarla un poco sobre el header
                .wrapContentHeight(),
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
                Text(
                    text = "Código verificación",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))

                OtpField(value = code, onValueChange = { code = it }, isError = errorMsg != null)

                if (errorMsg != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = Color(0xFFB00020),
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Botón verificar
                Button(
                    onClick = {
                        if (code.length == 6 && !isLoading) {
                            isLoading = true
                            errorMsg = null
                            scope.launch {
                                val res = onVerify(code)
                                isLoading = false
                                if (res.isSuccess) {
                                    // éxito: deja que el caller navegue
                                } else {
                                    errorMsg = res.exceptionOrNull()?.message ?: "Código inválido o expirado."
                                }
                            }
                        }
                    },
                    enabled = code.length == 6 && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraSalmon),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SyntraBlue)
                )
                {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text("Verificar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(12.dp))

                // Reenviar código
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No recibiste el código?",
                        color = SyntraGray
                    )
                    val canResend = secondsLeft == 0 && !isLoading
                    TextButton(
                        onClick = {
                            if (!canResend) return@TextButton
                            isLoading = true
                            errorMsg = null
                            scope.launch {
                                val res = onResend()
                                isLoading = false
                                secondsLeft = 30
                                if (res.isFailure) {
                                    errorMsg = res.exceptionOrNull()?.message ?: "No se pudo reenviar el código."
                                }
                            }
                        },
                        enabled = canResend
                    ) {
                        Text(
                            if (canResend) "Reenviar código"
                            else "Reenviar en ${"%02d".format(secondsLeft)} s",
                            color = if (canResend) SyntraBlue else SyntraGray
                        )
                    }
                }
            }
        }

        // Espacio inferior para evitar choque con teclado
        Spacer(Modifier.weight(1f))
    }
}