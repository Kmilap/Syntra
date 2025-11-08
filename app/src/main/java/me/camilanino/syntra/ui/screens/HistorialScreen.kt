package me.camilanino.syntra.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import me.camilanino.syntra.R
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import me.camilanino.syntra.ui.screens.ReportRepository
import me.camilanino.syntra.ui.screens.ReportesUiModel

/* ====== Tipografías ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

/* ====== Colores ====== */
private val SyntraBlue = Color(0xFF4D81E7)
private val SyntraSalmon = Color(0xFFE74C3C)
private val SyntraWhite = Color(0xFFF1F2F8)
private val SyntraGray = Color(0xFF6C7278)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraYellow = Color(0xFFE3C04D)
private val SyntraBackground = Color(0xFFF2F4F7)

/* ====== HISTORIAL ====== */
@Composable
fun HistorialScreen(
    navController: NavController,
    role: String,                     // "usuario" | "agente"
    fromMenu: Boolean = false,
    fromMap: Boolean = false
) {
    val scope = rememberCoroutineScope()

    var tabIndex by remember { mutableStateOf(0) } // 0: Mis reportes | 1: Últimas 24h
    var query by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<ReportesUiModel>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<ReportesUiModel?>(null) }

    fun load() {
        scope.launch {
            loading = true
            val res = if (tabIndex == 0) ReportRepository.getMyReports()
            else ReportRepository.getLast24hReports()
            loading = false
            items = res.getOrElse {
                toast = "Error cargando historial: ${it.message}"
                emptyList()
            }
        }
    }

    LaunchedEffect(tabIndex) { load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(36.dp))
        TopBar(navController, role, fromMenu, fromMap)

        Spacer(Modifier.height(12.dp))
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.Transparent,
            indicator = {}
        ) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                Text("Mis reportes", modifier = Modifier.padding(12.dp), fontFamily = SfPro)
            }
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                Text("Últimas 24h", modifier = Modifier.padding(12.dp), fontFamily = SfPro)
            }
        }

        Spacer(Modifier.height(10.dp))
        SearchBar(query) { query = it }
        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SyntraBlue)
            }
        } else {
            val filtered = items.filter {
                it.address.contains(query, true) ||
                        it.status.contains(query, true) ||
                        (it.description ?: "").contains(query, true)
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(bottom = 28.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered, key = { it.id }) { r ->
                    ReportCard(
                        reporte = r,
                        role = role,
                        onClick = { selected = r },
                        onChangeStatus = { newUi ->
                            if (role != "agente") return@ReportCard
                            scope.launch {
                                val ok = ReportRepository.updateReportStatus(r.id, newUi)
                                if (ok.isFailure) toast = "No se pudo actualizar"
                                load()
                            }
                        },
                        onDelete = { id ->
                            // 🔴 Borra inmediatamente al tocar el ícono de basura
                            scope.launch {
                                toast = null
                                val res = ReportRepository.deleteReport(id)
                                if (res.isSuccess) {
                                    toast = "Reporte eliminado"
                                    load()
                                } else {
                                    toast = "No se pudo eliminar: ${res.exceptionOrNull()?.message}"
                                }
                            }
                        }
                    )
                }
            }
        }

        toast?.let { Text(it, color = SyntraGray, fontSize = 12.sp) }
        Spacer(Modifier.height(12.dp))
    }

// Popup Detalle
    selected?.let { r ->
        ReportDialog(
            reporte = r,
            role = role,
            onDismiss = { selected = null },
            onChangeStatus = { newUi ->
                if (role == "agente") {
                    scope.launch {
                        val res = ReportRepository.updateReportStatus(r.id, newUi)
                        selected = null
                        // Vuelve a cargar la lista
                        // (reusa tu función load() del scope de pantalla)
                        // Ojo: si 'load()' está adentro, llámala aquí:
                        // load()
                    }
                }
            }
        )
    }
}

/* ====== Top Bar ====== */
@Composable
private fun TopBar(
    navController: NavController,
    role: String,
    fromMenu: Boolean,
    fromMap: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Volver",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        when {
                            fromMap -> navController.navigate("mapa_screen/$role?fromMenu=true") {
                                popUpTo("history_screen/$role") { inclusive = true }
                            }
                            fromMenu -> {
                                val dest = if (role == "usuario") "menu_user" else "menu_transito"
                                navController.navigate(dest) {
                                    popUpTo("history_screen/$role") { inclusive = true }
                                }
                            }
                            else -> navController.navigate("main_page/$role") {
                                popUpTo("history_screen/$role") { inclusive = true }
                            }
                        }
                    }
            )
            Spacer(Modifier.width(12.dp))
            Text("Historial", color = Color.Black, fontSize = 23.sp, fontWeight = FontWeight.Bold, fontFamily = SfPro)
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = "Menú",
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
    }
}

/* ====== Search ====== */
@Composable
private fun SearchBar(value: String, onChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text("Buscar", color = Color(0xFF9C9C9C), fontFamily = SfPro) },
        singleLine = true,
        shape = RoundedCornerShape(50.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                tint = Color(0xFF9C9C9C)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color(0xFFDADADA), RoundedCornerShape(50.dp))
    )
}

