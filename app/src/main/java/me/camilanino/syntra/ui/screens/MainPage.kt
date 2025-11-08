package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.camilanino.syntra.R

/* ====== FUENTES PERSONALIZADAS ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

/* ====== PALETA SINTRA ====== */
private val SyntraBlue   = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xFFE74C3C)
private val SyntraWhite  = Color(0xFFF1F2F8)
private val SyntraGray   = Color(0xFF6C7278)
private val SyntraGreen  = Color(0xFF63B58D)
private val SyntraDarkBlue = Color(0xFF273746)

/* ====== MAIN PAGE ====== */
@Composable
fun MainPage(navController: NavController, role: String) {
    var userRole by remember { mutableStateOf("usuario") } // por defecto

    // 🔹 Detectar el rol desde Firestore
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        uid?.let {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userRole = "usuario"
                    } else {
                        db.collection("transito").document(uid).get()
                            .addOnSuccessListener { agentDoc ->
                                if (agentDoc.exists()) userRole = "agente"
                            }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
    ) {
        TopSection()
        Spacer(Modifier.height(26.dp))
        MainContent(navController)
        Spacer(Modifier.height(36.dp))
        ReportSummary()   // 🔹 ahora dinámico
        Spacer(modifier = Modifier.weight(1f))
        BottomNavBar(navController, userRole)
    }
}

/* ====== HEADER SUPERIOR ====== */
@Composable
fun TopSection() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var displayName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        displayName = doc.getString("username") ?: "Usuario"
                    } else {
                        db.collection("transito").document(uid).get()
                            .addOnSuccessListener { agentDoc ->
                                displayName = if (agentDoc.exists()) "Agente" else "Usuario"
                            }
                    }
                }
        } else {
            displayName = "Usuario"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Syntra",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SfPro
            )

            Image(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = "Notificación",
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Hola, ${displayName ?: "..."}",
            color = SyntraGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            fontFamily = SfPro
        )
    }
}

/* ====== CONTENIDO CENTRAL ====== */
@Composable
fun MainContent(navController: NavController) {
    var userRole by remember { mutableStateOf("usuario") }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        uid?.let {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userRole = "usuario"
                    } else {
                        db.collection("transito").document(uid).get()
                            .addOnSuccessListener { agentDoc ->
                                if (agentDoc.exists()) userRole = "agente"
                            }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_traffic_light),
            contentDescription = "Semáforo",
            modifier = Modifier
                .width(210.dp)
                .height(250.dp)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Avista y reporta\nsemáforos averiados",
            fontFamily = SfProRounded,
            fontWeight = FontWeight.Medium,
            fontSize = 32.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Supervisa el estado de los semáforos de tu ciudad en tiempo real",
            fontFamily = SfPro,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = SyntraGray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(26.dp))

        Button(
            onClick = { navController.navigate("report_screen/$userRole") },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SyntraGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_report),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Reportar falla",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SfPro
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.navigate("history_screen/$userRole") },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.8.dp, Color.Black.copy(alpha = 0.8f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_list),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Ver reportes",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SfPro
                )
            }
        }
    }
}

/* ====== RESUMEN EN TIEMPO REAL ====== */
@Composable
fun ReportSummary() {
    var active by remember { mutableStateOf(0) }
    var inspection by remember { mutableStateOf(0) }
    var critical by remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val listener = db.collection("reports")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    error = e.message
                    loading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    var countInsp = 0
                    var countCrit = 0
                    for (doc in snapshot.documents) {
                        when (doc.getString("status")) {
                            "inspeccion" -> countInsp++
                            "falla_critica" -> countCrit++
                        }
                    }
                    inspection = countInsp
                    critical = countCrit
                    active = countInsp + countCrit
                    loading = false
                    error = null
                }
            }

        onDispose { listener.remove() }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFE6E6E6),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumen actual de reportes",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                fontFamily = SfPro
            )

            Spacer(Modifier.height(14.dp))

            when {
                loading -> CircularProgressIndicator(color = SyntraGreen, strokeWidth = 3.dp)
                error != null -> Text(
                    text = "Error: $error",
                    color = SyntraSalmon,
                    fontSize = 12.sp,
                    fontFamily = SfPro
                )

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryItem("Activos", active.toString(), showDot = true)
                        SummaryItem("En inspección", inspection.toString())
                        SummaryItem("Falla crítica", critical.toString())
                    }
                }
            }
        }
    }
}

/* ====== COMPONENTE DE ITEM DE RESUMEN ====== */
@Composable
fun SummaryItem(label: String, value: String, showDot: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showDot) {
                Canvas(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 4.dp)
                ) { drawCircle(color = SyntraGreen, style = Fill) }
            }
            Text(text = label, color = SyntraGray, fontSize = 13.sp, fontFamily = SfPro)
        }
        Text(
            text = value,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = SfPro
        )
    }
}

/* ====== BARRA INFERIOR ====== */
@Composable
fun BottomNavBar(navController: NavController, role: String) {
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
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
                tint = Color.Black,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { navController.navigate("main_page/$role") }
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = SyntraGray,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        if (role == "usuario") navController.navigate("menu_user")
                        else navController.navigate("menu_transito")
                    }
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SyntraDarkBlue)
                    .padding(8.dp)
                    .clickable { navController.navigate("chatbot_screen/$role") },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_traffic_center),
                    contentDescription = "Chatbot",
                    modifier = Modifier.size(70.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_history),
                contentDescription = "History",
                tint = SyntraGray,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { navController.navigate("history_screen/$role") }
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                tint = SyntraGray,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        if (role == "usuario") navController.navigate("profile_user")
                        else navController.navigate("profile_transito")
                    }
            )
        }
    }
}
