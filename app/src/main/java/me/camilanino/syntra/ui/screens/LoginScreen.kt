package me.camilanino.syntra.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.text.KeyboardOptions as FKeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

/* ====== Paleta ====== */
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

/* ====== Header simple ====== */
@Composable
private fun LoginHeader(
    title: String = "Ingresa en tu cuenta",
    subtitle: String = "Introduce tu email y contraseña para iniciar sesión",
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SyntraSalmon)
            .padding(top = 60.dp, bottom = 40.dp, start = 24.dp, end = 24.dp)
    ) {
        // Flecha de retroceso
        IconButton(
            onClick = { onBackClick?.invoke() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        // Títulos centrados
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
    }
}


/* ====== Pantalla Login con Firebase (auto-skip si hay sesión) ====== */
@Composable
fun LoginScreen(
    onRegisterClick: (() -> Unit)? = null,
    onForgotPassword: ((String) -> Unit)? = null,
    onLoginSuccess: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // --- NUEVO: chequeo de sesión persistida ---
    var isChecking by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        val current = auth.currentUser
        if (current != null) {
            // Ya hay sesión -> entra directo
            onLoginSuccess?.invoke()
        } else {
            isChecking = false
        }
    }

    if (isChecking) {
        // Loader breve mientras se verifica la sesión persistida
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SyntraBlue)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        LoginHeader(
            onBackClick = { onBackClick?.invoke() }
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 150.dp),
            color = SyntraWhite,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                LabeledTextField(
                    label = "Correo",
                    placeholder = "usuario@correo.com",
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SyntraGray) },
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(Modifier.height(14.dp))

                LabeledTextField(
                    label = "Contraseña",
                    placeholder = "••••••••",
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SyntraGray) },
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
                        "¿Olvidaste tu contraseña?",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            onForgotPassword?.invoke(email)
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        loading = true
                        if (email.isBlank() || password.length < 6) {
                            status = "Correo o contraseña inválidos"
                            loading = false
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    // Guarda el estado del checkbox aquí
                                    prefs.edit().putBoolean("remember_me", rememberMe).apply()

                                    status = "Inicio de sesión correcto"
                                    loading = false
                                    onLoginSuccess?.invoke()
                                }
                                .addOnFailureListener { e ->
                                    status = "No se pudo iniciar sesión: ${e.message}"
                                    loading = false
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraSalmon),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SyntraBlue)
                ) {
                    Text("Ingresar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("¿No tienes una cuenta? ", color = SyntraGray)
                    Text(
                        "Registrarse",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onRegisterClick?.invoke() }
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                status?.let {
                    Text(
                        text = it,
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}