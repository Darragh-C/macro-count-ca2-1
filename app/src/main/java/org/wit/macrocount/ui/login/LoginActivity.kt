package org.wit.macrocount.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.activities.Home
import org.wit.macrocount.ui.signup.SignupActivity
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.databinding.ActivityLogInBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber



class LoginActivity: AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityLogInBinding
    private lateinit var userRepo: UserRepo
    private lateinit var loginViewModel: LoginViewModel

    var user = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        userRepo = UserRepo(applicationContext)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        Timber.i("Log in started..")

        binding.btnLogin.setOnClickListener {

            user.email = binding.loginEmail.text.toString()
            user.password = binding.loginPassword.text.toString()

            val validationChecks = listOf(
                Pair(user.email.isEmpty(), R.string.snackbar_userEmail),
                Pair(user.password.isEmpty(), R.string.snackbar_userPassword)
            )

            var validationFailed = false

            for (check in validationChecks) {
                if (check.first) {
                    Snackbar
                        .make(it, check.second, Snackbar.LENGTH_LONG)
                        .show()
                    validationFailed = true
                    break
                }
            }

            if (!validationFailed) {

                Timber.i("Checking user for log in: $user")
                loginViewModel.login(user)

//                //var loggedInUser = app.users.logIn(user)

                if (loginViewModel.observableStatus.value == true) {
                    Timber.i("Logged in?: ${loginViewModel.observableUser.value}")
                    userRepo.userId = loginViewModel.observableUser.value?.id.toString()
                    Timber.i("Current user: ${loginViewModel.observableUser.value}")
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                } else {
                    Snackbar
                        .make(it, R.string.snackbar_invalidLogin, Snackbar.LENGTH_LONG)
                        .show()
                }
                Timber.i("user at sign up intent: $user")
            }

        }
    }

    fun onLinkClick(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

}