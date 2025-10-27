package com.screentime.reward.data.firebase

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
class FirebaseSyncRepository @Inject constructor() {
    
    val db: FirebaseFirestore = Firebase.firestore
    
    // Генерация случайного кода связки (6 цифр)
    fun generateConnectionCode(): String {
        return (100000..999999).random().toString()
    }
    
    // Создать семью (взрослый создает)
    suspend fun createFamily(connectionCode: String, onSuccess: (String) -> Unit = {}) {
        val familyId = db.collection("families").document().id
        val familyLink = FamilyLink(
            familyId = familyId,
            adultDeviceId = getCurrentDeviceId(),
            childDeviceId = null,
            connectionCode = connectionCode,
            isActive = false
        )
        
        db.collection("families")
            .document(familyId)
            .set(familyLink)
            .await()
        
        onSuccess(familyId)
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
    
    // Получить текущий ID устройства (упрощенная версия)
    private fun getCurrentDeviceId(): String {
        // В реальном приложении используй Settings.Secure.ANDROID_ID
        return java.util.UUID.randomUUID().toString()
    }
}
