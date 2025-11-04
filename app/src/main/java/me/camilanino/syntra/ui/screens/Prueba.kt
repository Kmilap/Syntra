package me.camilanino.syntra.ui.screens

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun probarSeparacionFirebase() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.signInWithEmailAndPassword("test@example.com", "123456")
        .addOnSuccessListener { result ->
            val user = result.user ?: return@addOnSuccessListener

            // Guardar información del usuario en /users/{uid}
            val perfil = mapOf(
                "nombre" to "Usuario de prueba",
                "email" to user.email,
                "rol" to "ciudadano"
            )

            db.collection("users").document(user.uid)
                .set(perfil)
                .addOnSuccessListener {
                    Log.d("FIREBASE", "Perfil creado en /users/${user.uid}")
                }

            // Crear un reporte de tránsito asociado al usuario
            val reporte = mapOf(
                "uidCreador" to user.uid,
                "tipo" to "infraccion",
                "descripcion" to "Vehículo mal estacionado",
                "estado" to "abierto"
            )

            db.collection("transito")
                .add(reporte)
                .addOnSuccessListener { doc ->
                    Log.d("FIREBASE", "Reporte creado en /transito/${doc.id}")
                }
        }
        .addOnFailureListener { e ->
            Log.e("FIREBASE", "Error al iniciar sesión: ${e.message}")
        }
}