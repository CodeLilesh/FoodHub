package com.example.foodorderingapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.foodorderingapp.data.models.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()
    
    companion object {
        private const val PREF_NAME = "FoodOrderingAppPrefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
    
    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }
    
    /**
     * Function to get auth token
     */
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Function to save user information
     */
    fun saveUserInfo(userId: Int, name: String, email: String) {
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }
    
    /**
     * Function to get user ID
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, Constants.DEFAULT_USER_ID)
    }
    
    /**
     * Function to get user name
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    /**
     * Function to get user email
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Function to check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    /**
     * Function to clear session
     */
    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
