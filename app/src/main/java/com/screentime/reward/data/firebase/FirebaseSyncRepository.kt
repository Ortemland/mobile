package com.screentime.reward.data.firebase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.screentime.reward.domain.model.FamilyLink
import com.screentime.reward.domain.model.PendingApproval
import com.screentime.reward.domain.model.DeviceRole
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncRepository @Inject constructor(
    private val context: Context
) {
    
    val db: FirebaseFirestore = Firebase.firestore
    
    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
    }
    
    // Генерация случайного кода связки (6 цифр)
    fun generateConnectionCode(): String {
        return (100000..999999).random().toString()
    }
    
    // Создать семью (взрослый создает) - возвращает familyId
    suspend fun createFamilySync(connectionCode: String): String {
        val deviceId = getCurrentDeviceId()
        android.util.Log.d("FirebaseSync", "Current device ID: $deviceId")
        
        val familyId = db.collection("families").document().id
        val familyLink = FamilyLink(
            familyId = familyId,
            adultDeviceId = deviceId,
            childDeviceId = null,
            connectionCode = connectionCode,
            isActive = false
        )
        
        android.util.Log.d("FirebaseSync", "Creating family with ID: $familyId, code: $connectionCode")
        
        db.collection("families")
            .document(familyId)
            .set(familyLink)
            .await()
        
        android.util.Log.d("FirebaseSync", "Family created successfully")
        
        return familyId
    }
    
    // Присоединиться к семье (ребенок вводит код)
    suspend fun joinFamily(connectionCode: String): Boolean {
        val query = db.collection("families")
            .whereEqualTo("connectionCode", connectionCode)
            .limit(1)
            .get()
            .await()
        
        if (query.isEmpty) {
            return false
        }
        
        val familyDoc = query.documents.first()
        val family = familyDoc.toObject(FamilyLink::class.java)
            ?: return false
        
        if (family.childDeviceId != null) {
            return false // Уже есть ребенок
        }
        
        // Обновляем связь, добавляя ID ребенка
        familyDoc.reference.update(
            mapOf(
                "childDeviceId" to getCurrentDeviceId(),
                "isActive" to true
            )
        ).await()
        
        return true
    }
    
    // Отправить задачу на утверждение в Firebase
    suspend fun submitTaskForApproval(task: PendingApproval, familyId: String) {
        db.collection("families")
            .document(familyId)
            .collection("pendingApprovals")
            .document(task.taskId)
            .set(task)
            .await()
    }
    
    // Утвердить задачу из Firebase
    suspend fun approveTask(taskId: String, familyId: String) {
        db.collection("families")
            .document(familyId)
            .collection("pendingApprovals")
            .document(taskId)
            .update("status", "approved")
            .await()
    }
    
    // Отклонить задачу из Firebase
    suspend fun rejectTask(taskId: String, familyId: String) {
        db.collection("families")
            .document(familyId)
            .collection("pendingApprovals")
            .document(taskId)
            .update("status", "rejected")
            .await()
    }
    
    // Получить все заявки на утверждение
    suspend fun getPendingApprovals(familyId: String): List<PendingApproval> {
        val query = db.collection("families")
            .document(familyId)
            .collection("pendingApprovals")
            .whereEqualTo("status", "pending")
            .get()
            .await()
        
        return query.documents.mapNotNull { 
            it.toObject(PendingApproval::class.java) 
        }
    }
    
    // Получить текущий ID устройства - постоянный для этого устройства
    private fun getCurrentDeviceId(): String {
        val deviceIdKey = "device_id"
        
        val deviceId = sharedPrefs.getString(deviceIdKey, null) ?: run {
            val newDeviceId = java.util.UUID.randomUUID().toString()
            sharedPrefs.edit().putString(deviceIdKey, newDeviceId).apply()
            android.util.Log.d("FirebaseSync", "Generated new device ID: $newDeviceId")
            newDeviceId
        }
        
        android.util.Log.d("FirebaseSync", "Using device ID: $deviceId")
        return deviceId
    }
}
