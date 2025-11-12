package me.camilanino.syntra.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "splash") {

        // === SPLASH ===
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    nav.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // === WELCOME ===
        composable("welcome") {
            WelcomeScreen(
                onLoginUser = { nav.navigate("login_user") },
                onLoginTransito = { nav.navigate("login_transito") }
            )
        }

        // === LOGIN USUARIO ===
        composable("login_user") {
            LoginScreen(
                onRegisterClick = { nav.navigate("register_user") },
                onForgotPassword = { email ->
                    nav.navigate("verify_password")
                },
                onLoginSuccess = {
                    // 🔹 Navegamos pasando el rol de usuario
                    nav.navigate("main_page/usuario") {
                        popUpTo("login_user") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack("welcome", inclusive = false)
                }
            )
        }

        // === VERIFICAR CONTRASEÑA (REAL) ===
        composable(
            route = "verify_password?fromProfile={fromProfile}",
            arguments = listOf(navArgument("fromProfile") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val fromProfile = backStackEntry.arguments?.getBoolean("fromProfile") ?: false

            VerificationScreen(
                onSendReset = { email ->
                    try {
                        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                        auth.sendPasswordResetEmail(email).await()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                },
                onAfterSend = {
                    // Mantén lo que ya hace
                    if (!fromProfile) {
                        nav.navigate("login_user") {
                            popUpTo("verify_password") { inclusive = true }
                        }
                    } else {
                        nav.navigate("profile_user") {
                            popUpTo("verify_password") { inclusive = true }
                        }
                    }
                },
                onBackClick = {
                    if (!fromProfile) {
                        nav.popBackStack("login_user", inclusive = false)
                    } else {
                        nav.popBackStack("profile_user", inclusive = false)
                    }
                }
            )
        }

        // === REGISTRO USUARIO (REAL) ===
        composable(route = "register_user") {
            RegisterScreen(
                onLoginNavigate = {
                    nav.navigate("login_user") {
                        popUpTo("register_user") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack("login_user", inclusive = false)
                }
            )
        }

        // === LOGIN TRÁNSITO ===
        composable("login_transito") {
            LoginScreenT(
                onForgotPassword = { email ->
                    nav.navigate("verify_password_t")
                },
                onRegister = {
                    nav.navigate("register_transito")
                },
                onLoginSuccess = {
                    // 🔹 Navegamos pasando el rol de agente
                    nav.navigate("main_page/agente") {
                        popUpTo("login_transito") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack("welcome", inclusive = false)
                }
            )
        }

        // === VERIFICAR CONTRASEÑA (TRÁNSITO) ===
        composable(
            route = "verify_password_t?fromProfile={fromProfile}",
            arguments = listOf(navArgument("fromProfile") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val fromProfile = backStackEntry.arguments?.getBoolean("fromProfile") ?: false

            VerificationScreen(
                onSendReset = { email ->
                    try {
                        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                        auth.sendPasswordResetEmail(email).await()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                },
                onAfterSend = {
                    if (!fromProfile) {
                        nav.navigate("login_transito") {
                            popUpTo("verify_password_t") { inclusive = true }
                        }
                    } else {
                        nav.navigate("profile_transito") {
                            popUpTo("verify_password_t") { inclusive = true }
                        }
                    }
                },
                onBackClick = {
                    if (!fromProfile) {
                        nav.popBackStack("login_transito", inclusive = false)
                    } else {
                        nav.popBackStack("profile_transito", inclusive = false)
                    }
                }
            )
        }

        // === REGISTRO TRÁNSITO (REAL) ===
        composable("register_transito") {
            RegisterScreenT(
                onLoginNavigate = {
                    nav.navigate("login_transito") {
                        popUpTo("register_transito") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack("login_transito", inclusive = false)
                }
            )
        }

        // === MAIN PAGE (COMPARTIDA) ===
        composable(
            route = "main_page/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            MainPage(navController = nav, role = role)
        }


        // === REPORTES SCREEN (COMPARTIDA) ===
        composable(
            route = "report_screen/{role}?fromMenu={fromMenu}&fromMap={fromMap}&fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType },
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromMap") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromChatbot") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            val fromMap = backStackEntry.arguments?.getBoolean("fromMap") ?: false
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            ReportesScreen(
                navController = nav,
                role = role,
                fromMenu = fromMenu,
                fromMap = fromMap,
                fromChatbot = fromChatbot
            )
        }




        // === HISTORIAL SCREEN (COMPARTIDA) ===
        composable(
            route = "history_screen/{role}?fromMenu={fromMenu}&fromMap={fromMap}&fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType },
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromMap") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromChatbot") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            val fromMap = backStackEntry.arguments?.getBoolean("fromMap") ?: false
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            HistorialScreen(
                navController = nav,
                role = role,
                fromMenu = fromMenu,
                fromMap = fromMap,
                fromChatbot = fromChatbot
            )
        }






        // === PERFIL (USUARIO) ===
        composable(
            route = "profile_user?fromMenu={fromMenu}&fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromChatbot") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            ProfileScreenFirebase(
                navController = nav,
                fromMenu = fromMenu,
                fromChatbot = fromChatbot,
                onForgotPassword = { nav.navigate("verify_password?fromProfile=true") }, // 🔹 CAMBIA AQUÍ
                onLogout = {
                    nav.navigate("welcome") {
                        popUpTo("main_page") { inclusive = true }
                    }
                }
            )
        }

        // === PERFIL (TRÁNSITO) ===
        composable(
            route = "profile_transito?fromMenu={fromMenu}&fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromChatbot") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            ProfileScreenTransito(
                navController = nav,
                fromMenu = fromMenu,
                fromChatbot = fromChatbot,
                onForgotPassword = { nav.navigate("verify_password_t?fromProfile=true") },
                onLogout = {
                    nav.navigate("welcome") {
                        popUpTo("main_page") { inclusive = true }
                    }
                }
            )
        }



        // === CHATBOT (COMPARTIDO) ===
        composable(
            route = "chatbot_screen/{role}?fromMenu={fromMenu}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType },
                navArgument("fromMenu") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            ChatbotScreen(navController = nav, role = role, fromMenu = fromMenu)
        }

        // === MAPA SCREEN (COMPARTIDA) ===
        composable(
            route = "mapa_screen/{role}?fromMenu={fromMenu}&fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType },
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromChatbot") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            MapaScreen(navController = nav, role = role, fromMenu = fromMenu, fromChatbot = fromChatbot)
        }




        // === ESTADÍSTICAS (EXCLUSIVA DE TRÁNSITO) ===
        composable(
            route = "estadisticas_screen/agente?fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("fromChatbot") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            EstadisticasScreen(
                navController = nav,
                fromChatbot = fromChatbot
            )
        }


        // === FEEDBACK SCREEN (COMPARTIDA) ===
        composable(
            route = "feedback_screen/{role}?fromMenu={fromMenu}&fromChatbot={fromChatbot}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType },
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false },
                navArgument("fromChatbot") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            val fromMenu = backStackEntry.arguments?.getBoolean("fromMenu") ?: false
            val fromChatbot = backStackEntry.arguments?.getBoolean("fromChatbot") ?: false

            FeedbackPage(navController = nav, role = role, fromMenu = fromMenu, fromChatbot = fromChatbot)
        }

        // === MAPA SELECCIONAR UBICACIÓN ===
        composable("select_location_screen") {
            SeleccionarUbicacionScreen(navController= nav)
        }

        // === MENÚS ===
        composable("menu_user") {
            Menu(navController = nav) //
        }

        composable("menu_transito") {
            MenuT(navController = nav) //

        }
        // === NOTIFICACIONES (COMPARTIDA) ===
        composable(
            route = "notifications_screen/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "usuario"
            NotificationScreen(navController = nav, role = role)
        }


    }
}
