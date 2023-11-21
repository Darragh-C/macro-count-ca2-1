package org.wit.macrocount.models
import timber.log.Timber.Forest.i
class UserMemStore: UserStore {

    val users = ArrayList<UserModel>()

    override fun findAll(): List<UserModel> {
        return users
    }

    override fun create(user: UserModel) {
        users.add(user)
    }

    override fun signUp(user: UserModel) {
        this.create(user)
        logIn(user)
    }

    override fun logIn(user: UserModel): Boolean {
        var foundUser: UserModel? = users.find { u -> u.email == user.email}
        if (foundUser != null && foundUser.password == user.password) {
            return true
        } else {
            return false
        }
    }

    override fun findById(id: Long?): UserModel? {
        return users.find { u -> u.id == id }
    }

    override fun update(user: UserModel) {
        var foundUser: UserModel? = users.find { u -> u.id == user.id }
        if (foundUser != null) {
            foundUser.name = user.name
            foundUser.gender = foundUser.gender
            foundUser.weight = foundUser.weight
            foundUser.dob = foundUser.dob

            logAll()
        }
    }

    override fun delete(user: UserModel) {
        var foundUserModel: UserModel? = users.find { u -> u.id == user.id }
        if (foundUserModel != null) {
            users.remove(user)
        }
    }

//    override fun setCurrentUser(user: UserModel){
//        currentUser = user.copy()
//    }
//
//    override fun getCurrentUser(): UserModel {
//        return currentUser
//    }

    fun logAll() {
        users.forEach{ i("${it}") }
    }

}