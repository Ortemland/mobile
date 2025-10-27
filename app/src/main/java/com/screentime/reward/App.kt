package com.screentime.reward

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Отключаем Firebase Messaging для избежания ошибок инициализации
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                // Не обрабатываем ошибку
            }
        } catch (e: Exception) {
            // Firebase Messaging недоступен - это нормально
        }
    }
}

