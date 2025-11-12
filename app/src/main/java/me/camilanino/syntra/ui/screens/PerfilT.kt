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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/* ====== PALETA ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xD9E74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)
private val SyntraGreen  = Color(0xFF63B58D)
private val SyntraGreenDark = Color(0xFF33B06B)

/* ====== TextField reutilizable ====== */
@Composable
private fun LabeledTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit),
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
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
            enabled = enabled,
            leadingIcon = leadingIcon,
            placeholder = { Text(placeholder, color = SyntraGray.copy(alpha = 0.7f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SyntraBlue,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF5F5F5),
                cursorColor = SyntraGreen
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            )
        )
    }
}

/* ====== Avatar ====== */
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
                imageVector = Icons.Outlined.Badge,
                contentDescription = null,
                tint = SyntraGray,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

/* ====== Header ====== */
@Composable
private fun BigHeader(
    title: String = "Perfil tránsito",
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(SyntraGreen)
    ) {
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

        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        )

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
            drawPath(path, SyntraGreenDark)
        }
    }
}

/* ====== PERFIL DE TRÁNSITO CON FIREBASE ====== */
@Composable
fun ProfileScreenTransito(
    navController: NavController,
    onForgotPassword: () -> Unit = {},
    onLogout: () -> Unit = {},
    fromMenu: Boolean = false,
    fromChatbot: Boolean = false
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid
    val scope = rememberCoroutineScope()

    var placa by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    // Cargar perfil desde Firestore
    LaunchedEffect(Unit) {
        if (uid == null) {
            message = "No hay sesión activa"
            loading = false
            return@LaunchedEffect
        }
        try {
            val snap = db.collection("transito").document(uid).get().await()
            placa = snap.getString("placa") ?: snap.getString("username") ?: ""
            email = snap.getString("email") ?: auth.currentUser?.email.orEmpty()
            document = snap.getString("document") ?: ""
        } catch (e: Exception) {
            message = "Error al cargar perfil: ${e.message}"
        } finally {
            loading = false
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SyntraBlue)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        BigHeader(
            title = "Perfil tránsito",
            onBack = {
                when {
                    fromChatbot -> navController.navigate("chatbot_screen/agente?fromMenu=false")
                    fromMenu -> navController.navigate("menu_transito")
                    else -> navController.navigate("main_page/agente")
                }
            }
        )



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar()
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (placa.isNotEmpty()) placa else "Agente",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )
            }

            Spacer(Modifier.height(18.dp))

            // Campo placa
            LabeledTextField(
                label = "Usuario (placa)",
                placeholder = "ABC123",
                value = placa,
                onValueChange = { placa = it.uppercase() },
                leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = SyntraGray) }
            )

            Spacer(Modifier.height(14.dp))

            // Correo (solo lectura)
            LabeledTextField(
                label = "Correo institucional",
                placeholder = "agente@institucion.gov",
                value = email,
                onValueChange = { },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SyntraGray) },
                enabled = false
            )

            Spacer(Modifier.height(14.dp))

            // Documento
            LabeledTextField(
                label = "Número de documento",
                placeholder = "0000 0000 000",
                value = document,
                onValueChange = { document = it },
                leadingIcon = { Icon(Icons.Outlined.AssignmentInd, contentDescription = null, tint = SyntraGray) },
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { message = "Datos listos para actualizar." },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SyntraGreen),
                    border = BorderStroke(1.dp, SyntraGreen)
                ) {
                    Text("Actualizar", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (uid == null) {
                            message = "No hay sesión activa."
                            return@Button
                        }
                        scope.launch {
                            try {
                                val data = mapOf(
                                    "placa" to placa,
                                    "email" to email,
                                    "document" to document,
                                    "rol" to "agente"
                                )
                                db.collection("transito").document(uid).set(data).await()
                                message = "Perfil de tránsito guardado."
                            } catch (e: Exception) {
                                message = "Error al guardar: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraGreen)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "¿Has olvidado tu contraseña?",
                color = SyntraBlue,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onForgotPassword() },
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SyntraBlue)
            ) {
                Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            message?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = if (it.contains("Error")) Color(0xFFB00020) else Color(0xFF008000))
            }
        }
    }
}