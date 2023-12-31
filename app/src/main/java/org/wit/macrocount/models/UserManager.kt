//package org.wit.macrocount.models
//import timber.log.Timber
//import timber.log.Timber.Forest.i
//object UserManager: UserStore {
//
//    val users = ArrayList<UserModel>()
//
//    override fun findAll(): List<UserModel> {
//        return users
//    }
//
//    override fun create(user: UserModel) {
//        users.add(user)
//    }
//
//    override fun signUp(user: UserModel) {
//        this.create(user)
//        logIn(user)
//    }
//
//    override fun logIn(user: UserModel): UserModel {
//        var foundUser: UserModel? = users.find { u -> u.email == user.email}
//        if (foundUser != null && foundUser.password == user.password) {
//            Timber.i("Logged in user: $foundUser")
//            return foundUser
//        } else {
//            Timber.i("User not found")
//            return UserModel()
//        }
//    }
//
//    override fun findById(id: Long?): UserModel? {
//        val user = users.find { u -> u.id == id }
//        i("user found findById: $user")
//        return user
//    }
//
//    override fun update(user: UserModel) {
//        var foundUser: UserModel? = users.find { u -> u.id == user.id }
//        if (foundUser != null) {
//            foundUser.name = user.name
//            foundUser.gender = foundUser.gender
//            foundUser.weight = foundUser.weight
//            foundUser.dob = foundUser.dob
//
//            logAll()
//        }
//    }
//
//    override fun delete(user: UserModel) {
//        var foundUserModel: UserModel? = users.find { u -> u.id == user.id }
//        if (foundUserModel != null) {
//            users.remove(user)
//        }
//    }
//
////    override fun setCurrentUser(user: UserModel){
////        currentUser = user.copy()
////    }
////
////    override fun getCurrentUser(): UserModel {
////        return currentUser
////    }
//
//    fun logAll() {
//        users.forEach{ i("${it}") }
//    }
//
//}