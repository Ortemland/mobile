package com.screentime.reward.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val Context.linkDataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "device_link")
        private val IS_LINKED_KEY = booleanPreferencesKey("is_linked")
        private val FAMILY_ID_KEY = androidx.datastore.preferences.core.stringPreferencesKey("family_id")
    }
    
    private val dataStore = context.linkDataStore
    
    fun isLinkedFlow(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LINKED_KEY] ?: false
        }
    }
    
    suspend fun setLinked(isLinked: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LINKED_KEY] = isLinked
        }
    }
    
    fun getFamilyIdFlow(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[FAMILY_ID_KEY]
        }
    }
    
    suspend fun setFamilyId(familyId: String) {
        dataStore.edit { preferences ->
            preferences[FAMILY_ID_KEY] = familyId
        }
    }
}

