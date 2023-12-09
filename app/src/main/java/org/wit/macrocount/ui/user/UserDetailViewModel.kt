package org.wit.macrocount.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.UserManager
import org.wit.macrocount.models.UserModel
import timber.log.Timber

class UserDetailViewModel : ViewModel() {

    private val user = MutableLiveData<UserModel>()

    val observableUser: LiveData<UserModel>
        get() = user

    fun getUser(id: Long) {
        user.value = UserManager.findById(id)
    }

}