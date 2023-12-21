package org.wit.macrocount.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface UserStore {

    fun create(firebaseUser: FirebaseUser, user: UserModel, callback: (Boolean) -> Unit)
    fun findById(userid: String, callback: (UserModel?) -> Unit)
    fun update(userid: String, user: UserModel)
    fun addFavourite(macroId: String, firebaseUser: FirebaseUser)
    fun removeFavourite(macroId: String, firebaseUser: FirebaseUser)
//    fun findAll(): List<UserModel>
//    fun create(user: UserModel)
//    fun update(user: UserModel)
//    fun delete(user: UserModel)
//    fun logIn(user: UserModel): UserModel?
//    fun signUp(user: UserModel)
//    fun findById(id: Long?): UserModel?
//    fun setCurrentUser(user: UserModel)
//    fun getCurrentUser(): UserModel
}