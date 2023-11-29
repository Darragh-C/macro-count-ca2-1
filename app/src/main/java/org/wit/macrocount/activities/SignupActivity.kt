package org.wit.macrocount.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.databinding.ActivitySignUpBinding
import org.wit.macrocount.fragments.UserFragment
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.models.generateRandomId
import timber.log.Timber
import timber.log.Timber.Forest.i

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

                val navController = findNavController(R.id.nav_host_fragment)
                fun navigateToUserFragment() {
                    i("navigating to user profile fragment")
                    navController.navigate(R.id.userFragment)
                }
                navigateToUserFragment()

//                val fragment = UserFragment()
//                val args = Bundle()
//                args.putParcelable("user_signup", user)
//                fragment.arguments = args
//
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.nav_host_fragment, fragment)
//                    .commit()

//                val intent = Intent(this, UserFragment::class.java)
//                intent.putExtra("user_signup", user)
//                startActivity(intent)
            }
        }
    }

    fun onLinkClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}