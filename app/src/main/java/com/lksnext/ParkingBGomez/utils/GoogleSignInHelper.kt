package com.lksnext.ParkingBGomez.utils

import android.content.Context
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.lksnext.ParkingBGomez.data.DataRepository
import com.lksnext.ParkingBGomez.domain.Callback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class GoogleSignInHelper(private val context: Context) {

    private val CLIENT_ID = "316056691670-rfk3d8t4e3k8vj3vppebcjtiujguggmf.apps.googleusercontent.com"
    private val TAG = "GoogleSignInHelper"

    fun signInWithGoogle(callback: Callback) {
        val credentialManager = CredentialManager.create(context)
        var googleIdOption: GetGoogleIdOption

        try {
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray(StandardCharsets.UTF_8)
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.joinToString("") { "%02x".format(it) }

            googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(CLIENT_ID)
                .setNonce(hashedNonce)
                .build()
        } catch (e: NoSuchAlgorithmException) {
            googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(CLIENT_ID)
                .build()
        }

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                val googleAuthCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                DataRepository.getInstance().signInWithCredential(googleAuthCredential, callback)

            }catch (e: GetCredentialException){
                Log.e(TAG, "Google Sign In Failed: ${e.message}", e)
                callback.onFailure()
            }catch (e: GoogleIdTokenParsingException){
                Log.e(TAG, "Google Sign In Failed: ${e.message}", e)
                callback.onSuccess()
            }

        }
    }

}
