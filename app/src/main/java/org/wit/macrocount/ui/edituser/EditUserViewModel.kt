package org.wit.macrocount.ui.edituser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.firebase.FirebaseDayManager
import org.wit.macrocount.firebase.FirebaseProfileManager
import org.wit.macrocount.models.UserModel
import timber.log.Timber

class EditUserViewModel: ViewModel() {

    private val status = MutableLiveData<Boolean>()
    private val user = MutableLiveData<UserModel>()

    private val userid = FirebaseAuth.getInstance().currentUser!!.uid

    val seekbarMin = 0
    val seekbarMax = 500

    val observableStatus: LiveData<Boolean>
        get() = status

    val observableUser: LiveData<UserModel>
        get() = user

    fun editUser(userModel: UserModel) {
        user.value = userModel
    }

    var snapshotCheck = MutableLiveData<Boolean>()

    val observableSnapshotCheck: MutableLiveData<Boolean>
        get() = snapshotCheck

    init {
        Timber.i("init block called")
        val currentUser = FirebaseAuth.getInstance().currentUser

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
        FirebaseProfileManager.findById(userid, user)
    }

    fun saveUser() {
        status.value = try {
            FirebaseProfileManager.update(userid, user.value!!)
            true
        } catch (e: IllegalArgumentException) {
            Timber.i("Error updating user: $e")
            false
        }
    }

    fun getUser(id: Long) {
         FirebaseProfileManager.findById(userid, user)
    }

    fun setGoal(string: String) {
        user.value!!.goal = string
    }

    fun setGender(string: String) {
        user.value!!.gender = string
    }

    fun setAge(string: String) {
        user.value!!.age = string
    }

    fun setHeight(string: String) {
        user.value!!.height = string
    }

    fun setWeight(string: String) {
        user.value!!.weight = string
    }

    fun setDob(string: String) {
        user.value!!.dob = string
    }

    fun setName(string: String) {
        user.value!!.name = string
    }

}