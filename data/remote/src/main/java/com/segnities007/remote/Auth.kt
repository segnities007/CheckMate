package com.segnities007.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val TAG = "Auth"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Google OAuth認証と、DataStoreによるセッション管理を統合したクラス。
 * @param context アプリケーションコンテキスト
 */
class Auth(private val context: Context) {

    private val credentialManager: CredentialManager = CredentialManager.create(context)

    suspend fun checkIsLoggedIn(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] != null
        }.first()
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun loginWithGoogle() {
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption())
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            handleSignIn(result)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google login failed or was cancelled.", e)
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        // 取得したIDとトークンを保存する
                        saveUserSession(
                            userId = googleIdTokenCredential.id,
                            idToken = googleIdTokenCredential.idToken
                        )

                        Log.d(TAG, "Google ID Token successfully received and saved.")

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Failed to parse Google ID Token.", e)
                    }
                } else {
                    Log.e(TAG, "Received an unexpected CustomCredential type: ${credential.type}")
                }
            }

            else -> {
                Log.e(TAG, "Received an unexpected credential type.")
            }
        }
    }

    private suspend fun saveUserSession(userId: String, idToken: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[ID_TOKEN_KEY] = idToken
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun googleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .setNonce(Uuid.random().toString())
            .build()
    }

    private companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val ID_TOKEN_KEY = stringPreferencesKey("id_token")
    }
}