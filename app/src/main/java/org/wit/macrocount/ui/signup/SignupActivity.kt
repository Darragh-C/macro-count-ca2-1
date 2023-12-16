package org.wit.macrocount.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.databinding.ActivitySignUpBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
//import org.wit.macrocount.models.generateRandomId
import org.wit.macrocount.ui.login.LoginActivity
import timber.log.Timber
import timber.log.Timber.Forest.i

class SignupActivity: AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signupViewModel: SignupViewModel
    var user = UserModel()
    private lateinit var userRepo: UserRepo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        userRepo = UserRepo(applicationContext)

        signupViewModel = ViewModelProvider(this).get(SignupViewModel::class.java)

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
                //user.id = generateRandomId()
                user.id = 123456L
                //app.users.create(user.copy())
                signupViewModel.addUser(user.copy())
                userRepo.userId = user.id.toString()

                if (signupViewModel.observableStatus.value == true) {
                    Timber.i("signed up user: ${signupViewModel.observableStatus.value}")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun onLinkClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}