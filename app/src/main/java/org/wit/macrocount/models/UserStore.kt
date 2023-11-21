package org.wit.macrocount.models

interface UserStore {
    fun findAll(): List<UserModel>
    fun create(user: UserModel)
    fun update(user: UserModel)
    fun delete(user: UserModel)
    fun logIn(user: UserModel): Boolean
    fun signUp(user: UserModel)
    fun findById(id: Long?): UserModel?
//    fun setCurrentUser(user: UserModel)
//    fun getCurrentUser(): UserModel
}