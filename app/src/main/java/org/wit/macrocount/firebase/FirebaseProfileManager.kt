package org.wit.macrocount.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.wit.macrocount.models.DayModel
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserStore
import retrofit2.Callback
import timber.log.Timber
import java.time.LocalDate

object FirebaseProfileManager: UserStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun findById(userid: String, user: MutableLiveData<UserModel>) {

        database.child("user-profiles").child(userid)
            .get().addOnSuccessListener {
                user.value = it.getValue(UserModel::class.java)
                Timber.i("firebase Got user profile it.value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: FirebaseUser, user: UserModel, callback: (Boolean) -> Unit) {
        Timber.i("Creating user profile FirebaseMacroManager : ${database}")

        val uid = firebaseUser.uid
        val key = database.child("user-profiles").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        user.uid = key
        val userValues = user.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/user-profiles/$key"] = userValues

        database.updateChildren(childAdd)
        callback(true)
    }

    override fun update(userid: String, user: UserModel) {
        val userValues = user.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["user-profiles/$userid/"] = userValues

        database.updateChildren(childUpdate)
    }

    fun snapshotCheck(userid: String, callback: (Boolean) -> Unit) {
        Timber.i("Checking user-profiles snapshot for user $userid")

        val userDaysRef = FirebaseDayManager.database.child("user-profiles").child(userid)

        userDaysRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase user-profiles error: ${error.message}")
                callback(false)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.i("Snapshot : $snapshot")

                if (snapshot.exists()) {
                    Timber.i("Snapshot exists")
                    callback(true)
                } else {
                    Timber.i("Snapshot does not exist, creating user-profile")

                    // Create a new user-profile for the current user
                    val newProfileModel = UserModel()
                    newProfileModel.uid = firebaseAuth.currentUser!!.uid

                    // Use the create function to store the new user-profile
                    Timber.i("creating user-profile with newProfileModel: $newProfileModel")
                    create(
                        firebaseAuth.currentUser!!,
                        newProfileModel
                    ) { createdResult ->
                        if (createdResult) {
                            Timber.i("Created user-profile node")
                            callback(true)
                        } else {
                            Timber.i("Error creating day")
                            callback(false)
                        }
                    }
                }
            }
        })
    }

}