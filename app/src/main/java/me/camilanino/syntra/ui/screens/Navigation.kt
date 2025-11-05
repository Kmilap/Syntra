package me.camilanino.syntra.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.tasks.await
@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    nav.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

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
                    nav.navigate("main_page_temp") {
                        popUpTo("login_user") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack("welcome", inclusive = false)
                }
            )
        }
        // === VERIFICAR CONTRASEÑA (REAL) ===
        composable("verify_password") {
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
                    // Cuando el correo se envía con éxito, volvemos al Login
                    nav.navigate("login_user") {
                        popUpTo("verify_password") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Si toca la flecha atrás, también volvemos al Login
                    nav.popBackStack("login_user", inclusive = false)
                }
            )
        }
        // === REGISTRO USUARIO (REAL) ===
        composable(route = "register_user") {
            RegisterScreen(
                onLoginNavigate = {
                    // Cuando se registra o toca "¿Ya tienes cuenta?"
                    nav.navigate("login_user") {
                        popUpTo("register_user") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Si toca la flecha atrás, también volvemos al Login
                    nav.popBackStack("login_user", inclusive = false)
                }
            )
        }

        composable("main_page_temp") {
            androidx.compose.material3.Text("Main Page temporal")
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
                    nav.navigate("main_page_t_temp") {
                        popUpTo("login_transito") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack("welcome", inclusive = false)
                }
            )
        }
        // === VERIFICAR CONTRASEÑA (TRÁNSITO) ===
        composable("verify_password_t") {
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
                    // Cuando el correo se envía con éxito, volvemos al Login de Tránsito
                    nav.navigate("login_transito") {
                        popUpTo("verify_password_t") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Si toca la flecha atrás, también volvemos al Login de Tránsito
                    nav.popBackStack("login_transito", inclusive = false)
                }
            )
        }
        // === REGISTRO TRÁNSITO (REAL) ===
        composable("register_transito") {
            RegisterScreenT(
                onLoginNavigate = {
                    // Cuando se registra o toca "¿Ya tienes cuenta?"
                    nav.navigate("login_transito") {
                        popUpTo("register_transito") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Si toca la flecha atrás, también volvemos al Login de Tránsito
                    nav.popBackStack("login_transito", inclusive = false)
                }
            )
        }









// === MAIN PAGE TRÁNSITO (TEMPORAL) ===
        composable("main_page_t_temp") {
            Text("Main Page Tránsito temporal")
        }








    }
}
