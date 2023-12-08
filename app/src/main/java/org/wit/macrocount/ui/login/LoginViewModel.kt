package org.wit.macrocount.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.UserManager
import org.wit.macrocount.models.UserModel

class LoginViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()
    private val user = MutableLiveData<UserModel>()

    val observableStatus: LiveData<Boolean>
        get() = status

    val observableUser: LiveData<UserModel>
        get() = user

    fun login(user: UserModel) {
        status.value = try {
            val signedInUser = UserManager.logIn(user)
            this.user.value = signedInUser
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}