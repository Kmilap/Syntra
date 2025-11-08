package me.camilanino.syntra.ui.screens

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

/* ============================================================
 * MODELOS
 * ============================================================ */

data class ReportesUiModel(
    val id: String = "",
    val reporterUid: String = "",
    val role: String = "",                     // "usuario" | "transito"
    val status: String = "",                   // "operativo" | "inspeccion" | "falla_critica"
    val address: String = "",
    val description: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val photosCount: Long = 0L,
    val photoUrls: List<String> = emptyList(), // URLs directas
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
) {
    val createdAtMillis: Long? get() = createdAt?.toDate()?.time
    val updatedAtMillis: Long? get() = updatedAt?.toDate()?.time
}

data class ReportStats(
    val total: Int = 0,
    val fixed: Int = 0,
    val inspection: Int = 0,
    val urgent: Int = 0,
    val solvedSemester: Int = 0
) {
    val active: Int get() = inspection + urgent
    val fixedPercent: Int get() = if (total > 0) (fixed * 100 / total) else 0
}

/* ============================================================
 * REPOSITORIO
 * ============================================================ */

object ReportRepository {

    private const val TAG = "ReportRepository"
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /* ------------------- Crear + (opcional) subir fotos ------------------- */
    suspend fun createReportAndUploadPhotos(
        address: String,
        lat: Double? = null,
        lng: Double? = null,
        statusUi: String,
        description: String,
        role: String,
        photoUris: List<Uri> = emptyList()
    ): Result<String> {
        val created = createReport(address, lat, lng, statusUi, description, role)
        if (created.isFailure) return created
        val reportId = created.getOrThrow()

        if (photoUris.isNotEmpty()) {
            val up = uploadReportPhotos(reportId, photoUris)
            if (up.isFailure) {
                Log.e(TAG, "Fotos NO subidas: ${up.exceptionOrNull()?.message}", up.exceptionOrNull())
            }
        }
        return Result.success(reportId)
    }

