package org.wit.macrocount.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.activities.Home
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.databinding.ActivityLogInBinding
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException


class LoginActivity: AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityLogInBinding
    private lateinit var userRepo: UserRepo
    //private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginRegisterViewModel : LoginRegisterViewModel
    private lateinit var startForResult : ActivityResultLauncher<Intent>

//    var user = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        userRepo = UserRepo(applicationContext)

        //loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        Timber.i("Log in started..")

        binding.btnLogin.setOnClickListener {
            signIn(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
        }

        binding.btnSignup.setOnClickListener() {
            createAccount(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
        }

        binding.googleSignInButton.setSize(SignInButton.SIZE_WIDE)
        binding.googleSignInButton.setColorScheme(0)

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        loginRegisterViewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)
        loginRegisterViewModel.liveFirebaseUser.observe(this, Observer
        { firebaseUser -> if (firebaseUser != null)
            startActivity(Intent(this, Home::class.java)) })

        loginRegisterViewModel.firebaseAuthManager.errorStatus.observe(this, Observer
        { status -> checkStatus(status) })

        setupGoogleSignInCallback()

        binding.googleSignInButton.setOnClickListener { googleSignIn() }
    }

    //Required to exit app from Login Screen - must investigate this further
    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"Click again to Close App...",Toast.LENGTH_LONG).show()
        finish()
    }

    private fun createAccount(email: String, password: String) {
        Timber.d("createAccount:$email")
        if (!validateForm()) { return }

        loginRegisterViewModel.register(email,password)
    }

    private fun signIn(email: String, password: String) {
        Timber.d("signIn:$email")
        if (!validateForm()) { return }

        loginRegisterViewModel.login(email,password)
    }

    private fun checkStatus(error:Boolean) {
        if (error)
            Toast.makeText(this,
                getString(R.string.auth_failed),
                Toast.LENGTH_LONG).show()
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.loginEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.loginEmail.error = "Required."
            valid = false
        } else {
            binding.loginEmail.error = null
        }

        val password = binding.loginPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.loginPassword.error = "Required."
            valid = false
        } else {
            binding.loginPassword.error = null
        }
        return valid
    }

    private fun googleSignIn() {
        val signInIntent = loginRegisterViewModel.firebaseAuthManager
            .googleSignInClient.value!!.signInIntent

        startForResult.launch(signInIntent)
    }

    private fun setupGoogleSignInCallback() {
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            val account = task.getResult(ApiException::class.java)
                            loginRegisterViewModel.authWithGoogle(account!!)
                        } catch (e: ApiException) {
                            // Google Sign In failed
                            Timber.i( "Google sign in failed $e")
                            Snackbar.make(binding.loginLayout, "Authentication Failed.",
                                Snackbar.LENGTH_SHORT).show()
                        }
                        Timber.i("DonationX Google Result $result.data")
                    }
                    RESULT_CANCELED -> {

                    } else -> { }
                }
            }
    }

}

//
//    fun onLinkClick(view: View) {
//        val intent = Intent(this, SignupActivity::class.java)
//        startActivity(intent)
//    }



//user.email = binding.loginEmail.text.toString()
//user.password = binding.loginPassword.text.toString()
//
//val validationChecks = listOf(
//    Pair(user.email.isEmpty(), R.string.snackbar_userEmail),
//    Pair(user.password.isEmpty(), R.string.snackbar_userPassword)
//)
//
//var validationFailed = false
//
//for (check in validationChecks) {
//    if (check.first) {
//        Snackbar
//            .make(it, check.second, Snackbar.LENGTH_LONG)
//            .show()
//        validationFailed = true
//        break
//    }
//}
//
//if (!validationFailed) {
//
//    Timber.i("Checking user for log in: $user")
//    loginViewModel.login(user)
//
////                //var loggedInUser = app.users.logIn(user)
//
//    if (loginViewModel.observableStatus.value == true) {
//        Timber.i("Logged in?: ${loginViewModel.observableUser.value}")
//        userRepo.userId = loginViewModel.observableUser.value?.id.toString()
//        Timber.i("Current user: ${loginViewModel.observableUser.value}")
//        val intent = Intent(this, Home::class.java)
//        startActivity(intent)
//    } else {
//        Snackbar
//            .make(it, R.string.snackbar_invalidLogin, Snackbar.LENGTH_LONG)
//            .show()
//    }
//    Timber.i("user at sign up intent: $user")
//}
//
//}