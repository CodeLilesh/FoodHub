package com.example.foodorderingapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodorderingapp.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension property for context to create a DataStore instance
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCES_NAME)

/**
 * SessionManager is responsible for managing user session data in DataStore
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey(Constants.AUTH_TOKEN_KEY)
        private val USER_ID = stringPreferencesKey(Constants.USER_ID_KEY)
    }

    /**
     * Get the auth token as a flow
     */
    fun getAuthTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }
    }

    /**
     * Get the user ID as a flow
     */
    fun getUserIdFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    /**
     * Get the auth token synchronously (not recommended for UI thread)
     * Should be used with runBlocking or in a repository method
     */
    suspend fun getAuthToken(): String? {
        var token: String? = null
        context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }.collect {
            token = it
        }
        return token
    }

    /**
     * Get the user ID synchronously (not recommended for UI thread)
     * Should be used with runBlocking or in a repository method
     */
    suspend fun getUserId(): String? {
        var userId: String? = null
        context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }.collect {
            userId = it
        }
        return userId
    }

    /**
     * Save auth token to DataStore
     */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    /**
     * Save user ID to DataStore
     */
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    /**
     * Clear all session data
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}