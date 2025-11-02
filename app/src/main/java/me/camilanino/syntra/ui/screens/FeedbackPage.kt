package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import kotlinx.coroutines.launch
import me.camilanino.syntra.R

/* ====== FUENTES (igual que en MainPage) ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))

/* ====== PALETA SYNTRA (idéntica a tu MainPage) ====== */
private val SyntraBlue     = Color(0xFF4D81E7)
private val SyntraSalmon   = Color(0xFFE74C3C)
private val SyntraWhite    = Color(0xFFF1F2F8)
private val SyntraGray     = Color(0xFF6C7278)
private val SyntraGreen    = Color(0xFF63B58D)
private val SyntraDarkBlue = Color(0xFF273746)

/* =================================================================================
 * PANTALLA FEEDBACK (con borrar + deshacer)
 * ================================================================================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackPage(
    onBack: () -> Unit = {},
    // Si quieres usar tu BottomNavBar() original, cambia esta línea a: bottomBar = { BottomNavBar() }
    bottomBar: @Composable () -> Unit = { FeedbackBottomNavBar() }
) {
    var comentarios by remember { mutableStateOf(listOf<String>()) }
    var nuevo by remember { mutableStateOf("") }

    // Snackbar para “Deshacer”
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Feedback",
                        fontFamily = SfPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SyntraWhite
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SyntraWhite)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Campo para agregar comentario
            Text(
                text = "Agregar comentario",
                fontFamily = SfPro,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nuevo,
                onValueChange = { nuevo = it },
                placeholder = { Text("Escribe tu comentario…", fontFamily = SfPro, color = SyntraGray) },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (nuevo.isNotBlank()) {
                            comentarios = comentarios + nuevo.trim()
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

            // Lista de comentarios
            Text(
                text = "Comentarios",
                fontFamily = SfPro,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = SyntraGray
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(comentarios) { index, c ->
                    CommentCard(
                        texto = c,
                        onDelete = {
                            // Borrar con opción de deshacer
                            val eliminado = comentarios[index]
                            val listaAnterior = comentarios
                            comentarios = comentarios.toMutableList().also { it.removeAt(index) }

                            scope.launch {
                                val res = snackbarHostState.showSnackbar(
                                    message = "Comentario eliminado",
                                    actionLabel = "Deshacer",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                                if (res == SnackbarResult.ActionPerformed) {
                                    // Restaurar
                                    comentarios = listaAnterior
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

/* ====== CARD DE COMENTARIO (gris estilo mock) ====== */
@Composable
private fun CommentCard(
    texto: String,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFD0D0D0) // gris de tus cards
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // avatar círculo blanco
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Spacer(Modifier.width(10.dp))

            // texto
            Text(
                text = texto,
                fontFamily = SfPro,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            // botón eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = SyntraSalmon
                )
            }
        }
    }
}

/* =================================================================================
 * BOTTOM NAV BAR RENOMBRADA (misma estética)
 * ================================================================================= */
@Composable
fun FeedbackBottomNavBar(
    onHome: () -> Unit = {},
    onSearch: () -> Unit = {},
    onCenter: () -> Unit = {},
    onHistory: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
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
                    .padding(2.dp)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = SyntraGray,
                modifier = Modifier
                    .size(26.dp)
                    .padding(2.dp)
            )

            // Botón central “resaltado”
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SyntraDarkBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_traffic_center),
                    contentDescription = "Centro",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_history),
                contentDescription = "History",
                tint = SyntraGray,
                modifier = Modifier
                    .size(26.dp)
                    .padding(2.dp)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                tint = SyntraGray,
                modifier = Modifier
                    .size(26.dp)
                    .padding(2.dp)
            )
        }
    }
}