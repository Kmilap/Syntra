package me.camilanino.syntra.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.camilanino.syntra.R
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding

/* ====== FUENTES Y COLORES ====== */
private val SfProRounded = FontFamily(Font(R.font.sf_pro_rounded_regular))
private val SfPro = FontFamily(Font(R.font.sf_pro))
private val SyntraBlue = Color(0xFF4D81E7)
private val SyntraWhite = Color(0xFFF1F2F8)
private val SyntraGray = Color(0xFF6C7278)
private val SyntraGreen = Color(0xFF63B58D)
private val SyntraLightGray = Color(0xFFE6E6E6)

/* ====== DATA CLASSES ====== */
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val buttons: List<ChatButton>? = null
)

data class ChatButton(
    val label: String,
    val destination: String
)

/* ====== PANTALLA PRINCIPAL ====== */
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatbotScreen(navController: NavController, role: String, fromMenu: Boolean = false) {
    val coroutineScope = rememberCoroutineScope()
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }

    // 🔹 Clave de API ( ApiKeyProvider)
    val context = LocalContext.current
    val apiKey = remember { ApiKeyProvider.getOpenAIKey(context) ?: "" }

    // Mensaje inicial
    LaunchedEffect(role) {
        messages = messages + ChatbotBrain.getWelcomeMessage(role)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SyntraWhite)
            .imePadding()
    ) {
        ChatHeader(navController, role, fromMenu)

        // Lista que ocupa todo el alto disponible
        ChatMessages(
            messages = messages,
            navController = navController,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )


        ChatInputBar(
            text = inputText,
            onTextChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    val userMsg = ChatMessage(text = inputText.trim(), isUser = true)
                    messages = messages + userMsg

                    val userInput = inputText
                    inputText = ""

                    coroutineScope.launch {
                        val botReply = ChatbotBrain.processMessage(
                            userText = userInput,
                            role = role,
                            apiKey = apiKey
                        )
                        messages = messages + botReply
                    }
                }
            }
        )
    }
}

/* ====== ENCABEZADO ====== */
@Composable
fun ChatHeader(navController: NavController, role: String, fromMenu: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "Atrás",
            tint = Color.Black,
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    if (fromMenu) {
                        if (role == "usuario") navController.navigate("menu_user")
                        else navController.navigate("menu_transito")
                    } else {
                        navController.navigate("main_page/$role")
                    }
                }
        )
        Column {
            Text(
                text = "Asistente de Syntra",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SfProRounded
            )
            Text(
                text = "Chat de ayuda y orientación",
                color = SyntraGray.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontFamily = SfPro
            )
        }
    }
}

/* ====== CUERPO DEL CHAT ====== */
@Composable
fun ChatMessages(
    messages: List<ChatMessage>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()


    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .background(SyntraLightGray),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { msg ->
            ChatBubble(msg, navController)
        }
    }
}

/* ====== BURBUJAS CON BOTONES ====== */
@Composable
fun ChatBubble(message: ChatMessage, navController: NavController) {
    val bubbleColor = if (message.isUser) SyntraGreen else Color.LightGray.copy(alpha = 0.85f)
    val boxAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val cornerShape = if (message.isUser)
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    else
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = boxAlignment
    ) {
        Column(
            modifier = Modifier
                .clip(cornerShape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else Color.Black,
                fontSize = 14.sp,
                fontFamily = SfPro,
                lineHeight = 18.sp,
                textAlign = if (message.isUser) TextAlign.End else TextAlign.Start
            )

            message.buttons?.forEach { btn ->
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = { navController.navigate(btn.destination) },
                    colors = ButtonDefaults.buttonColors(containerColor = SyntraBlue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = btn.label,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontFamily = SfPro
                    )
                }
            }
        }
    }
}

/* ====== INPUT BAR (FIJA ABAJO) ====== */
@Composable
fun ChatInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SyntraLightGray)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black,
                    fontFamily = SfPro,
                    fontSize = 15.sp
                ),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "Escribe tu mensaje...",
                            color = SyntraGray.copy(alpha = 0.6f),
                            fontFamily = SfPro,
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Enviar",
                    tint = SyntraBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}