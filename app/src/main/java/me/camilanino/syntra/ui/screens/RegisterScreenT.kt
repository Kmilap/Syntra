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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.text.KeyboardOptions as FKeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

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
    title: String = "Registrarse (Tránsito)",
    subtitle: String = "Crea tu cuenta de agente de tránsito",
    waveHeightDp: Int = 56,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SyntraSalmon)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
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


/* ====== PANTALLA REGISTRO (TRÁNSITO) CON FIREBASE ====== */
@Composable
fun RegisterScreenT(
    onLoginNavigate: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") } // cédula
    var plate by remember { mutableStateOf("") }    // "username" en tu UI original, pero aquí es placa
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        HeaderWithWaveRegister(
            onBackClick = { onLoginNavigate() }
        )

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
                LabeledTextField(
                    label = "Correo Institucional",
                    placeholder = "agente@institucion.gov",
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SyntraGray) },
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(Modifier.height(14.dp))

                LabeledTextField(
                    label = "Número de documento",
                    placeholder = "0000000000",
                    value = document,
                    onValueChange = { document = it },
                    leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = SyntraGray) },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )

                Spacer(Modifier.height(14.dp))

                LabeledTextField(
                    label = "Placa",
                    placeholder = "ABC123",
                    value = plate,
                    onValueChange = { plate = it },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = SyntraGray) },
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

                Spacer(Modifier.height(26.dp))

                Button(
                    onClick = {
                        loading = true
                        if (email.isBlank() || password.length < 6) {
                            status = "Email o contraseña inválidos"
                            loading = false
                        } else {
                            // 1) Crear cuenta de agente en Firebase Auth
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    val uid = auth.currentUser!!.uid

                                    // 2) Guardar perfil de agente en /transito/{uid}
                                    val agente = mapOf(
                                        "email" to email,
                                        "document" to document,
                                        "placa" to plate,
                                        "rol" to "agente",
                                        "creado" to System.currentTimeMillis()
                                    )

                                    db.collection("transito").document(uid).set(agente)
                                        .addOnSuccessListener {
                                            status = "Agente registrado correctamente"
                                            loading = false


                                            auth.signOut()          // opcional: forzar login limpio
                                            onLoginNavigate()
                                        }
                                        .addOnFailureListener { e ->
                                            status = "Error guardando perfil de agente: ${e.message}"
                                            loading = false
                                        }
                                }
                                .addOnFailureListener { e ->
                                    status = "Error creando cuenta: ${e.message}"
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

