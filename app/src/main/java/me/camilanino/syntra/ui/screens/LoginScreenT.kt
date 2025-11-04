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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.text.KeyboardOptions as FKeyboardOptions

/* ====== PALETA ====== */
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

/* ====== LOGIN DE TRÁNSITO CON SESIÓN PERSISTENTE ====== */
@Composable
fun LoginScreenT(
    onForgotPassword: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    onLoginSuccess: ((agentDoc: Map<String, Any?>) -> Unit)? = null
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // 🔸 Chequeo de sesión persistente
    var isChecking by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        val current = auth.currentUser
        if (current != null) {
            val uid = current.uid
            db.collection("transito").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        onLoginSuccess?.invoke(doc.data ?: emptyMap())
                    } else {
                        auth.signOut() // no pertenece a tránsito
                        isChecking = false
                    }
                }
                .addOnFailureListener { isChecking = false }
        } else {
            isChecking = false
        }
    }

    if (isChecking) {
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
        HeaderWithWave(
            title = "Ingreso Agentes de Tránsito",
            subtitle = "Usa tu correo institucional y contraseña",
            waveHeightDp = 64
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
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
                    label = "Correo institucional",
                    placeholder = "agente@institucion.gov",
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = Icons.Outlined.Email,
                    isPassword = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(Modifier.height(14.dp))

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
                        modifier = Modifier.clickable { onForgotPassword(email) }
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
                                    val uid = auth.currentUser!!.uid
                                    db.collection("transito").document(uid).get()
                                        .addOnSuccessListener { doc ->
                                            if (doc.exists()) {
                                                status = "Ingreso correcto"
                                                loading = false
                                                onLoginSuccess?.invoke(doc.data ?: emptyMap())
                                            } else {
                                                status = "Tu cuenta no está habilitada como agente"
                                                loading = false
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            status = "Error verificando perfil: ${e.message}"
                                            loading = false
                                        }
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
                        "registrarse",
                        color = SyntraBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onRegister() }
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