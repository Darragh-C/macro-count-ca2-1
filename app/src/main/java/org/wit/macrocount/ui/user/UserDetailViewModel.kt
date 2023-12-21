package org.wit.macrocount.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.firebase.FirebaseProfileManager
import org.wit.macrocount.models.UserModel
import timber.log.Timber

class UserDetailViewModel : ViewModel() {

    private val user = MutableLiveData<UserModel>()
    val currentUser = FirebaseAuth.getInstance().currentUser!!

    val observableUser: LiveData<UserModel>
        get() = user

    var snapshotCheck = MutableLiveData<Boolean>()

    init {
        Timber.i("init block called")

        if (currentUser != null) {
            FirebaseProfileManager.snapshotCheck(currentUser.uid) { result ->
                if (result) {
                    loadProfile(currentUser.uid)
                    Timber.i("snapshot true called")
                    snapshotCheck.value = true
                } else {
                    Timber.i("snapshot failed at init block")
                }
            }
        } else {
            Timber.i("User not authenticated")
        }
    }

    fun loadProfile(userid: String) {
        Timber.i("loading profile for $userid")
        FirebaseProfileManager.findById(userid) {
            user.value = it
            Timber.i("user.value = $user")
        }
    }

}