    /* ------------------- Crear reporte ------------------- */
    suspend fun createReport(
        address: String,
        lat: Double? = null,
        lng: Double? = null,
        statusUi: String,
        description: String,
        role: String
    ): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("No hay usuario autenticado"))

        val status = when (statusUi) {
            "Operativo" -> "operativo"
            "Inspección" -> "inspeccion"
            else -> "falla_critica"
        }

        val ref = firestore.collection("reports").document()
        val data = hashMapOf(
            "reporterUid" to uid,
            "role"        to role,
            "status"      to status,
            "address"     to address,
            "description" to description,
            "photoUrls"   to emptyList<String>(),
            "photosCount" to 0L,
            "createdAt"   to FieldValue.serverTimestamp(),
            "updatedAt"   to FieldValue.serverTimestamp()
        )

        if (lat != null && lng != null) {
            data["geo"] = mapOf("lat" to lat, "lng" to lng)
        }

        return try {
            Log.d(TAG, "Creando reporte por uid=$uid …")
            ref.set(data).await()
            Log.d(TAG, "Reporte creado id=${ref.id}")
            Result.success(ref.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creando reporte", e)
            Result.failure(e)
        }
    }

    /* ------------------- Subir foto individual ------------------- */
    suspend fun uploadPhotoToReport(reportId: String, uri: Uri): Result<String> {
        val currentUid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("No hay usuario autenticado"))

        return try {
            val reportSnap = firestore.collection("reports").document(reportId).get().await()
            if (!reportSnap.exists()) {
                return Result.failure(IllegalStateException("El reporte $reportId no existe (aún)."))
            }

            val photoId  = "photo_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val path     = "reports/$reportId/$photoId"
            val photoRef = storage.reference.child(path)

            Log.d(TAG, "Subiendo foto: $path")
            photoRef.putFile(uri).await()
            val downloadUrl = photoRef.downloadUrl.await().toString()
            Log.d(TAG, "Foto subida: $downloadUrl")

            // Subcolección opcional
            val photoDoc = firestore.collection("reports")
                .document(reportId)
                .collection("photos")
                .document(photoId)

            val photoData = mapOf(
                "url"         to downloadUrl,
                "storagePath" to path,
                "uploadedAt"  to FieldValue.serverTimestamp(),
                "uploaderUid" to currentUid
            )
            photoDoc.set(photoData).await()

            // Actualizar documento principal
            firestore.collection("reports").document(reportId)
                .update(
                    mapOf(
                        "photosCount" to FieldValue.increment(1),
                        "photoUrls"   to FieldValue.arrayUnion(downloadUrl),
                        "updatedAt"   to FieldValue.serverTimestamp()
                    )
                ).await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error subiendo foto a $reportId", e)
            Result.failure(e)
        }
    }

    /* ------------------- Subir varias fotos ------------------- */
    suspend fun uploadReportPhotos(reportId: String, uris: List<Uri>): Result<List<String>> {
        if (uris.isEmpty()) return Result.success(emptyList())
        return try {
            val urls = mutableListOf<String>()
            for (u in uris) {
                val r = uploadPhotoToReport(reportId, u)
                if (r.isSuccess) urls += r.getOrThrow()
                else throw r.exceptionOrNull() ?: Exception("Error subiendo una de las fotos")
            }
            Result.success(urls)
        } catch (e: Exception) {
            Log.e(TAG, "Error subiendo lote de fotos", e)
            Result.failure(e)
        }
    }

    /* ------------------- Obtener mis reportes ------------------- */
    suspend fun getMyReports(): Result<List<ReportesUiModel>> = try {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("No hay usuario autenticado"))

        val snap = firestore.collection("reports")
            .whereEqualTo("reporterUid", uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get().await()

        Result.success(snap.documents.map { it.toUi() })
    } catch (e: Exception) {
        Log.e(TAG, "Error getMyReports", e)
        Result.failure(e)
    }

    /* ------------------- Obtener reportes últimos 24h (TODOS) -------------- */
    suspend fun getLast24hReports(): Result<List<ReportesUiModel>> = try {
        val sinceMs = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        val sinceTs = Timestamp(Date(sinceMs))

        val snap = firestore.collection("reports")
            .whereGreaterThan("updatedAt", sinceTs)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get().await()

        Result.success(snap.documents.map { it.toUi() })
    } catch (e: Exception) {
        Log.e(TAG, "Error getLast24hReports", e)
        Result.failure(e)
    }

    /* ------------------- Actualizar estado ------------------- */
    suspend fun updateReportStatus(reportId: String, newStatusUi: String): Result<Unit> {
        val status = when (newStatusUi) {
            "Operativo" -> "operativo"
            "Inspección" -> "inspeccion"
            else -> "falla_critica"
        }
        return try {
            firestore.collection("reports").document(reportId)
                .update(
                    mapOf(
                        "status"    to status,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updateReportStatus", e)
            Result.failure(e)
        }
    }

    /* ------------------- Eliminar reporte (transito o dueño) --------------- */
    suspend fun deleteReport(reportId: String): Result<Unit> = try {
        // 1) Borrar archivos en Storage
        val folderRef = storage.reference.child("reports/$reportId")
        deleteFolderRecursive(folderRef)

        // 2) Borrar subcolección /photos
        val photosColl = firestore.collection("reports").document(reportId).collection("photos")
        var page = photosColl.limit(500).get().await()
        while (!page.isEmpty) {
            val batch = firestore.batch()
            for (doc in page.documents) batch.delete(doc.reference)
            batch.commit().await()
            page = photosColl.limit(500).get().await()
        }

        // 3) Borrar documento del reporte
        firestore.collection("reports").document(reportId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error deleteReport($reportId)", e)
        Result.failure(e)
    }

    private suspend fun deleteFolderRecursive(dir: StorageReference) {
        try {
            val list = dir.listAll().await()
            for (item in list.items) item.delete().await()
            for (prefix in list.prefixes) deleteFolderRecursive(prefix)
        } catch (e: Exception) {
            Log.w(TAG, "deleteFolderRecursive: ${dir.path} -> ${e.message}")
        }
    }

    /* ------------------- Estadísticas para pantalla ------------------- */
    suspend fun getReportStats(): Result<ReportStats> = try {
        val coll = firestore.collection("reports")

        val fixedSnap = coll.whereEqualTo("status", "operativo").get().await()
        val inspectionSnap = coll.whereEqualTo("status", "inspeccion").get().await()
        val urgentSnap = coll.whereEqualTo("status", "falla_critica").get().await()

        val fixed = fixedSnap.size()
        val inspection = inspectionSnap.size()
        val urgent = urgentSnap.size()
        val total = fixed + inspection + urgent

        val cal = java.util.Calendar.getInstance().apply {
            val m = get(java.util.Calendar.MONTH)
            val startMonth = if (m < 6) java.util.Calendar.JANUARY else java.util.Calendar.JULY
            set(java.util.Calendar.MONTH, startMonth)
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val sinceTs = Timestamp(cal.time)

        val semSnap = coll.whereGreaterThan("updatedAt", sinceTs).get().await()
        val solvedSemester = semSnap.documents.count { it.getString("status") == "operativo" }

        Result.success(
            ReportStats(
                total = total,
                fixed = fixed,
                inspection = inspection,
                urgent = urgent,
                solvedSemester = solvedSemester
            )
        )
    } catch (e: Exception) {
        Log.e(TAG, "Error getReportStats", e)
        Result.failure(e)
    }

    /* ------------------- Mapper: Document -> UI ------------------- */
    private fun DocumentSnapshot.toUi(): ReportesUiModel {
        val data = data ?: emptyMap<String, Any?>()
        val geo = data["geo"] as? Map<*, *>
        val lat = (geo?.get("lat") as? Number)?.toDouble()
        val lng = (geo?.get("lng") as? Number)?.toDouble()
        @Suppress("UNCHECKED_CAST")
        val urls = (data["photoUrls"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

        return ReportesUiModel(
            id = id,
            reporterUid = data["reporterUid"] as? String ?: "",
            role = data["role"] as? String ?: "",
            status = data["status"] as? String ?: "operativo",
            address = data["address"] as? String ?: "",
            description = data["description"] as? String ?: "",
            lat = lat,
            lng = lng,
            photoUrls = urls,
            photosCount = (data["photosCount"] as? Number)?.toLong() ?: 0L,
            createdAt = data["createdAt"] as? Timestamp,
            updatedAt = data["updatedAt"] as? Timestamp
        )
    }
}
