package org.wit.macrocount.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserManager
import org.wit.macrocount.models.UserModel

class SignupViewModel : ViewModel() {
    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addUser(user: UserModel) {
        status.value = try {
            UserManager.create(user)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}