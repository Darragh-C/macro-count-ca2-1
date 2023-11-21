package org.wit.macrocount.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.databinding.ActivityLogInBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.currentUser
import org.wit.macrocount.models.generateRandomId
import timber.log.Timber



class LoginActivity: AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityLogInBinding

    var user = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

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

                Timber.i("Checking user for log in: $user.email")

                var loggedInUser = app.users.logIn(user)
                if (loggedInUser) {
                    Timber.i("currentUser: $currentUser")
                    val intent = Intent(this, MacroCountListActivity::class.java)
                    intent.putExtra("user_login", currentUser)
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