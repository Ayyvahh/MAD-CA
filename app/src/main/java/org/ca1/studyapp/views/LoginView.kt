package org.ca1.studyapp.views
/*References:
I used the below repos to help me implement google sign in in my project
* https://github.com/Ashwin29/GoogleSignIn_Kotlin/blob/master/app/src/main/java/com/winision/googlesignin/MainActivity.kt
* https://github.com/firebase/quickstart-android/blob/master/database/app/src/main/java/com/google/firebase/quickstart/database/kotlin/SignInFragment.kt
*
**/
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import org.ca1.studyapp.databinding.ActivityLoginBinding
import org.ca1.studyapp.main.MainApp

class LoginView : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        if (app.authManager.isUserSignedIn()) {
            app.store.initForUser()
            navigateToTaskList()
            return
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = app.authManager.getGoogleSignInClient().signInIntent
        startActivityForResult(signInIntent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account == null || account.idToken == null) {
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_LONG).show()
                    return
                }
                app.authManager.signInWithGoogle(
                    idToken = account.idToken!!,
                    onSuccess = {
                        app.store.initForUser()
                        navigateToTaskList()
                    },
                    onFailure = {
                        Toast.makeText(this, "Sign-in failed", Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToTaskList() {
        val intent = Intent(this, TaskListView::class.java)
        startActivity(intent)
        finish()
    }
}
