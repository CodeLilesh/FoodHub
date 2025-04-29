package com.example.foodorderingapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodorderingapp.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Context to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFERENCES_NAME
)

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Keys for the preferences
    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey(Constants.AUTH_TOKEN_KEY)
        val USER_ID = stringPreferencesKey(Constants.USER_ID_KEY)
        val USER_EMAIL = stringPreferencesKey(Constants.USER_EMAIL_KEY)
        val USER_NAME = stringPreferencesKey(Constants.USER_NAME_KEY)
    }
    
    // Save authentication token
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
        }
    }
    
    // Get authentication token
    fun getAuthToken(): String? {
        return runCatching {
            val preferences = context.dataStore.data.map { preferences ->
                preferences[PreferencesKeys.AUTH_TOKEN]
            }
            preferences.toString()
        }.getOrNull()
    }
    
    // Get authentication token as Flow
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]
    }
    
    // Save user details
    suspend fun saveUserDetails(userId: String, email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }
    
    // Get user ID as Flow
    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID]
    }
    
    // Get user email as Flow
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL]
    }
    
    // Get user name as Flow
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME]
    }
    
    // Check if user is logged in
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        !preferences[PreferencesKeys.AUTH_TOKEN].isNullOrEmpty()
    }
    
    // Clear session (logout)
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}