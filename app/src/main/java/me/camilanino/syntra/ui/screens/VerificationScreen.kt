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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

/* ====== PALETA ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xD9E74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)

/* ====== HEADER ====== */
@Composable
private fun HeaderVerification(
    title: String,
    subtitle: String = "",
    onBackClick: (() -> Unit)? = null
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = SyntraSalmon) {
        Box {
            // Flecha de retroceso
            IconButton(
                onClick = { onBackClick?.invoke() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp, bottom = 62.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        subtitle,
                        color = Color.White.copy(alpha = 0.94f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


/* =============================================================================
 * VERIFICATION SCREEN
 * ===========================================================================*/
@Composable
fun VerificationScreen(
    initialEmail: String = "",
    onSendReset: suspend (email: String) -> Result<Unit>,
    onUseAnotherMethod: () -> Unit = {},
    onAfterSend: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    var email by remember { mutableStateOf(initialEmail) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    // cooldown de reenvío
    var secondsLeft by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // countdown
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
        HeaderVerification(
            title = "Recuperar contraseña",
            subtitle = "Te enviaremos un enlace a tu correo para restablecer tu contraseña.",
            onBackClick = onBackClick

        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-28).dp),
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
                // Correo editable
                Text("Correo electrónico", color = Color.Black, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        errorMsg = null
                        successMsg = null
                    },
                    singleLine = true,
                    placeholder = { Text("usuario@correo.com", color = SyntraGray.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SyntraBlue,
                        unfocusedBorderColor = SyntraWhite.copy(alpha = 0f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = SyntraBlue
                    ),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                if (errorMsg != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(errorMsg!!, color = Color(0xFFB00020), fontSize = 12.sp)
                }
                if (successMsg != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(successMsg!!, color = Color(0xFF0F9D58), fontSize = 12.sp)
                }

                Spacer(Modifier.height(16.dp))

                // Enviar enlace
                val canSend = email.isNotBlank() && !isLoading && secondsLeft == 0
                Button(
                    onClick = {
                        if (!canSend) return@Button
                        isLoading = true
                        errorMsg = null
                        successMsg = null
                        scope.launch {
                            val res = onSendReset(email)
                            isLoading = false
                            if (res.isSuccess) {
                                successMsg = "Enlace enviado. Revisa tu bandeja de entrada."
                                secondsLeft = 30 // cooldown
                                onAfterSend()
                            } else {
                                errorMsg = res.exceptionOrNull()?.message ?: "No se pudo enviar el correo."
                            }
                        }
                    },
                    enabled = canSend,
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
                    Text(
                        if (secondsLeft == 0) "Enviar enlace"
                        else "Reintentar en ${"%02d".format(secondsLeft)} s",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }


            }
        }

        Spacer(Modifier.weight(1f))
    }
}