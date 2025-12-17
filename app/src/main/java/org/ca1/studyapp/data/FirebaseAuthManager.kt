package org.ca1.studyapp.data

/** References:
 * https://firebase.google.com/docs/auth/android/google-signin
 * https://github.com/Ashwin29/GoogleSignIn_Kotlin/blob/master/app/src/main/java/com/winision/googlesignin/MainActivity.kt
 * https://www.youtube.com/watch?v=H_maapn4Q3Q
 *
 **/
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.ca1.studyapp.R

class FirebaseAuthManager(context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    init {
        val clientId = context.getString(R.string.google_sign_in_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    fun signInWithGoogle(
        idToken: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure("User is null after authentication")
                    }
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Google sign-in failed"
                    onFailure(errorMessage)
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Firebase error: ${exception.localizedMessage}")
            }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            onComplete()
        }
    }
}
