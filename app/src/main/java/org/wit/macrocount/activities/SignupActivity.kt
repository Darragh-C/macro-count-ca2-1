package org.wit.macrocount.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.databinding.ActivitySignUpBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.models.generateRandomId
import timber.log.Timber

class SignupActivity: AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivitySignUpBinding

    var user = UserModel()
    private lateinit var userRepo: UserRepo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        userRepo = UserRepo(applicationContext)

        Timber.i("Sign up started..")

        binding.btnSignup.setOnClickListener() {

            user.email = binding.signupEmail.text.toString()
            user.password = binding.signupPassword.text.toString()

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

                Timber.i("User added: $user.email")
                user.id = generateRandomId()
                app.users.create(user.copy())
                userRepo.userId = user.id.toString()

                Timber.i("user at sign up intent: $user")

                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("user_signup", user)
                startActivity(intent)
            }
        }
    }

    fun onLinkClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}