/* ====== Tarjeta de reporte ====== */
@Composable
private fun ReportCard(
    reporte: ReportesUiModel,
    role: String,
    onClick: () -> Unit,
    onChangeStatus: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha = animateFloatAsState(if (visible) 1f else 0f, label = "fade")
    LaunchedEffect(Unit) { visible = true }

    val (chipBg, chipFg, estadoUi) = when (reporte.status) {
        "operativo"  -> Triple(SyntraGreen.copy(alpha = 0.20f), SyntraGreen, "Operativo")
        "inspeccion" -> Triple(SyntraYellow.copy(alpha = 0.20f), SyntraYellow, "Inspección")
        else         -> Triple(SyntraSalmon.copy(alpha = 0.20f), SyntraSalmon, "Falla crítico")
    }

    Box(
        modifier = Modifier
            .graphicsLayer(alpha = alpha.value)
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp))
            .background(Color.White, RoundedCornerShape(28.dp))
            .border(0.6.dp, Color(0xFFE6E6E6), RoundedCornerShape(28.dp))
            .padding(horizontal = 22.dp, vertical = 22.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ===== IZQUIERDA (elástica) =====
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_traffic_card),
                    contentDescription = "Semáforo",
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reporte.address,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        fontFamily = SfPro,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Actualizado: ${
                            reporte.updatedAtMillis?.let {
                                android.text.format.DateUtils.getRelativeTimeSpanString(it)
                            } ?: "—"
                        }",
                        color = SyntraGray.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontFamily = SfPro
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // ===== DERECHA (ancho controlado) =====
            Row(
                modifier = Modifier.widthIn(min = 150.dp, max = 210.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (role == "agente") {
                    // Basura con área táctil estable (no se achica)
                    IconButton(
                        onClick = { onDelete(reporte.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = SyntraSalmon,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        AssistChip(
                            onClick = { expanded = true },
                            label = {
                                Text(
                                    estadoUi,
                                    color = chipFg,
                                    fontFamily = SfProRounded,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.widthIn(max = 140.dp), // evita empujar la basura
                            colors = AssistChipDefaults.assistChipColors(containerColor = chipBg)
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("Operativo") },
                                onClick = { expanded = false; onChangeStatus("Operativo") })
                            DropdownMenuItem(text = { Text("Inspección") },
                                onClick = { expanded = false; onChangeStatus("Inspección") })
                            DropdownMenuItem(text = { Text("Falla crítico") },
                                onClick = { expanded = false; onChangeStatus("Falla crítico") })
                        }
                    }
                } else {
                    // Usuario: solo chip (limitado)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(chipBg)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .widthIn(max = 160.dp)
                    ) {
                        Text(
                            estadoUi,
                            color = chipFg,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = SfProRounded,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/* ====== Dialog Detalle ====== */
@Composable
private fun ReportDialog(
    reporte: ReportesUiModel,
    role: String,
    onDismiss: () -> Unit,
    onChangeStatus: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.background(Color.White).padding(20.dp)
            ) {
                Text(reporte.address, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black, fontFamily = SfPro)

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Actualizado ${
                        reporte.updatedAtMillis?.let { android.text.format.DateUtils.getRelativeTimeSpanString(it) } ?: "recientemente"
                    }",
                    color = SyntraGray.copy(alpha = 0.7f), fontSize = 13.sp, fontFamily = SfPro
                )

                Spacer(Modifier.height(14.dp))

                if (role == "agente") {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        AssistChip(
                            onClick = { expanded = true },
                            label = {
                                Text(
                                    when (reporte.status) {
                                        "operativo" -> "Operativo"
                                        "inspeccion" -> "Inspección"
                                        else -> "Falla crítico"
                                    },
                                    fontFamily = SfProRounded
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFECECEC))
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Operativo", "Inspección", "Falla crítico").forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = { expanded = false; onChangeStatus(it) })
                            }
                        }
                    }
                } else {
                    val (chipBg, chipFg) = when (reporte.status) {
                        "operativo"  -> SyntraGreen.copy(alpha = 0.2f) to SyntraGreen
                        "inspeccion" -> SyntraYellow.copy(alpha = 0.2f) to SyntraYellow
                        else         -> SyntraSalmon.copy(alpha = 0.2f) to SyntraSalmon
                    }
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(chipBg).padding(horizontal = 16.dp, vertical = 8.dp)
                    ) { Text( when (reporte.status) {
                        "operativo"->"Operativo"; "inspeccion"->"Inspección"; else->"Falla crítico"
                    }, color = chipFg, fontFamily = SfProRounded )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(reporte.description ?: "(Sin descripción)", color = Color.Black, fontSize = 15.sp, lineHeight = 20.sp, fontFamily = SfPro)

                if (reporte.photoUrls.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text("Fotos del reporte:", color = SyntraGray, fontWeight = FontWeight.SemiBold, fontFamily = SfPro)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(reporte.photoUrls) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, SyntraGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraBlue)) {
                    Text("Cerrar", color = Color.White)
                }
            }
        }
    }
}
