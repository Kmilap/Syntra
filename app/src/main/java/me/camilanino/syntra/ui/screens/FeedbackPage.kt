package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import me.camilanino.syntra.R
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

/* ====== FUENTES ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

/* ====== PALETA ====== */
private val SyntraBlue = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xFFE74C3C)
private val SyntraWhite = Color(0xFFF1F2F8)
private val SyntraGray = Color(0xFF6C7278)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraDarkBlue = Color(0xFF273746)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackPage(
    navController: NavController,
    role: String,
    fromMenu: Boolean = false,
    fromChatbot: Boolean = false,
    bottomBar: @Composable () -> Unit = { FeedbackBottomNavBar() }
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scope = rememberCoroutineScope()

    var nuevo by remember { mutableStateOf("") }
    var feedbackList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var username by remember { mutableStateOf("Usuario") }
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔹 Obtener username desde Firestore una sola vez
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            val userDoc = db.collection("users").document(uid).get().await()
            val fetchedName = userDoc.getString("username")
            if (!fetchedName.isNullOrBlank()) username = fetchedName
        }
    }

    // 🔹 Escucha activa de comentarios
    LaunchedEffect(Unit) {
        db.collection("feedback")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e == null && snapshots != null) {
                    feedbackList = snapshots.documents.map { doc ->
                        doc.data?.plus("id" to doc.id) ?: emptyMap()
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feedback", fontFamily = SfPro, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            fromChatbot -> navController.navigate("chatbot_screen/$role?fromMenu=false")
                            fromMenu -> {
                                if (role == "usuario") navController.navigate("menu_user")
                                else navController.navigate("menu_transito")
                            }
                            else -> navController.navigate("main_page/$role")
                        }
                    }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SyntraWhite)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SyntraWhite)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // ===== CAMPO DE NUEVO COMENTARIO =====
            Text("Agregar comentario", fontFamily = SfPro, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nuevo,
                onValueChange = { nuevo = it },
                placeholder = { Text("Escribe tu comentario…", fontFamily = SfPro, color = SyntraGray) },
                maxLines = 5,
                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        val currentUser = auth.currentUser
                        if (nuevo.isNotBlank() && currentUser != null) {
                            val feedbackData = mapOf(
                                "uid" to currentUser.uid,
                                "name" to username,
                                "message" to nuevo.trim(),
                                "createdAt" to FieldValue.serverTimestamp()
                            )
                            db.collection("feedback").add(feedbackData)
                            nuevo = ""
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraGreen),
                    enabled = nuevo.isNotBlank()
                ) {
                    Text("Enviar", fontFamily = SfPro)
                }
            }

            Spacer(Modifier.height(14.dp))

            // ===== LISTA DE COMENTARIOS =====
            Text(
                text = "Comentarios recientes",
                fontFamily = SfPro,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = SyntraGray
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(feedbackList) { fb ->
                    val name = fb["name"] as? String ?: "Usuario"
                    val message = fb["message"] as? String ?: ""
                    val uid = fb["uid"] as? String
                    val firstLetter = name.firstOrNull()?.uppercaseChar() ?: 'U'
                    val canDelete = uid == user?.uid

                    CommentCard(
                        name = name,
                        initial = firstLetter.toString(),
                        texto = message,
                        canDelete = canDelete,
                        onDelete = {
                            fb["id"]?.let { id ->
                                db.collection("feedback").document(id.toString()).delete()
                            }
                        }
                    )
                }
            }
        }
    }
}

/* ====== CARD CON NOMBRE Y AVATAR ====== */
@Composable
private fun CommentCard(
    name: String,
    initial: String,
    texto: String,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFDDE3F3)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Nombre encima
            Text(
                text = name,
                fontFamily = SfPro,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = SyntraBlue
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar con inicial
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(SyntraBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initial, color = Color.White, fontFamily = SfPro, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = texto,
                    fontFamily = SfProRounded,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = SyntraSalmon)
                    }
                }
            }
        }
    }
}

/* ====== NAVBAR ====== */
@Composable
fun FeedbackBottomNavBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home", tint = Color.Black)
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search", tint = SyntraGray)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SyntraDarkBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_traffic_center), contentDescription = "Centro", tint = Color.Unspecified)
            }
            Icon(painter = painterResource(id = R.drawable.ic_history), contentDescription = "History", tint = SyntraGray)
            Icon(painter = painterResource(id = R.drawable.ic_profile), contentDescription = "Profile", tint = SyntraGray)
        }
    }
}
