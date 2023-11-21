package org.wit.macrocount.models
import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import org.wit.macrocount.helpers.exists
import org.wit.macrocount.helpers.read
import org.wit.macrocount.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import java.util.Random

const val USER_JSON_FILE = "users.json"

var currentUser = UserModel()


val userGsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()

val userListType: Type = object : TypeToken<ArrayList<UserModel>>() {}.type

fun genRandomId(): Long {
    return Random().nextLong()
}

class UserJSONStore(private val context: Context): UserStore {

    var users = mutableListOf<UserModel>()

    init {
        if (exists(context, USER_JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<UserModel> {
        Timber.i("Finding all users")
        logAll()
        return users
    }

//    override fun getUser(user: UserModel): UserModel {
//        var foundUser: UserModel? = users.find { m -> m.id == user.id }
//    }

    override fun create(user: UserModel) {
        Timber.i("Creating user")
        users.add(user)
        serialize()
    }

    override fun logIn(user: UserModel): Boolean {
        Timber.i("Logging in user: $user")
        var foundUser: UserModel? = users.find { u -> u.email == user.email}
        return foundUser != null && foundUser.password == user.password
    }

    override fun findById(id: Long?): UserModel? {
        Timber.i("Finding user by id: $id")
        return users.find { u -> u.id == id }
    }

    override fun signUp(user: UserModel) {
        Timber.i("Signing up user: $user")
        this.create(user)

        logIn(user)
    }

    override fun update(user: UserModel) {
        var foundUser: UserModel? = users.find { m -> m.id == user.id }
        Timber.i("Updating user: $foundUser")
        if (foundUser != null) {
            foundUser.name = user.name
            foundUser.gender = user.gender
            foundUser.weight = user.weight
            foundUser.dob = user.dob
            foundUser.email = user.email
            foundUser.password = user.password

            serialize()
            Timber.i("Updated user: $user")
            //logAll()
        }
    }

    override fun delete(user: UserModel) {
        var foundUser: UserModel? = users.find { u -> u.id == user.id }
        if (foundUser != null) {
            users.remove(user)
        }
        serialize()
    }

//    override fun setCurrentUser(user: UserModel){
//        Timber.i("Setting current user: $user")
//        currentUser = user.copy()
//    }
//
//    override fun getCurrentUser(): UserModel {
//        Timber.i("Getting current user: $currentUser")
//        return currentUser
//    }

    private fun serialize() {
        val jsonString = userGsonBuilder.toJson(users, userListType)
        write(context, USER_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, USER_JSON_FILE)
        users = userGsonBuilder.fromJson(jsonString, userListType)
    }

    private fun logAll() {
        users.forEach { Timber.i("$it") }
    }
}

