package com.screentime.reward.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.screentime.reward.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val BASE_TIME_HOURS_KEY = intPreferencesKey("base_time_hours")
        private val CURRENT_ROLE_KEY = stringPreferencesKey("current_role")
    }
    
    private val dataStore = context.dataStore
    
    suspend fun getBaseTimeHours(): Int {
        return dataStore.data.first().let { preferences ->
            preferences[BASE_TIME_HOURS_KEY] ?: 2
        }
    }
    
    fun getBaseTimeHoursFlow(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[BASE_TIME_HOURS_KEY] ?: 2
        }
    }
    
    suspend fun setBaseTimeHours(hours: Int) {
        dataStore.edit { preferences ->
            preferences[BASE_TIME_HOURS_KEY] = hours
        }
    }
    
    fun getCurrentRoleFlow(): Flow<UserRole> {
        return dataStore.data.map { preferences ->
            val roleString = preferences[CURRENT_ROLE_KEY] ?: "CHILD"
            when (roleString) {
                "CHILD" -> UserRole.CHILD
                "ADULT" -> UserRole.ADULT
                else -> UserRole.CHILD
            }
        }
    }
    
    suspend fun setCurrentRole(role: UserRole) {
        dataStore.edit { preferences ->
            preferences[CURRENT_ROLE_KEY] = when (role) {
                UserRole.CHILD -> "CHILD"
                UserRole.ADULT -> "ADULT"
            }
        }
    }
}
